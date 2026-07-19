# 视频权益 App 项目记忆

## 核心原则
- **禁止本地构建部署**：所有部署走 GitHub Actions (randy2014/mini-video-pro)（包含本地 `mvn`/`vite build` 仅可作语法校验参考，绝不用于部署）
- **禁止手动操作 Flyway 迁移**：会导致 schema_history 冲突
- **禁止 docker cp 做持久化**：容器重启丢失，用 `-v` 卷挂载
- **APK 每次构建必须给出时间戳下载链接 + 二维码**
- **禁止变更服务器上除本项目以外的任何内容**：只操作 `video-*` 容器与 `/data/video-apk` 等本项目路径；不碰其他项目的 Nginx、配置、容器（含系统级 Nginx 只可 reload，不可改其他 server 块）

## 服务器信息
- IP: 43.161.222.78
- SSH Key: ~/.ssh/video-pro-key
- MySQL root: VideoPro@2024!
- Admin: admin / Admin@123

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

## 已删除模块
- 播放路由 (playback)
- 权益管理 (entitlement)
- 健康监控 (health)

## 保留模块
- 平台管理 (platform): 支持 video/music/tv/drama 类型, 编辑/删除
- 管理员管理 (admin)
- 风控管理 (risk)
- 配置发布 (config)
- 统计 (stats)

## Android APK
- 版本: v1.2 (versionCode 3)
- 构建脚本: build_and_deploy.sh (构建→时间戳命名→二维码→上传)
- 命名: video-entitlement-v{version}-{YYYYMMDD}-{HHmmss}.apk
- 下载页: http://43.161.222.78:8082/downloads/
- 特性: 高贵紫主题, 全屏沉浸, API驱动, SwipeRefreshLayout, 版本号显示

## 数据库
- video_platform: 核心表, 有 platform_type 字段
- 不要直接删表, 用 Flyway 迁移
- 不要手动 INSERT 数据, 用管理后台或 SQL 文件 + utf8mb4