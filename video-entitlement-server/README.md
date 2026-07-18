# video-entitlement-server

视频权益APP后端服务 - 基于 Spring Boot 3.x + Java 21 构建

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.4.3 |
| MySQL | 8.0 |
| Redis | 7.x |
| Flyway | 10.x |
| JWT | jjwt 0.12.6 |
| Swagger | SpringDoc 2.7.0 |

## 快速启动

```bash
# 1. 启动依赖服务
docker-compose up -d mysql redis

# 2. 编译运行
mvn clean package -DskipTests
java -jar target/video-entitlement-server-1.0.0-SNAPSHOT.jar

# 3. 或一键启动全部
docker-compose up -d
```

## 项目结构

```
src/main/java/com/video/entitlement/
├── common/                    # 公共组件
│   ├── config/               # Redis, Swagger 配置
│   ├── constant/             # 常量
│   ├── enums/                # CodeEnum 接口
│   ├── exception/            # 异常和全局处理器
│   ├── filter/               # RequestIdFilter
│   ├── response/             # ApiResponse, PageResult
│   ├── security/             # JWT, SecurityConfig
│   └── util/                 # 工具类
├── module/
│   ├── admin/                # 管理员 (认证/角色/权限/操作日志)
│   ├── user/                 # 用户 (登录/Refresh Token/登录日志)
│   ├── device/               # 设备 (设备绑定/状态)
│   ├── entitlement/          # 权益 (产品/批次/权益码/兑换/次数限制)
│   ├── platform/             # 平台 (域名/URL规则/标准化)
│   ├── playback/             # 播放路由 (供应商/线路/规则/resolve/report)
│   ├── health/               # 健康 (健康指标/熔断/指标统计)
│   ├── configrelease/        # 配置发布 (版本/灰度/回滚)
│   ├── risk/                 # 风控 (限流/黑名单/风险事件)
│   └── stats/                # 统计
└── VideoEntitlementApplication.java
```

## API 端点

### 客户端接口 (前缀 /api/v1)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/auth/login | 用户登录 |
| POST | /api/v1/auth/refresh | 刷新Token |
| GET | /api/v1/client/platforms | 获取平台列表 |
| POST | /api/v1/client/url/standardize | URL标准化 |
| GET | /api/v1/entitlement/my | 我的权益 |
| POST | /api/v1/entitlement/redeem | 兑换权益码 |
| POST | /api/v1/playback/resolve | 播放决策 |
| POST | /api/v1/playback/report | 上报播放结果 |

### 管理端接口 (前缀 /admin/api/v1)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /admin/api/v1/auth/login | 管理员登录 |
| POST | /admin/api/v1/auth/create-admin | 创建管理员 |
| GET | /admin/api/v1/admin/users | 管理员列表 |
| GET | /admin/api/v1/admin/operation-logs | 操作日志 |
| GET | /admin/api/v1/admin/roles | 角色列表 |
| POST | /admin/api/v1/entitlement/products | 创建权益产品 |
| GET | /admin/api/v1/entitlement/products | 产品列表 |
| POST | /admin/api/v1/entitlement/batches | 生成权益码批次 |
| GET | /admin/api/v1/entitlement/batches | 批次列表 |
| POST | /admin/api/v1/platform | 创建平台 |
| GET | /admin/api/v1/platform | 平台列表 |
| POST | /admin/api/v1/platform/{id}/domains | 添加域名 |
| POST | /admin/api/v1/platform/{id}/rules | 添加URL规则 |
| GET/POST | /admin/api/v1/playback/providers | 供应商管理 |
| GET/POST | /admin/api/v1/playback/routes | 线路管理 |
| GET/POST | /admin/api/v1/playback/rules | 规则管理 |
| GET | /admin/api/v1/playback/health | 线路健康状态 |
| GET/POST | /admin/api/v1/risk/blacklist | 黑名单管理 |
| GET | /admin/api/v1/risk/events | 风险事件 |
| GET | /admin/api/v1/risk/rules | 风控规则 |
| GET/POST | /admin/api/v1/config/releases | 配置发布 |
| GET | /admin/api/v1/stats/summary | 核心统计 |

## 错误码体系

| 类别 | 编码范围 | 示例 |
|------|---------|------|
| 认证 | 1000-1999 | AUTH_TOKEN_INVALID(1001) |
| 用户 | 2000-2999 | USER_DISABLED(2001) |
| 设备 | 3000-3999 | DEVICE_BLOCKED(3001) |
| 权益 | 4000-4999 | ENTITLEMENT_EXPIRED(4003) |
| 平台 | 5000-5999 | PLATFORM_DISABLED(5002) |
| URL | 6000-6999 | URL_HOST_NOT_ALLOWED(6002) |
| 播放 | 7000-7999 | PLAYBACK_ROUTE_UNAVAILABLE(7003) |
| 配置 | 8000-8999 | CONFIG_VERSION_INVALID(8001) |
| 风控 | 9000-9999 | RATE_LIMITED(9001) |
| 通用 | 10001+ | SYSTEM_ERROR(10500) |

## 默认管理员

- 用户名: `admin`
- 密码: `Admin@123` (可通过 ADMIN_DEFAULT_PASSWORD 环境变量修改)

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| DB_USERNAME | root | 数据库用户名 |
| DB_PASSWORD | root123 | 数据库密码 |
| DB_HOST | localhost | 数据库主机 |
| REDIS_HOST | localhost | Redis主机 |
| JWT_ACCESS_SECRET | (必须修改) | Access Token密钥 |
| JWT_REFRESH_SECRET | (必须修改) | Refresh Token密钥 |
| ENTITLEMENT_HMAC_KEY | (必须修改) | 权益码HMAC密钥 |
| ADMIN_DEFAULT_PASSWORD | Admin@123 | 默认管理员密码 |
| SWAGGER_ENABLED | true | 是否启用Swagger |
