package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.MeditationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationDao {
    @Query("SELECT * FROM meditation_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<MeditationRecord>>

    @Query("SELECT * FROM meditation_records WHERE createDate >= :startDate AND createDate <= :endDate ORDER BY startTime DESC")
    fun getRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<MeditationRecord>>

    @Query("SELECT * FROM meditation_records WHERE createDate = :date ORDER BY startTime DESC")
    fun getRecordsByDate(date: Long): Flow<List<MeditationRecord>>

    @Query("SELECT SUM(duration) FROM meditation_records WHERE createDate >= :startDate AND createDate <= :endDate")
    fun getTotalDurationByDateRange(startDate: Long, endDate: Long): Flow<Int?>

    @Query("SELECT COUNT(*) FROM meditation_records WHERE createDate >= :startDate AND createDate <= :endDate")
    fun getCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM meditation_records")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT SUM(duration) FROM meditation_records")
    fun getTotalDuration(): Flow<Int?>

    @Insert
    suspend fun insert(record: MeditationRecord): Long

    @Update
    suspend fun update(record: MeditationRecord)

    @Delete
    suspend fun delete(record: MeditationRecord)

    @Query("DELETE FROM meditation_records")
    suspend fun deleteAll()
}
