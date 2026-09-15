#!/bin/bash
# ============================================================================
# 视频权益项目 → https://xs2026.site 网关接入补丁
#
# 背景：xs2026.site 的 80/443 由**另一个项目** mini-novel 的网关容器
#       （mini-novel-gateway，配置 /opt/mini-h5/deploy/gateway/nginx.conf）持有。
#       本项目 video-* 容器原本只按 IP:端口 暴露，未接入该域名。
#
# 本脚本做三件事（全部为**新增**，不改动 mini-novel 任何现有路由/容器/数据）：
#   1) 在网关 443 server 块内插入 video-entitlement 接入块，新增 4 条 location：
#        /platform/    -> video-frontend:80   （管理后台 SPA）
#        /downloads/   -> video-frontend:80   （APK 下载目录）
#        /admin/api/   -> video-backend:8080  （管理后台接口）
#        /api/v1/      -> video-backend:8080  （App 端接口）
#      与 mini-novel 无冲突：mini-novel 用 /、/assets/、/api/、/cover/、/admin/、
#      /admin-api/、/crawler-api/，均与上述前缀不同。
#   2) 把 video-frontend / video-backend 通过 docker network connect 接入
#      mini-novel 所在的 deploy_default 网络（纯新增连接）。
#   3) nginx -t 校验后 reload 网关。
#
# ⚠️ 重要：/opt/mini-h5 是 mini-novel 的 rsync 同步目录，它每次部署都会覆盖
#    deploy/gateway/nginx.conf，本补丁会被抹掉。**mini-novel 每次重新部署后，
#    请重跑本脚本**：
#        bash /opt/video-entitlement-gateway/apply-gateway-patch.sh
#    本脚本幂等，重复执行安全。
#
# 同理，video-* 容器每次被本项目 CI 重建后也会掉出 deploy_default 网络，
# 故 deploy.yml 里已加了 docker network connect（见该文件）。
# ============================================================================
set -euo pipefail

CONF="/opt/mini-h5/deploy/gateway/nginx.conf"
MARK="video-entitlement 接入"
GATEWAY="mini-novel-gateway"

if [ ! -f "$CONF" ]; then
  echo "❌ 找不到网关配置：$CONF" >&2
  exit 1
fi

if grep -q "$MARK" "$CONF"; then
  echo "ℹ️  补丁块已存在，跳过插入（仅做网络检查与 reload）"
else
  cp -a "$CONF" "${CONF}.bak.$(date +%Y%m%d-%H%M%S)"
  echo "🗄  已备份原配置"

  CONF="$CONF" python3 - <<'PYEOF'
import os
conf = os.environ["CONF"]
src = open(conf, encoding="utf-8").read()

# 锚点：443 server 块内的上游变量定义处（插在其后，位于所有 location 之前）
anchor = "  set $admin_upstream http://mini-novel-admin-ui:80;\n"
if anchor not in src:
    raise SystemExit("❌ 未找到锚点，已放弃插入（避免破坏网关配置）")

block = """
  # ---------- video-entitlement 接入（本块由 /opt/video-entitlement-gateway/apply-gateway-patch.sh 维护）----------
  # ⚠️ mini-novel 重新部署会覆盖本文件，届时重跑该脚本即可恢复。
  # 上游 video-frontend / video-backend 经 docker network connect 接入 deploy_default。
  set $video_frontend_upstream http://video-frontend:80;
  set $video_backend_upstream  http://video-backend:8080;

  # 管理后台 SPA：https://xs2026.site/platform/
  # 原样转发（不剥前缀），由 video-frontend 容器内部剥掉 /platform/ 前缀。
  location = /platform { return 301 /platform/; }
  location ^~ /platform/ {
    proxy_pass $video_frontend_upstream;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

  # APK 下载目录：https://xs2026.site/downloads/
  location ^~ /downloads/ {
    proxy_pass $video_frontend_upstream;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

  # 管理后台接口：https://xs2026.site/admin/api/v1/...
  location ^~ /admin/api/ {
    proxy_pass $video_backend_upstream;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

  # App 端接口：https://xs2026.site/api/v1/...（mini-novel 只用 /api/，/api/v1/ 不冲突）
  location ^~ /api/v1/ {
    proxy_pass $video_backend_upstream;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

"""
src = src.replace(anchor, anchor + block, 1)
# 就地写回（同一 inode），容器内的单文件 bind mount 才能立刻看到新内容
with open(conf, "w", encoding="utf-8") as f:
    f.write(src)
print("✅ 已插入 video-entitlement 接入块")
PYEOF
fi

# 2) 确保 video-* 容器接在网关网络上
for c in video-frontend video-backend; do
  nets="$(docker inspect -f '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}' "$c" 2>/dev/null || true)"
  if [ -z "$nets" ]; then
    echo "❌ 容器不存在：$c" >&2
  elif echo "$nets" | grep -q deploy_default; then
    echo "ℹ️  已在 deploy_default：$c"
  else
    docker network connect deploy_default "$c" && echo "🔗 已接入 deploy_default：$c"
  fi
done

# 3) 校验并重载
docker exec "$GATEWAY" nginx -t
docker exec "$GATEWAY" nginx -s reload
echo "✅ 网关已重载"
echo
echo "验证："
echo "  curl -sI https://xs2026.site/platform/"
echo "  curl -s   https://xs2026.site/api/v1/client/version"
echo "  curl -sI https://xs2026.site/downloads/video-entitlement-latest.apk"
