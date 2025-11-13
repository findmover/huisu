package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {
    @Query("SELECT * FROM todo_categories ORDER BY createdAt ASC")
    fun getAllCategories(): Flow<List<TodoCategory>>

    @Query("SELECT * FROM todo_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): TodoCategory?

    @Insert
    suspend fun insertCategory(category: TodoCategory): Long

    @Update
    suspend fun updateCategory(category: TodoCategory)

    @Delete
    suspend fun deleteCategory(category: TodoCategory)

    @Query("DELETE FROM todo_categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("SELECT COUNT(*) FROM todo_categories")
    suspend fun getCategoryCount(): Int
}