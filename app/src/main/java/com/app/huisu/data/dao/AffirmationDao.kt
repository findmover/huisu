package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.Affirmation
import kotlinx.coroutines.flow.Flow

@Dao
interface AffirmationDao {
    @Query("SELECT * FROM affirmations WHERE isActive = 1 ORDER BY `order` ASC")
    fun getActiveAffirmations(): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations ORDER BY `order` ASC")
    fun getAllAffirmations(): Flow<List<Affirmation>>

    @Query("SELECT * FROM affirmations WHERE id = :id")
    fun getAffirmationById(id: Long): Flow<Affirmation?>

    @Insert
    suspend fun insert(affirmation: Affirmation): Long

    @Update
    suspend fun update(affirmation: Affirmation)

    @Delete
    suspend fun delete(affirmation: Affirmation)

    @Query("DELETE FROM affirmations")
    suspend fun deleteAll()
}
