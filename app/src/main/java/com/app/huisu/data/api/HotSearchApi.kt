package com.app.huisu.data.api

import com.app.huisu.data.entity.HotSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 热搜榜单API接口
 * 基础URL: https://api.codelife.cc/
 */
interface HotSearchApi {

    /**
     * 获取热搜列表
     * @param lang 语言设置,默认"cn"
     * @param id 平台ID
     * @return 热搜数据响应
     */
    @GET("api/top/list")
    suspend fun getHotSearchList(
        @Query("lang") lang: String = "cn",
        @Query("id") id: String
    ): HotSearchResponse
}
