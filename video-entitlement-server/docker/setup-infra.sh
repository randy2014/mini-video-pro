#!/bin/bash
# 一次性基础设施搭建脚本 - 在服务器上运行一次即可
# 使用独立 Docker 容器，完全不影响现有服务

set -e

echo "=== 创建 Docker 网络 ==="
docker network create video-net 2>/dev/null || echo "video-net 已存在"

echo "=== 启动 MySQL (端口 3307, 独立数据卷) ==="
docker stop video-mysql 2>/dev/null || true
docker rm video-mysql 2>/dev/null || true
docker run -d --name video-mysql --restart always \
  --network video-net \
  -p 3307:3306 \
  -e MYSQL_ROOT_PASSWORD="${DB_ROOT_PASSWORD:-VideoPro@2024!}" \
  -e MYSQL_DATABASE=video_entitlement \
  -e MYSQL_USER=video_user \
  -e MYSQL_PASSWORD="${DB_PASSWORD:-VideoUser@2024!}" \
  -e TZ=Asia/Shanghai \
  -v video-mysql-data:/var/lib/mysql \
  mysql:8.0 \
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

echo "=== 启动 Redis (端口 6380, 独立数据卷) ==="
docker stop video-redis 2>/dev/null || true
docker rm video-redis 2>/dev/null || true
docker run -d --name video-redis --restart always \
  --network video-net \
  -p 6380:6379 \
  -v video-redis-data:/data \
  redis:7-alpine \
  redis-server --appendonly yes

echo "=== 等待服务就绪 ==="
sleep 10

echo "=== 检查 MySQL ==="
docker exec video-mysql mysqladmin ping -h localhost -u root -p"${DB_ROOT_PASSWORD:-VideoPro@2024!}" 2>&1 | grep -q "alive" && echo "MySQL OK" || echo "MySQL 可能还在启动中..."

echo "=== 检查 Redis ==="
docker exec video-redis redis-cli ping 2>&1 | grep -q "PONG" && echo "Redis OK" || echo "Redis 可能还在启动中..."

echo "=== 基础设施搭建完成 ==="
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" | grep video
