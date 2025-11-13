package com.app.huisu.data.repository

import com.app.huisu.data.dao.AffirmationDao
import com.app.huisu.data.dao.AffirmationRecordDao
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.data.entity.AffirmationRecord
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AffirmationRepository @Inject constructor(
    private val affirmationDao: AffirmationDao,
    private val affirmationRecordDao: AffirmationRecordDao
) {

    // Affirmation CRUD
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
        return affirmationDao.insert(affirmation)
    }

    suspend fun updateAffirmation(affirmation: Affirmation) {
        affirmationDao.update(affirmation)
    }

    suspend fun deleteAffirmation(affirmation: Affirmation) {
        affirmationDao.delete(affirmation)
    }

    // Affirmation Records
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
        return affirmationRecordDao.insert(record)
    }

    suspend fun updateRecord(record: AffirmationRecord) {
        affirmationRecordDao.update(record)
    }

    suspend fun deleteRecord(record: AffirmationRecord) {
        affirmationRecordDao.delete(record)
    }

    suspend fun initializeDefaultAffirmations() {
        // 初始化默认暗示语
        val defaultAffirmations = listOf(
            Affirmation(content = "我充满力量和自信,每一天都在变得更好,我值得拥有美好的生活", order = 0),
            Affirmation(content = "我专注当下,活在此刻,内心充满平静和喜悦", order = 1),
            Affirmation(content = "我接纳自己的一切,我正在成为更好的自己", order = 2)
        )
        defaultAffirmations.forEach { insertAffirmation(it) }
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
