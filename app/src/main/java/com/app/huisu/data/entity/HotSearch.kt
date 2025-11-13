package com.app.huisu.data.entity

/**
 * 热搜条目数据模型
 */
data class HotSearchItem(
    val index: Int,              // 排名索引(1-50)
    val title: String,           // 热搜标题
    val hotValue: String,        // 热度值(如"1000万")
    val link: String             // 热搜详情链接
)

/**
 * API响应数据模型
 */
data class HotSearchResponse(
    val code: Int,               // 状态码,200表示成功
    val msg: String,             // 响应消息
    val data: List<HotSearchItem> // 热搜条目数组
)

/**
 * 热搜平台枚举
 */
enum class HotSearchPlatform(
    val id: String,
    val displayName: String,
    val icon: String
) {
    WEIBO("KqndgxeLl9", "微博", "🔥"),
    ZHIHU("mproPpoq6O", "知乎", "💡"),
    WEIXIN("WnBe01o371", "微信", "💬"),
    TOUTIAO("toutiao", "头条", "📰"),
    DOUYIN("DpQvNABoNE", "抖音", "🎵"),
    HISTORY("KMZd7X3erO", "历史", "📅"),
    BILIBILI("b0vmbRXdB1", "B站", "📺");

    companion object {
        fun fromId(id: String): HotSearchPlatform {
            return values().find { it.id == id } ?: WEIBO
        }
    }
}
