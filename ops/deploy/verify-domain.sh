#!/bin/bash
# 域名入口全量验收：本项目 4 条路由 + mini-novel 回归
set -u
BASE="https://xs2026.site"

chk() { # chk <label> <url> [curl-extra...]
  local label="$1"; shift
  local url="$1"; shift
  local code size
  read -r code size < <(curl -s -o /dev/null -w "%{http_code} %{size_download}" "$@" "$url")
  printf "  %-46s HTTP %-4s %s bytes\n" "$label" "$code" "$size"
}

echo "===== 本项目（video-entitlement）====="
chk "/platform/"                        "$BASE/platform/"
chk "/platform/ 入口 JS"                "$BASE/platform/assets/index-CwqQlcB6.js"
chk "/api/v1/client/version"            "$BASE/api/v1/client/version"
chk "/admin/api/v1/versions(需鉴权)"    "$BASE/admin/api/v1/versions"
chk "/downloads/ 时间戳 APK"            "$BASE/downloads/video-entitlement-v0.9.202609151543-20260915-154435.apk" -I
chk "/downloads/latest.apk"             "$BASE/downloads/video-entitlement-latest.apk" -I
chk "/downloads/ 时间戳二维码"          "$BASE/downloads/qr-video-entitlement-v0.9.202609151543-20260915-154435.png" -I
chk "/downloads/qr-download-latest.png" "$BASE/downloads/qr-download-latest.png" -I

echo
echo "===== IP 直连兜底 ====="
chk ":8082/ 管理后台"      "http://127.0.0.1:8082/"
chk ":8082/platform/ JS"   "http://127.0.0.1:8082/platform/assets/index-CwqQlcB6.js"
chk ":8082/downloads/"     "http://127.0.0.1:8082/downloads/video-entitlement-latest.apk" -I
chk ":8081 后端 API"       "http://127.0.0.1:8081/api/v1/client/version"

echo
echo "===== mini-novel 回归（不应受影响）====="
chk "/"             "$BASE/"
chk "/h5/home"      "$BASE/h5/home"
chk "/admin/login"  "$BASE/admin/login"
chk "/healthz"      "$BASE/healthz"
chk "/api/home"     "$BASE/api/home"
echo -n "  首页标题: "; curl -s "$BASE/" | grep -o '<title>[^<]*</title>'

echo
echo "===== 小程序端看到的版本 ====="
curl -s "$BASE/api/v1/client/version" | python3 -m json.tool 2>/dev/null || curl -s "$BASE/api/v1/client/version"
