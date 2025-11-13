package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affirmations")
data class Affirmation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isActive: Boolean = true,
    val isSelected: Boolean = false, // 当前选中的暗示语
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
