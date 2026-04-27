package com.app.huisu.data.repository

import com.app.huisu.data.dao.QuickNoteDao
import com.app.huisu.data.entity.QuickNote
import com.app.huisu.data.entity.QuickNoteStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickNoteRepository @Inject constructor(
    private val quickNoteDao: QuickNoteDao,
    private val cloudSyncRepository: CloudSyncRepository
) {

    fun getAllNotes(): Flow<List<QuickNote>> = quickNoteDao.getAllNotes()

    suspend fun insert(note: QuickNote): Long {
        return quickNoteDao.insert(note).also {
            cloudSyncRepository.requestAutoUpload()
        }
    }

    suspend fun update(note: QuickNote) {
        quickNoteDao.update(note)
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun toggleFavorite(noteId: Long) {
        val note = quickNoteDao.getNoteById(noteId) ?: return
        quickNoteDao.update(
            note.copy(
                isFavorite = !note.isFavorite,
                updatedAt = System.currentTimeMillis()
            )
        )
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun togglePinned(noteId: Long) {
        val note = quickNoteDao.getNoteById(noteId) ?: return
        quickNoteDao.update(
            note.copy(
                isPinned = !note.isPinned,
                updatedAt = System.currentTimeMillis()
            )
        )
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun archive(noteId: Long) {
        val note = quickNoteDao.getNoteById(noteId) ?: return
        quickNoteDao.update(
            note.copy(
                status = QuickNoteStatus.ARCHIVED,
                isPinned = false,
                updatedAt = System.currentTimeMillis()
            )
        )
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun restore(noteId: Long) {
        val note = quickNoteDao.getNoteById(noteId) ?: return
        quickNoteDao.update(
            note.copy(
                status = QuickNoteStatus.ACTIVE,
                updatedAt = System.currentTimeMillis()
            )
        )
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun softDelete(noteId: Long) {
        val note = quickNoteDao.getNoteById(noteId) ?: return
        quickNoteDao.update(
            note.copy(
                status = QuickNoteStatus.DELETED,
                isPinned = false,
                updatedAt = System.currentTimeMillis()
            )
        )
        cloudSyncRepository.requestAutoUpload()
    }
}
