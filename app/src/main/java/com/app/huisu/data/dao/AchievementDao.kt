package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY category, targetValue")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE unlocked = 1 ORDER BY unlockedDate DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE `key` = :key LIMIT 1")
    fun getAchievementByKey(key: String): Flow<Achievement?>

    @Query("SELECT * FROM achievements WHERE `key` = :key LIMIT 1")
    suspend fun getAchievementByKeySync(key: String): Achievement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement): Long

    @Update
    suspend fun update(achievement: Achievement)

    @Delete
    suspend fun delete(achievement: Achievement)

    @Query("SELECT * FROM achievements WHERE unlocked = 1 AND notificationShown = 0")
    suspend fun getNewlyUnlockedAchievements(): List<Achievement>

    @Query("UPDATE achievements SET notificationShown = :shown WHERE id = :id")
    suspend fun updateNotificationShown(id: Long, shown: Boolean)

    @Query("DELETE FROM achievements")
    suspend fun deleteAll()
}
