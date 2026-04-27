package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "quick_notes")
data class QuickNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val type: QuickNoteType = QuickNoteType.NOTE,
    val space: QuickNoteSpace = QuickNoteSpace.PERSONAL,
    val tags: String = "",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val status: QuickNoteStatus = QuickNoteStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class QuickNoteType(val displayName: String) {
    MEMORY("记忆"),
    WORK("工作记录"),
    IDEA("灵感"),
    NOTE("笔记"),
    TODO("待办"),
    MATERIAL("资料")
}

enum class QuickNoteSpace(val displayName: String) {
    PERSONAL("个人"),
    WORK("工作"),
    KEY("密钥")
}

enum class QuickNoteStatus(val displayName: String) {
    ACTIVE("有效"),
    ARCHIVED("归档"),
    DELETED("删除")
}

fun QuickNote.tagList(): List<String> {
    return tags
        .split(Regex("[,，、\\n]"))
        .map { it.trim().trimStart('#').trim() }
        .filter { it.isNotEmpty() }
        .distinct()
}

fun generateQuickNoteTitle(timestamp: Long = System.currentTimeMillis()): String {
    return "${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))} 记录"
}
