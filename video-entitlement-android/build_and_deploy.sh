#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "🔨 构建 APK..."
./gradlew assembleDebug --quiet

TIMESTAMP=$(date +%Y%m%d-%H%M%S)
VERSION=$(grep versionName app/build.gradle.kts | head -1 | grep -oP '"\K[^"]+')
APK_NAME="video-entitlement-v${VERSION}-${TIMESTAMP}.apk"
QR_NAME="qr-${APK_NAME%.apk}.png"
SRC_APK="app/build/outputs/apk/debug/app-debug.apk"
SERVER="root@43.161.222.78"
SSH_KEY="$HOME/.ssh/video-pro-key"
DEPLOY_DIR="/data/video-apk"
DOMAIN="http://43.161.222.78:8082"

# 本地备份
cp "$SRC_APK" "/workspace/${APK_NAME}"
cp "$SRC_APK" "/workspace/video-entitlement-android.apk"

# 生成二维码 (高贵紫主题)
echo "🎨 生成二维码..."
python3 - << PYEOF
import qrcode
url = "${DOMAIN}/downloads/${APK_NAME}"
qr = qrcode.QRCode(version=2, error_correction=qrcode.constants.ERROR_CORRECT_H, box_size=14, border=2)
qr.add_data(url)
qr.make(fit=True)
img = qr.make_image(fill_color="#7C5CBF", back_color="#F5F0FF")
img.save("/workspace/${QR_NAME}")
print(f"   时间戳 QR: ${QR_NAME}")

url2 = "${DOMAIN}/downloads/video-entitlement-latest.apk"
qr2 = qrcode.QRCode(version=2, error_correction=qrcode.constants.ERROR_CORRECT_H, box_size=14, border=2)
qr2.add_data(url2)
qr2.make(fit=True)
img2 = qr2.make_image(fill_color="#7C5CBF", back_color="#F5F0FF")
img2.save("/workspace/qr-download-latest.png")
print("   最新版 QR: qr-download-latest.png")
PYEOF

echo "📤 上传到服务器..."
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SRC_APK" "${SERVER}:${DEPLOY_DIR}/${APK_NAME}"
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SRC_APK" "${SERVER}:${DEPLOY_DIR}/video-entitlement-latest.apk"
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "/workspace/${QR_NAME}" "${SERVER}:${DEPLOY_DIR}/"
scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "/workspace/qr-download-latest.png" "${SERVER}:${DEPLOY_DIR}/"

SIZE=$(ls -lh "$SRC_APK" | awk '{print $5}')
echo ""
echo "✅ 构建完成!"
echo "   文件: ${APK_NAME}"
echo "   大小: ${SIZE}"
echo ""
echo "   📥 时间戳版: ${DOMAIN}/downloads/${APK_NAME}"
echo "   📥 最新版:   ${DOMAIN}/downloads/video-entitlement-latest.apk"
echo "   📷 时间戳QR: ${DOMAIN}/downloads/${QR_NAME}"
echo "   📷 最新QR:   ${DOMAIN}/downloads/qr-download-latest.png"
echo "   📋 全部版本: ${DOMAIN}/downloads/"
