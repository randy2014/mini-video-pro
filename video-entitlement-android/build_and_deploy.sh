#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "🔨 构建 APK..."
./gradlew assembleDebug --quiet

TIMESTAMP=$(date +%Y%m%d-%H%M%S)
VERSION=$(grep versionName app/build.gradle.kts | head -1 | grep -oP '"\K[^"]+')
APK_NAME="video-entitlement-v${VERSION}-${TIMESTAMP}.apk"
SRC_APK="app/build/outputs/apk/debug/app-debug.apk"
SERVER="root@43.161.222.78"
SSH_KEY="$HOME/.ssh/video-pro-key"
DEPLOY_DIR="/data/video-apk"

# 本地备份
cp "$SRC_APK" "/workspace/${APK_NAME}"
cp "$SRC_APK" "/workspace/video-entitlement-android.apk"

echo "📤 上传到服务器..."
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SRC_APK" "${SERVER}:${DEPLOY_DIR}/${APK_NAME}"
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SRC_APK" "${SERVER}:${DEPLOY_DIR}/video-entitlement-latest.apk"

SIZE=$(ls -lh "$SRC_APK" | awk '{print $5}')
echo ""
echo "✅ 构建完成!"
echo "   文件: ${APK_NAME}"
echo "   大小: ${SIZE}"
echo ""
echo "   📥 时间戳版: http://43.161.222.78:8082/downloads/${APK_NAME}"
echo "   📥 最新版:   http://43.161.222.78:8082/downloads/video-entitlement-latest.apk"
echo "   📋 全部版本: http://43.161.222.78:8082/downloads/"
