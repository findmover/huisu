# HuiSu Sync API

这是一个轻量的单用户同步服务。App 继续把 Room 数据库作为本地主数据源，服务器只保存一份 JSON 快照和递增版本号。

## 接口

所有 `/v1/*` 接口都需要请求头：

```http
Authorization: Bearer <SYNC_API_TOKEN>
```

### `GET /health`

健康检查，不需要认证。

### `POST /v1/sync/probe`

认证和服务连通性检查。

### `GET /v1/sync/meta`

只返回云端当前快照的元信息，不返回完整数据。

### `GET /v1/sync/snapshot`

返回云端当前快照。

没有快照时：

```json
{
  "exists": false,
  "revision": 0,
  "snapshot": null
}
```

### `PUT /v1/sync/snapshot`

上传本地快照。正常情况下需要带上 App 上一次看到的 `base_revision`，避免覆盖其他设备刚同步的数据。

```json
{
  "device_id": "android-main",
  "base_revision": 0,
  "client_updated_at": "2026-04-27T10:00:00+08:00",
  "snapshot": {
    "schemaVersion": 1,
    "exportedAt": 1770000000000,
    "tables": {}
  }
}
```

如果云端版本已经变化，服务会返回 `409`。App 应该先 `GET /v1/sync/snapshot` 拉取云端，再决定合并或覆盖。

需要强制覆盖时使用：

```http
PUT /v1/sync/snapshot?force=true
```

## Docker 部署

1. 创建环境文件：

```bash
cp .env.example .env
```

2. 修改 `.env`：

```env
SYNC_API_TOKEN=换成一段足够长的随机字符串
SYNC_DB_PATH=/data/sync.db
SYNC_MAX_BODY_BYTES=10485760
SYNC_HISTORY_LIMIT=30
```

3. 启动：

```bash
docker compose up -d --build
```

4. 测试：

```bash
curl http://服务器IP:18080/health
curl -X POST http://服务器IP:18080/v1/sync/probe \
  -H "Authorization: Bearer 你的TOKEN"
```

## 腾讯云部署建议

生产环境建议用 HTTPS。最简单的方式是在这服务前面放 Nginx、Caddy 或腾讯云负载均衡，把公网 HTTPS 反代到容器的 `8080`。

如果只给自己用，也可以先在安全组里只放行你的常用 IP，再开放 `18080`。长期使用建议改为域名加 HTTPS。

## 同步策略

当前版本是快照同步，不拆每张表的增量接口。适合一个人使用、少量设备同步和备份恢复。

推荐 App 侧流程：

1. App 启动时调用 `GET /v1/sync/meta`，发现云端版本高于本地记录时再拉取快照。
2. 本地关键操作后导出 Room 快照，调用 `PUT /v1/sync/snapshot`。
3. 如果返回 `409`，先拉云端快照并合并到本地，再重新上传。
4. 本地保存最近一次成功同步的 `revision`。
