# Android APK 签名信息

## 签名文件

| 项目 | 值 |
|------|-----|
| 文件名 | `mini-video-release.jks` |
| 位置 | `/workspace/mini-video-release.jks` |
| 密钥库类型 | PKCS12 |
| 密钥库密码 | `MiniVideo@2026!` |
| 别名 (Alias) | `mini-video` |
| 密钥密码 | `MiniVideo@2026!` |

## 证书信息

| 项目 | 值 |
|------|-----|
| 所有者 (CN) | mini-video |
| 组织单元 (OU) | Video Entitlement |
| 组织 (O) | Mini Video |
| 地区 (L) | Shenzhen |
| 省份 (ST) | Guangdong |
| 国家 (C) | CN |
| 颁发者 (Issuer) | CN=mini-video (自签名) |

## 证书有效期

| 项目 | 值 |
|------|-----|
| 签发日期 | 2026年7月19日 08:46:14 CST |
| 到期日期 | 2126年6月25日 08:46:14 CST |
| 有效期 | 约 100 年 |
| 序列号 | `efcca161ab0789b5` |
| 签名算法 | SHA384withRSA |
| 公钥 | 2048-bit RSA |

## 指纹

| 算法 | 指纹 |
|------|------|
| SHA1 | `35:7F:C0:A2:34:E6:F5:F8:8F:AF:08:CD:87:49:00:5C:AC:CB:06:CB` |
| SHA256 | `81:EE:2E:E7:95:42:20:5E:FC:F2:74:88:0C:08:20:B8:2F:E7:70:EE:88:61:9D:D7:EF:17:B3:F3:5F:D8:26:09` |

## 构建配置

在 `app/build.gradle.kts` 中配置：

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../mini-video-release.jks")
        storePassword = "MiniVideo@2026!"
        keyAlias = "mini-video"
        keyPassword = "MiniVideo@2026!"
    }
}
```

签名方案：V2 (APK Signature Scheme v2)

## 安全提醒

- 签名文件为自签名证书，仅用于应用内部签名
- 请勿将密钥库文件和密码泄露给不相关的人员
- 建议将 `mini-video-release.jks` 备份到安全位置
