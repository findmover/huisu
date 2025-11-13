package com.app.huisu.data.dao

import androidx.room.*
import com.app.huisu.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getTodosByCategory(categoryId: Long): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByCompletedStatus(completed: Boolean): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE categoryId = :categoryId AND isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByCategoryAndCompleted(categoryId: Long, completed: Boolean): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE dueDate IS NOT NULL AND dueDate < :currentTime AND isCompleted = 0")
    suspend fun getOverdueTodos(currentTime: Long): List<TodoItem>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoItem?

    @Insert
    suspend fun insertTodo(todo: TodoItem): Long

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteTodoById(id: Long)

    @Query("UPDATE todo_items SET isCompleted = :completed, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoCompletionStatus(id: Long, completed: Boolean, completedAt: Long?, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM todo_items")
    suspend fun getTodoCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    suspend fun getCompletedTodoCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 0")
    suspend fun getPendingTodoCount(): Int
}