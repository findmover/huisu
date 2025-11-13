package com.app.huisu.data.repository

import com.app.huisu.data.dao.MeditationDao
import com.app.huisu.data.entity.MeditationRecord
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeditationRepository @Inject constructor(
    private val meditationDao: MeditationDao
) {

    fun getAllRecords(): Flow<List<MeditationRecord>> {
        return meditationDao.getAllRecords()
    }

    fun getRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<MeditationRecord>> {
        return meditationDao.getRecordsByDateRange(startDate, endDate)
    }

    fun getTodayRecords(): Flow<List<MeditationRecord>> {
        val today = getTodayStartMillis()
        return meditationDao.getRecordsByDate(today)
    }

    fun getTodayDuration(): Flow<Int?> {
        val today = getTodayStartMillis()
        val tomorrow = today + 24 * 60 * 60 * 1000
        return meditationDao.getTotalDurationByDateRange(today, tomorrow)
    }

    fun getTodayCount(): Flow<Int> {
        val today = getTodayStartMillis()
        val tomorrow = today + 24 * 60 * 60 * 1000
        return meditationDao.getCountByDateRange(today, tomorrow)
    }

    fun getThisWeekDuration(): Flow<Int?> {
        val (start, end) = getThisWeekRange()
        return meditationDao.getTotalDurationByDateRange(start, end)
    }

    fun getThisWeekCount(): Flow<Int> {
        val (start, end) = getThisWeekRange()
        return meditationDao.getCountByDateRange(start, end)
    }

    fun getThisMonthDuration(): Flow<Int?> {
        val (start, end) = getThisMonthRange()
        return meditationDao.getTotalDurationByDateRange(start, end)
    }

    fun getThisMonthCount(): Flow<Int> {
        val (start, end) = getThisMonthRange()
        return meditationDao.getCountByDateRange(start, end)
    }

    fun getTotalCount(): Flow<Int> {
        return meditationDao.getTotalCount()
    }

    fun getTotalDuration(): Flow<Int?> {
        return meditationDao.getTotalDuration()
    }

    suspend fun insertRecord(record: MeditationRecord): Long {
        return meditationDao.insert(record)
    }

    suspend fun updateRecord(record: MeditationRecord) {
        meditationDao.update(record)
    }

    suspend fun deleteRecord(record: MeditationRecord) {
        meditationDao.delete(record)
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
