package com.app.huisu.data.repository

import com.app.huisu.data.dao.AffirmationDao
import com.app.huisu.data.dao.AffirmationRecordDao
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.data.entity.AffirmationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AffirmationRepository @Inject constructor(
    private val affirmationDao: AffirmationDao,
    private val affirmationRecordDao: AffirmationRecordDao,
    private val cloudSyncRepository: CloudSyncRepository
) {

    fun getActiveAffirmations(): Flow<List<Affirmation>> {
        return affirmationDao.getActiveAffirmations()
    }

    fun getAllAffirmations(): Flow<List<Affirmation>> {
        return affirmationDao.getAllAffirmations()
    }

    fun getAffirmationById(id: Long): Flow<Affirmation?> {
        return affirmationDao.getAffirmationById(id)
    }

    suspend fun insertAffirmation(affirmation: Affirmation): Long {
        return affirmationDao.insert(affirmation).also {
            cloudSyncRepository.requestAutoUpload()
        }
    }

    suspend fun updateAffirmation(affirmation: Affirmation) {
        affirmationDao.update(affirmation)
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun deleteAffirmation(affirmation: Affirmation) {
        affirmationDao.delete(affirmation)
        if (affirmation.isSelected) {
            ensureSelectedAffirmation()
        }
        cloudSyncRepository.requestAutoUpload()
    }

    fun getAllRecords(): Flow<List<AffirmationRecord>> {
        return affirmationRecordDao.getAllRecords()
    }

    fun getRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<AffirmationRecord>> {
        return affirmationRecordDao.getRecordsByDateRange(startDate, endDate)
    }

    fun getTodayCompletedCount(): Flow<Int> {
        val today = getTodayStartMillis()
        val tomorrow = today + 24 * 60 * 60 * 1000
        return affirmationRecordDao.getCompletedCountByDateRange(today, tomorrow)
    }

    fun getThisWeekCompletedCount(): Flow<Int> {
        val (start, end) = getThisWeekRange()
        return affirmationRecordDao.getCompletedCountByDateRange(start, end)
    }

    fun getThisWeekTotalCount(): Flow<Int> {
        val (start, end) = getThisWeekRange()
        return affirmationRecordDao.getTotalCountByDateRange(start, end)
    }

    fun getThisMonthCompletedCount(): Flow<Int> {
        val (start, end) = getThisMonthRange()
        return affirmationRecordDao.getCompletedCountByDateRange(start, end)
    }

    fun getThisMonthTotalCount(): Flow<Int> {
        val (start, end) = getThisMonthRange()
        return affirmationRecordDao.getTotalCountByDateRange(start, end)
    }

    fun getTotalDuration(): Flow<Int?> {
        return affirmationRecordDao.getTotalDurationByDateRange(0, Long.MAX_VALUE)
    }

    fun getTotalCompletedCount(): Flow<Int> {
        return affirmationRecordDao.getCompletedCountByDateRange(0, Long.MAX_VALUE)
    }

    suspend fun insertRecord(record: AffirmationRecord): Long {
        return affirmationRecordDao.insert(record).also {
            cloudSyncRepository.requestAutoUpload()
        }
    }

    suspend fun updateRecord(record: AffirmationRecord) {
        affirmationRecordDao.update(record)
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun deleteRecord(record: AffirmationRecord) {
        affirmationRecordDao.delete(record)
        cloudSyncRepository.requestAutoUpload()
    }

    suspend fun initializeDefaultAffirmations() {
        val existingAffirmations = affirmationDao.getAllAffirmations().first()
        if (existingAffirmations.isNotEmpty()) {
            ensureSelectedAffirmation(existingAffirmations)
            return
        }

        val defaultAffirmations = listOf(
            Affirmation(
                content = "我充满力量和自信，每一天都在变得更好。我值得拥有美好的生活。",
                isSelected = true,
                order = 0
            ),
            Affirmation(
                content = "我专注当下，活在此刻，内心充满平静和喜悦。",
                order = 1
            ),
            Affirmation(
                content = "我接纳自己的每一部分，我正在成为更好的自己。",
                order = 2
            )
        )
        defaultAffirmations.forEach { insertAffirmation(it) }
    }

    suspend fun selectAffirmation(affirmationId: Long) {
        val affirmations = affirmationDao.getAllAffirmations().first()
        if (affirmations.isEmpty()) return

        affirmations.forEach { affirmation ->
            val shouldBeSelected = affirmation.id == affirmationId
            if (affirmation.isSelected != shouldBeSelected) {
                affirmationDao.update(affirmation.copy(isSelected = shouldBeSelected))
            }
        }
    }

    private suspend fun ensureSelectedAffirmation(
        affirmations: List<Affirmation>? = null
    ) {
        val currentAffirmations = affirmations ?: affirmationDao.getAllAffirmations().first()
        if (currentAffirmations.isEmpty()) return

        val selectedId = currentAffirmations.firstOrNull { it.isSelected }?.id ?: currentAffirmations.first().id
        currentAffirmations.forEach { affirmation ->
            val shouldBeSelected = affirmation.id == selectedId
            if (affirmation.isSelected != shouldBeSelected) {
                affirmationDao.update(affirmation.copy(isSelected = shouldBeSelected))
            }
        }
        cloudSyncRepository.requestAutoUpload()
    }

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getThisWeekRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    private fun getThisMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }
}
