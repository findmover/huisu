package com.app.huisu.data.repository

import com.app.huisu.data.entity.HotSearchItem
import com.app.huisu.data.entity.HotSearchPlatform
import com.app.huisu.data.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 热搜数据仓库
 * 负责从API获取热搜数据
 */
@Singleton
class HotSearchRepository @Inject constructor() {

    /**
     * 获取指定平台的热搜列表
     * @param platform 平台枚举
     * @return Result包装的热搜列表
     */
    suspend fun getHotSearchList(platform: HotSearchPlatform): Result<List<HotSearchItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.hotSearchApi.getHotSearchList(id = platform.id)

                if (response.code == 200 && response.data.isNotEmpty()) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
