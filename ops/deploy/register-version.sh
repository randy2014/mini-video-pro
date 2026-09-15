#!/bin/bash
# 登记 APK 版本到「APP 版本管理」表，并把旧版本置为 INACTIVE。
#
# 用法：
#   register-version.sh <versionName> <versionCode> <apk文件名> "<更新说明>"
# 例：
#   register-version.sh 0.9.202609151543 9151543 \
#     video-entitlement-v0.9.202609151543-20260915-154435.apk "访问域名切换为 https://xs2026.site"
#
# 说明：build-android.yml 只负责构建+上传 APK 文件，**不会**自动登记版本表，
#       所以每次打包后都要单独跑一次本脚本（见 迁移手顺.md 第八节）。
set -euo pipefail

BASE="${BASE:-https://xs2026.site}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-Admin@123}"

VERSION_NAME="${1:?usage: register-version.sh <versionName> <versionCode> <apkName> [notes]}"
VERSION_CODE="${2:?}"
APK_NAME="${3:?}"
NOTES="${4:-自动化构建发布}"
DOWNLOAD_URL="$BASE/downloads/$APK_NAME"

echo "==> 登录管理端 $BASE"
TOKEN=$(curl -sS -X POST "$BASE/admin/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}" \
  | python3 -c "import sys,json; d=json.load(sys.stdin); print(d['data']['adminToken'])")
if [ -z "$TOKEN" ]; then echo "❌ 登录失败，拿不到 adminToken" >&2; exit 1; fi
echo "    token 获取成功"

echo "==> 登记新版本 $VERSION_NAME (code $VERSION_CODE)"
BODY=$(VERSION_NAME="$VERSION_NAME" VERSION_CODE="$VERSION_CODE" NOTES="$NOTES" DL="$DOWNLOAD_URL" python3 -c '
import json, os
print(json.dumps({
    "versionName": os.environ["VERSION_NAME"],
    "versionCode": int(os.environ["VERSION_CODE"]),
    "downloadUrl": os.environ["DL"],
    "releaseNotes":  os.environ["NOTES"],
    "forceUpdate":   False,
}, ensure_ascii=False))')

RESP=$(curl -sS -X POST "$BASE/admin/api/v1/versions" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d "$BODY")
echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print('    ->', d.get('code'), d.get('message'), 'id=', (d.get('data') or {}).get('id'))"

echo "==> 停用其它 ACTIVE 旧版本（保留刚登记的这条）"
NEW_ID=$(echo "$RESP" | python3 -c "import sys,json; print((json.load(sys.stdin).get('data') or {}).get('id') or '')")
curl -sS "$BASE/admin/api/v1/versions" -H "Authorization: Bearer $TOKEN" \
  | python3 -c "
import sys, json
d = json.load(sys.stdin)
new_id = '$NEW_ID'
for v in (d.get('data') or []):
    if v.get('status') == 'ACTIVE' and str(v.get('id')) != str(new_id):
        print(v['id'], v.get('versionName'))
" | while read -r id name; do
    [ -z "$id" ] && continue
    echo "    INACTIVE -> id=$id $name"
    curl -sS -X PUT "$BASE/admin/api/v1/versions/$id/status?status=INACTIVE" \
      -H "Authorization: Bearer $TOKEN" >/dev/null
  done

echo
echo "==> 客户端视角校验（App 拉取的版本）"
curl -sS "$BASE/api/v1/client/version" | python3 -m json.tool
