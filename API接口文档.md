# 热搜榜单 API 接口文档

## 📋 项目概述

本项目聚合了多个平台的热搜榜单数据，包括微博、知乎、微信、抖音等 8 个平台。

**API 提供方**: CodeLife API (https://api.codelife.cc)

---

## 🔗 API 基础信息

### 基础 URL
```
https://api.codelife.cc/
```

### 热搜榜单端点
```
GET /api/top/list
```

### 完整请求 URL
```
https://api.codelife.cc/api/top/list?lang=cn&id={平台ID}
```

---

## 📊 支持的平台列表

| 序号 | 平台名称 | 平台 ID | 说明 |
|------|---------|---------|------|
| 1 | 微博 | `KqndgxeLl9` | 微博实时热搜榜 |
| 2 | 知乎 | `mproPpoq6O` | 知乎热榜 |
| 3 | 微信 | `WnBe01o371` | 微信热门文章 |
| 4 | 头条 | `toutiao` | 今日头条热榜 |
| 5 | 煎蛋 | `NRrvWq3e5z` | 煎蛋热文 |
| 6 | 抖音 | `DpQvNABoNE` | 抖音热榜 |
| 7 | 历史上的今天 | `KMZd7X3erO` | 历史事件 |
| 8 | 哔哩哔哩 | `b0vmbRXdB1` | B站热门视频 |

---

## 📝 请求参数

### Query 参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| `lang` | String | 否 | `cn` | 语言设置（中文） |
| `id` | String | 是 | - | 平台ID，见上表 |

### 请求示例

**获取微博热搜**:
```bash
curl "https://api.codelife.cc/api/top/list?lang=cn&id=KqndgxeLl9"
```

**获取知乎热榜**:
```bash
curl "https://api.codelife.cc/api/top/list?lang=cn&id=mproPpoq6O"
```

**获取抖音热榜**:
```bash
curl "https://api.codelife.cc/api/top/list?lang=cn&id=DpQvNABoNE"
```

---

## 🔑 请求头 (Headers)

**重要**: 该 API 需要认证 token 和签名密钥

```http
accept: application/json, text/plain, */*
accept-language: zh-CN,zh;q=0.9,en;q=0.8
cache-control: no-cache
token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MjE0YWU5MjhmODk3ZDNhZGZjZjkwZTYiLCJpYXQiOjE3MzUxNzk5NzgsImV4cCI6MTc5NzM4Nzk3OH0.DDgHUHZ9kXp5_1cJ2pA-a_ON0kUMv7AKY1LtejJ8kSQ
signaturekey: U2FsdGVkX1+lkSSk7qM9110tQ2KZKA8dk6AQ7j8d0cc=
origin: chrome-extension://inedkoakiaeepjoblbiiipedngonadhn
user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
```

**Token 信息**:
- JWT token，包含用户 ID
- 有效期: 到 2027年（从 exp: 1797387978 推算）
- 用户 ID: `6214ae928f897d3adfcf90e6`

**注意**:
- `signaturekey` 用于请求签名验证
- `origin` 表明这是 Chrome 扩展的 API

---

## 📤 响应格式

### 成功响应

**HTTP 状态码**: `200 OK`

**响应结构**:
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "index": 1,
      "title": "热搜标题",
      "hotValue": "1000万",
      "link": "https://weibo.com/..."
    },
    {
      "index": 2,
      "title": "第二条热搜",
      "hotValue": "800万",
      "link": "https://weibo.com/..."
    }
    // ... 更多条目
  ]
}
```

### 响应字段说明

**根对象**:
| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，200 表示成功 |
| `msg` | String | 响应消息 |
| `data` | Array | 热搜条目数组 |

**热搜条目对象 (HotSearchItem)**:
| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `index` | Integer | 排名索引（1-50） | `1` |
| `title` | String | 热搜标题 | `"某某事件上热搜"` |
| `hotValue` | String | 热度值（含单位） | `"1000万"`, `"50万"` |
| `link` | String | 热搜详情链接 | `"https://weibo.com/..."` |

### 错误响应

**API 错误**:
```json
{
  "code": 400,
  "msg": "Invalid platform ID",
  "data": []
}
```

**认证失败**:
```json
{
  "code": 401,
  "msg": "Unauthorized",
  "data": []
}
```

---

## 💻 代码示例

### JavaScript (Fetch API)

```javascript
async function getHotSearch(platformId) {
  const url = `https://api.codelife.cc/api/top/list?lang=cn&id=${platformId}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'accept': 'application/json',
      'token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MjE0YWU5MjhmODk3ZDNhZGZjZjkwZTYiLCJpYXQiOjE3MzUxNzk5NzgsImV4cCI6MTc5NzM4Nzk3OH0.DDgHUHZ9kXp5_1cJ2pA-a_ON0kUMv7AKY1LtejJ8kSQ',
      'signaturekey': 'U2FsdGVkX1+lkSSk7qM9110tQ2KZKA8dk6AQ7j8d0cc='
    }
  });

  const data = await response.json();
  return data;
}

// 使用示例
getHotSearch('KqndgxeLl9').then(result => {
  console.log('微博热搜:', result.data);
});
```

### Kotlin (Retrofit)

```kotlin
// API 接口定义
interface WeiboApi {
    @GET("api/top/list")
    suspend fun getHotSearchList(
        @Query("lang") lang: String = "cn",
        @Query("id") id: String
    ): WeiboHotSearchResponse
}

// 数据模型
data class WeiboHotSearchResponse(
    val code: Int,
    val msg: String,
    val data: List<HotSearchItem>
)

data class HotSearchItem(
    val index: Int,
    val title: String,
    val hotValue: String,
    val link: String
)

// 使用示例
suspend fun getWeiboHotSearch() {
    val response = NetworkClient.weiboApi.getHotSearchList(id = "KqndgxeLl9")
    if (response.code == 200) {
        response.data.forEach { item ->
            println("${item.index}. ${item.title} - ${item.hotValue}")
        }
    }
}
```

### Python (Requests)

```python
import requests

def get_hot_search(platform_id):
    url = "https://api.codelife.cc/api/top/list"
    params = {
        "lang": "cn",
        "id": platform_id
    }
    headers = {
        "accept": "application/json",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "signaturekey": "U2FsdGVkX1+lkSSk7qM9110tQ2KZKA8dk6AQ7j8d0cc="
    }

    response = requests.get(url, params=params, headers=headers)
    return response.json()

# 使用示例
result = get_hot_search("KqndgxeLl9")
for item in result["data"]:
    print(f"{item['index']}. {item['title']} - {item['hotValue']}")
```

---

## 🔧 Retrofit 完整配置

### NetworkClient.kt

```kotlin
object NetworkClient {
    private const val BASE_URL = "https://api.codelife.cc/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("accept", "application/json, text/plain, */*")
                .header("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .header("signaturekey", "U2FsdGVkX1+lkSSk7qM9110tQ2KZKA8dk6AQ7j8d0cc=")
                .header("user-agent", "Mozilla/5.0 ...")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weiboApi: WeiboApi = retrofit.create(WeiboApi::class.java)
}
```

---

## 📱 平台切换逻辑

### 根据平台名称获取 ID

```kotlin
fun getPlatformId(platformName: String): String {
    return when (platformName) {
        "weibo" -> "KqndgxeLl9"      // 微博
        "zhihu" -> "mproPpoq6O"      // 知乎
        "weixin" -> "WnBe01o371"     // 微信
        "toutiao" -> "toutiao"       // 头条
        "jandan" -> "NRrvWq3e5z"     // 煎蛋
        "douyin" -> "DpQvNABoNE"     // 抖音
        "history" -> "KMZd7X3erO"    // 历史
        "bilibili" -> "b0vmbRXdB1"   // B站
        else -> "KqndgxeLl9"         // 默认微博
    }
}
```

### 数据加载示例

```kotlin
suspend fun loadHotSearch(platform: String): List<HotSearchItem> {
    val platformId = getPlatformId(platform)
    val response = NetworkClient.weiboApi.getHotSearchList(id = platformId)

    return if (response.code == 200 && response.data.isNotEmpty()) {
        response.data
    } else {
        emptyList()
    }
}
```

---

## ⚠️ 注意事项

1. **Token 有效期**
   - 当前 token 有效期到 2027年
   - 如果 token 过期，需要重新获取

2. **请求频率限制**
   - API 可能有请求频率限制
   - 建议实现本地缓存，避免频繁请求

3. **跨域问题**
   - Web 环境可能遇到 CORS 问题
   - 建议通过后端代理或使用浏览器扩展

4. **链接有效性**
   - 热搜链接可能失效
   - 建议添加错误处理

5. **数据时效性**
   - 热搜数据实时更新
   - 建议每 3分钟刷新一次

---

## 📚 相关文件位置

- API 接口定义: `app/src/main/java/com/xooov/hotpot/network/WeiboApi.kt`
- 网络客户端: `app/src/main/java/com/xooov/hotpot/network/NetworkClient.kt`
- 数据加载服务: `app/src/main/java/com/xooov/hotpot/HotSearchWidgetService.kt`

---

**文档版本**: v1.0
**更新日期**: 2025-11-12
**维护者**: Hotpot 项目组
