#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "🔨 构建 APK..."
./gradlew assembleDebug --quiet

TIMESTAMP=$(date +%Y%m%d-%H%M%S)
VERSION=$(grep versionName app/build.gradle.kts | head -1 | grep -oP '"\K[^"]+')
APK_NAME="video-entitlement-v${VERSION}-${TIMESTAMP}.apk"
SRC_APK="app/build/outputs/apk/debug/app-debug.apk"

# 复制到 workspace
cp "$SRC_APK" "/workspace/${APK_NAME}"
cp "$SRC_APK" "/workspace/video-entitlement-latest.apk"

# 部署到服务器
SERVER="root@43.161.222.78"
SSH_KEY="$HOME/.ssh/video-pro-key"

echo "📤 上传到服务器..."
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "/workspace/${APK_NAME}" "${SERVER}:/tmp/apk/"
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "/workspace/${APK_NAME}" "${SERVER}:/tmp/apk/video-entitlement-latest.apk"

echo "📦 更新 nginx 下载目录..."
ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SERVER" "
  docker exec video-frontend mkdir -p /usr/share/nginx/html/downloads
  docker cp /tmp/apk/${APK_NAME} video-frontend:/usr/share/nginx/html/downloads/
  docker cp /tmp/apk/video-entitlement-latest.apk video-frontend:/usr/share/nginx/html/
"

SIZE=$(ls -lh "/workspace/${APK_NAME}" | awk '{print $5}')
echo ""
echo "✅ 构建完成!"
echo "   文件: ${APK_NAME}"
echo "   大小: ${SIZE}"
echo "   下载: http://43.161.222.78:8082/downloads/${APK_NAME}"
echo "   最新: http://43.161.222.78:8082/video-entitlement-latest.apk"
