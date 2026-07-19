# mini-video-pro

迷你视频 VIP 视频权益 APP

## 项目结构

```
├── video-entitlement-server/    # 后端 Spring Boot 3.4 + Java 21
├── video-entitlement-android/   # Android APP
└── video-entitlement-admin/     # 管理后台前端 Vite + React
```

## Android 签名信息

| 项目 | 值 |
|------|-----|
| 签名文件 | `video-entitlement-android/mini-video-release.jks` |
| keystore 密码 | `MiniVideo@2026!` |
| key alias | `mini-video` |
| key 密码 | `MiniVideo@2026!` |
| 签名算法 | RSA 2048 / SHA256withRSA |
| 有效期 | 100 年 |
| 签名者 DN | CN=mini-video, OU=Video Entitlement, O=Mini Video, L=Shenzhen, ST=Guangdong, C=CN |
| SHA-256 | `81ee2ee79542205efcf274880c0820b82fe770ee88619dd7ef17b3f35fd82609` |
| SHA-1 | `357fc0a234e6f5f88faf08cd8749005caccb06cb` |

## 版本号规则

- **测试版**: `versionName = 0.9.yyyyMMddHHmm`, `versionCode = MMddHHmm`
- **正式版**: `versionName = 1.0.yyyyMMddHHmm`

## 构建

### 后端

```bash
cd video-entitlement-server
mvn clean package -DskipTests
```

### Android

```bash
cd video-entitlement-android
./gradlew assembleRelease
```
