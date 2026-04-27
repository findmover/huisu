package com.app.huisu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.huisu.data.entity.QuickNote
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickNoteDao {
    @Query("SELECT * FROM quick_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<QuickNote>>

    @Query("SELECT * FROM quick_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): QuickNote?

    @Insert
    suspend fun insert(note: QuickNote): Long

    @Update
    suspend fun update(note: QuickNote)
}
