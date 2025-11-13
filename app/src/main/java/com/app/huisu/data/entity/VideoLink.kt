package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_links")
data class VideoLink(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val link: String,
    val title: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
