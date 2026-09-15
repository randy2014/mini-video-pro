# 视频权益 App 项目记忆

## 核心原则
- **禁止本地构建部署**：所有部署走 GitHub Actions (randy2014/mini-video-pro)（包含本地 `mvn`/`vite build` 仅可作语法校验参考，绝不用于部署）
- **禁止手动操作 Flyway 迁移**：会导致 schema_history 冲突
- **禁止 docker cp 做持久化**：容器重启丢失，用 `-v` 卷挂载
- **APK 每次构建必须给出时间戳下载链接 + 二维码**
- **禁止变更服务器上除本项目以外的任何内容**：只操作 `video-*` 容器与 `/data/video-apk` 等本项目路径；不碰其他项目的 Nginx、配置、容器（含系统级 Nginx 只可 reload，不可改其他 server 块）
- **每次构建发布后清理服务器 Docker 缓存**：`deploy.yml` 部署完成自动执行 `docker system prune -a -f`（清理悬空/未用镜像、停止容器、构建缓存）；⚠️ **绝不能加 `--volumes`**，否则会删掉 MySQL/Redis 数据卷
- **🔴 仓库当前是 public（2026-09-15 实测）**：匿名 `GET api.github.com/repos/randy2014/mini-video-pro` 返回 200、`private: false`。而仓库内有明文 keystore 密码（`README.md`）、DB/Redis/Admin 口令（本文件）、以及签名的 `mini-video-release.jks`。**建议立即转私有并轮换密钥**（与 `问题一览.md` 第 9 条"已转私有"的记载不符）

## 服务器信息
- IP: 64.90.19.6（SSH 端口 52527，用户 root；已于 2026-08-04 从旧服务器 43.161.222.78 迁移）
- SSH Key: `~/.ssh/mini_h5_vps_ed25519_v2`（⚠️ 旧文档写的 `~/.ssh/video-pro-key` 在本机**已不存在**，2026-09-15 实测可用的是这把）
- MySQL root: VideoPro@2024!
- Admin: admin / Admin@123

## 对外访问入口（2026-09-15 起）
| 用途 | 地址 |
|------|------|
| 管理后台 | **https://xs2026.site/platform/** |
| App 端 API | **https://xs2026.site/api/v1/...** |
| 管理端 API | **https://xs2026.site/admin/api/v1/...** |
| APK 下载页 | **https://xs2026.site/downloads/** |
| 直连兜底（IP+端口，仍然可用） | http://64.90.19.6:8082/ 、http://64.90.19.6:8081/ |

> ⚠️ **域名不在本项目手里**：xs2026.site 的 80/443 由**另一个项目 mini-novel** 的网关容器
> `mini-novel-gateway`（nginx，配置 `/opt/mini-h5/deploy/gateway/nginx.conf`）持有。
> 本项目通过在该网关**新增** 4 条 location（`/platform/`、`/downloads/`、`/admin/api/`、`/api/v1/`）
> 接入域名，未改动 mini-novel 任何现有路由。
> - 补丁脚本：`ops/gateway-patch/apply-gateway-patch.sh`（服务器上在 `/opt/video-entitlement-gateway/`）
> - ⚠️ **`/opt/mini-h5` 是 mini-novel 的 rsync 同步目录，它每次部署都会覆盖 `deploy/gateway/nginx.conf`**（补丁会被抹掉）→ **mini-novel 重新部署后必须重跑该脚本**（幂等）。
> - ⚠️ 网关对单文件 bind mount 会**缓存 inode**：改完配置若 `nginx -s reload` 不生效，需 `docker restart mini-novel-gateway` 重新解析挂载。
> - `video-frontend` / `video-backend` 需接入 `deploy_default` 网络才能被网关解析到（`deploy.yml` 每次部署已自动 `docker network connect`）。

## Docker 容器
| 容器 | 端口 | 备注 |
|------|------|------|
| video-frontend | 8082 | 卷挂载 /data/video-apk:/usr/share/nginx/html/downloads |
| video-backend | 8081 | 镜像 ghcr.io/randy2014/mini-video-pro-backend:latest |
| video-mysql | 3307 | password=VideoPro@2024! |
| video-redis | 6380 | password=VideoUser@2024! |

## 项目结构
- video-entitlement-server: Spring Boot 3 + Java 21
- video-entitlement-admin: React + TypeScript + Ant Design + Vite
- video-entitlement-android: Kotlin + XML + ExoPlayer + WebView

## 业务模块（以当前代码为准，共 6 个）
- 平台管理 (platform): 支持 video/music/tv/drama 类型, 编辑/删除 + APP 版本发布
- 管理员管理 (admin): 认证/角色/权限/操作日志 + 用户管理
- 用户管理 (user): 登录/注册/邀请码/验证码
- 权益管理 (entitlement): 权益码生成/兑换
- 设备管理 (device): 设备绑定/状态
- 统计 (stats)

> 已删除模块: 播放路由 (playback)、健康监控 (health)、风控管理 (risk)、配置发布 (configrelease)。注意 `application.yml` 仍残留 `app.playback.*` 配置段，属历史遗留，可清理。

## Android APK
- 当前版本: **0.9.202609151543 (versionCode 9151543)** — 2026-09-15 打包，已登记版本表（id=14, ACTIVE）
- 构建脚本: build_and_deploy.sh (构建→时间戳命名→二维码→上传) — ⚠️ 已弃用，打包走 CI
- 打包工作流: `.github/workflows/build-android.yml`（workflow_dispatch 手动触发）
- API 基址: `https://xs2026.site`（App 调 `/api/v1/...`；原为 `http://64.90.19.6:8081`）
- 命名: video-entitlement-v{version}-{YYYYMMDD}-{HHmmss}.apk
- 下载页: https://xs2026.site/downloads/
- 特性: 高贵紫主题, 全屏沉浸, API驱动, SwipeRefreshLayout, 版本号显示
- ⚠️ CI 打包注意：**不要再用 `android-actions/setup-android@v3`**（其内部 `sdkmanager tools`
  因上游移除 tools 包而失败）；且仓库内 `video-entitlement-android/local.properties` 的
  `sdk.dir` 写死了旧 action 的 `/opt/android-sdk`，AGP 中它**优先于 ANDROID_HOME**，
  故 CI 里必须按真实 SDK 路径覆写。详见 `2026-09-15-域名接入变更.md` 第七节。
- **打包后必须单独登记版本表**（CI 不会自动登记）：`ops/deploy/register-version.sh <versionName> <versionCode> <apk文件名> "<说明>"`

## GitHub 访问方式（2026-09-15 起，重要）
- ⚠️ 本机 **`github.com:443` 不通**（解析到被墙 IP，hosts 无权限改），`git push` 不可用；
  但 `api.github.com` 正常。
- 推送改用 `ops/github/push-via-api.ps1`（GitHub REST API 建 blob→tree→commit→更新 main），
  等效一次正常 push，会触发 `deploy.yml`。操作前先核对脚本里的 `$expectBase` 与远端 main。

## 数据库
- video_platform: 核心表, 有 platform_type 字段
- 不要直接删表, 用 Flyway 迁移
- 不要手动 INSERT 数据, 用管理后台或 SQL 文件 + utf8mb4