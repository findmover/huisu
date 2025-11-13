package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.VideoLink
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoLinkDao {
    @Query("SELECT * FROM video_links ORDER BY createdAt DESC")
    fun getAllVideoLinks(): Flow<List<VideoLink>>

    @Query("SELECT * FROM video_links WHERE isDefault = 1 LIMIT 1")
    fun getDefaultVideoLink(): Flow<VideoLink?>

    @Query("SELECT * FROM video_links WHERE id = :id")
    fun getVideoLinkById(id: Long): Flow<VideoLink?>

    @Insert
    suspend fun insert(videoLink: VideoLink): Long

    @Update
    suspend fun update(videoLink: VideoLink)

    @Delete
    suspend fun delete(videoLink: VideoLink)

    @Query("UPDATE video_links SET isDefault = 0")
    suspend fun clearAllDefaults()

    @Transaction
    suspend fun setDefault(videoLink: VideoLink) {
        clearAllDefaults()
        update(videoLink.copy(isDefault = true))
    }

    @Query("DELETE FROM video_links")
    suspend fun deleteAll()
}
