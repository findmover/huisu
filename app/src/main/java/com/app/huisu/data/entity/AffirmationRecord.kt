package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affirmation_records")
data class AffirmationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val startTime: Long,
    val duration: Int, // in seconds
    val isCompleted: Boolean,
    val createDate: Long
)
