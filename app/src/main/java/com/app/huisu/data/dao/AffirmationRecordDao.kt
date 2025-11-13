package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.AffirmationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AffirmationRecordDao {
    @Query("SELECT * FROM affirmation_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<AffirmationRecord>>

    @Query("SELECT * FROM affirmation_records WHERE createDate >= :startDate AND createDate <= :endDate ORDER BY startTime DESC")
    fun getRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<AffirmationRecord>>

    @Query("SELECT * FROM affirmation_records WHERE createDate = :date ORDER BY startTime DESC")
    fun getRecordsByDate(date: Long): Flow<List<AffirmationRecord>>

    @Query("SELECT COUNT(*) FROM affirmation_records WHERE createDate >= :startDate AND createDate <= :endDate AND isCompleted = 1")
    fun getCompletedCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM affirmation_records WHERE createDate >= :startDate AND createDate <= :endDate")
    fun getTotalCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT SUM(duration) FROM affirmation_records WHERE createDate >= :startDate AND createDate <= :endDate")
    fun getTotalDurationByDateRange(startDate: Long, endDate: Long): Flow<Int?>

    @Insert
    suspend fun insert(record: AffirmationRecord): Long

    @Update
    suspend fun update(record: AffirmationRecord)

    @Delete
    suspend fun delete(record: AffirmationRecord)

    @Query("DELETE FROM affirmation_records")
    suspend fun deleteAll()
}
