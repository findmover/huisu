package com.app.huisu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.huisu.data.entity.QuickNoteImage
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickNoteImageDao {
    @Query("SELECT * FROM quick_note_images WHERE status != 'DELETED' ORDER BY updatedAt DESC")
    fun getAllImages(): Flow<List<QuickNoteImage>>

    @Query("SELECT * FROM quick_note_images WHERE noteId = :noteId AND status != 'DELETED' ORDER BY createdAt ASC")
    fun getImagesForNote(noteId: Long): Flow<List<QuickNoteImage>>

    @Insert
    suspend fun insert(image: QuickNoteImage): Long

    @Update
    suspend fun update(image: QuickNoteImage)
}

