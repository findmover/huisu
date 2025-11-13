package com.app.huisu.data.repository

import com.app.huisu.data.dao.VideoLinkDao
import com.app.huisu.data.entity.VideoLink
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoLinkRepository @Inject constructor(
    private val videoLinkDao: VideoLinkDao
) {

    fun getAllVideoLinks(): Flow<List<VideoLink>> {
        return videoLinkDao.getAllVideoLinks()
    }

    fun getDefaultVideoLink(): Flow<VideoLink?> {
        return videoLinkDao.getDefaultVideoLink()
    }

    fun getVideoLinkById(id: Long): Flow<VideoLink?> {
        return videoLinkDao.getVideoLinkById(id)
    }

    suspend fun insertVideoLink(videoLink: VideoLink): Long {
        return videoLinkDao.insert(videoLink)
    }

    suspend fun updateVideoLink(videoLink: VideoLink) {
        videoLinkDao.update(videoLink)
    }

    suspend fun deleteVideoLink(videoLink: VideoLink) {
        videoLinkDao.delete(videoLink)
    }

    suspend fun setDefaultVideoLink(videoLink: VideoLink) {
        videoLinkDao.setDefault(videoLink)
    }

    suspend fun initializeDefaultLinks() {
        // 初始化默认视频链接
        val defaultLink = VideoLink(
            link = "https://b23.tv/56h7R7n",
            title = "冥想音乐 - 深度放松",
            isDefault = true
        )
        insertVideoLink(defaultLink)
    }
}
