package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "meditation_records")
data class MeditationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // in seconds
    val videoLink: String,
    val createDate: Long
)
