package com.app.huisu.data.repository

import com.app.huisu.data.dao.TodoCategoryDao
import com.app.huisu.data.dao.TodoItemDao
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.TodoPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoCategoryDao: TodoCategoryDao,
    private val todoItemDao: TodoItemDao
) {
    // 分类相关
    fun getAllCategories(): Flow<List<TodoCategory>> = todoCategoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): TodoCategory? = todoCategoryDao.getCategoryById(id)

    suspend fun insertCategory(category: TodoCategory): Long = todoCategoryDao.insertCategory(category)

    suspend fun updateCategory(category: TodoCategory) = todoCategoryDao.updateCategory(category)

    suspend fun deleteCategory(category: TodoCategory) = todoCategoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) = todoCategoryDao.deleteCategoryById(id)

    suspend fun getCategoryCount(): Int = todoCategoryDao.getCategoryCount()

    // TODO项目相关
    fun getAllTodos(): Flow<List<TodoItem>> = todoItemDao.getAllTodos()

    fun getTodosByCategory(categoryId: Long): Flow<List<TodoItem>> =
        todoItemDao.getTodosByCategory(categoryId)

    fun getTodosByCompletedStatus(completed: Boolean): Flow<List<TodoItem>> =
        todoItemDao.getTodosByCompletedStatus(completed)

    fun getTodosByCategoryAndCompleted(categoryId: Long, completed: Boolean): Flow<List<TodoItem>> =
        todoItemDao.getTodosByCategoryAndCompleted(categoryId, completed)

    suspend fun getOverdueTodos(): List<TodoItem> =
        todoItemDao.getOverdueTodos(System.currentTimeMillis())

    suspend fun getTodoById(id: Long): TodoItem? = todoItemDao.getTodoById(id)

    suspend fun insertTodo(todo: TodoItem): Long = todoItemDao.insertTodo(todo)

    suspend fun updateTodo(todo: TodoItem) = todoItemDao.updateTodo(todo)

    suspend fun deleteTodo(todo: TodoItem) = todoItemDao.deleteTodo(todo)

    suspend fun deleteTodoById(id: Long) = todoItemDao.deleteTodoById(id)

    suspend fun updateTodoCompletionStatus(id: Long, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        todoItemDao.updateTodoCompletionStatus(
            id = id,
            completed = completed,
            completedAt = completedAt,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun getTodoCount(): Int = todoItemDao.getTodoCount()

    suspend fun getCompletedTodoCount(): Int = todoItemDao.getCompletedTodoCount()

    suspend fun getPendingTodoCount(): Int = todoItemDao.getPendingTodoCount()

    // 计算函数
    suspend fun getCompletionRate(categoryId: Long? = null): Float {
        val total = if (categoryId != null) {
            todoItemDao.getTodosByCategory(categoryId).first().size
        } else {
            getTodoCount()
        }

        if (total == 0) return 0f

        val completed = if (categoryId != null) {
            todoItemDao.getTodosByCategoryAndCompleted(categoryId, true).first().size
        } else {
            getCompletedTodoCount()
        }

        return (completed.toFloat() / total) * 100f
    }

    // 初始化默认分类
    suspend fun initializeDefaultCategories() {
        if (getCategoryCount() == 0) {
            val defaultCategories = listOf(
                TodoCategory(
                    name = "工作",
                    color = "#FF667EEA",
                    icon = "💼"
                ),
                TodoCategory(
                    name = "学习",
                    color = "#FF10B981",
                    icon = "📚"
                ),
                TodoCategory(
                    name = "生活",
                    color = "#FFF59E0B",
                    icon = "🏠"
                ),
                TodoCategory(
                    name = "健康",
                    color = "#FFEF4444",
                    icon = "💪"
                ),
                TodoCategory(
                    name = "其他",
                    color = "#FF8B5CF6",
                    icon = "📌"
                )
            )

            defaultCategories.forEach { category ->
                insertCategory(category)
            }
        }
    }
}