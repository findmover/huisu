package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_note_images")
data class QuickNoteImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val fileName: String,
    val mimeType: String,
    val dataBase64: String,
    val sizeBytes: Long,
    val status: QuickNoteImageStatus = QuickNoteImageStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class QuickNoteImageStatus(val displayName: String) {
    ACTIVE("有效"),
    DELETED("删除")
}

