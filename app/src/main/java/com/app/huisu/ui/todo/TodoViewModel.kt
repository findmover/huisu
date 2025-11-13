package com.app.huisu.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.TodoPriority
import com.app.huisu.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoUiState(
    val categories: List<TodoCategory> = emptyList(),
    val todos: List<TodoItem> = emptyList(),
    val selectedCategoryId: Long? = null,
    val showCompletedOnly: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val categoryStats: Map<Long, CategoryStats> = emptyMap(),
    val overallStats: OverallStats = OverallStats()
)

data class CategoryStats(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val completionRate: Float = 0f
)

data class OverallStats(
    val totalTodos: Int = 0,
    val completedTodos: Int = 0,
    val pendingTodos: Int = 0,
    val overallCompletionRate: Float = 0f,
    val overdueCount: Int = 0
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        initializeData()
        loadData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                todoRepository.initializeDefaultCategories()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "初始化失败: ${e.message}"
                )
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                todoRepository.getAllCategories(),
                todoRepository.getAllTodos()
            ) { categories, todos ->
                calculateStats(categories, todos)
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    todos = getFilteredTodos(categories, todos),
                    overallStats = calculateOverallStats(todos)
                )
            }.collect {}
        }
    }

    private fun getFilteredTodos(
        categories: List<TodoCategory>,
        todos: List<TodoItem>
    ): List<TodoItem> {
        var filtered = todos

        // 按分类筛选
        _uiState.value.selectedCategoryId?.let { categoryId ->
            filtered = filtered.filter { it.categoryId == categoryId }
        }

        // 按完成状态筛选
        if (_uiState.value.showCompletedOnly) {
            filtered = filtered.filter { it.isCompleted }
        }

        return filtered
    }

    private fun calculateStats(categories: List<TodoCategory>, todos: List<TodoItem>) {
        val categoryStatsMap = mutableMapOf<Long, CategoryStats>()

        categories.forEach { category ->
            val categoryTodos = todos.filter { it.categoryId == category.id }
            val completed = categoryTodos.count { it.isCompleted }
            val total = categoryTodos.size
            val pending = total - completed
            val completionRate = if (total > 0) (completed.toFloat() / total) * 100f else 0f

            categoryStatsMap[category.id] = CategoryStats(
                total = total,
                completed = completed,
                pending = pending,
                completionRate = completionRate
            )
        }

        _uiState.value = _uiState.value.copy(categoryStats = categoryStatsMap)
    }

    private suspend fun calculateOverallStats(todos: List<TodoItem>): OverallStats {
        val completed = todos.count { it.isCompleted }
        val total = todos.size
        val pending = total - completed
        val completionRate = if (total > 0) (completed.toFloat() / total) * 100f else 0f
        val overdue = todoRepository.getOverdueTodos().size

        return OverallStats(
            totalTodos = total,
            completedTodos = completed,
            pendingTodos = pending,
            overallCompletionRate = completionRate,
            overdueCount = overdue
        )
    }

    fun selectCategory(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        // 重新筛选todos
        viewModelScope.launch {
            val todos = todoRepository.getAllTodos().first()
            val categories = _uiState.value.categories
            _uiState.value = _uiState.value.copy(
                todos = getFilteredTodos(categories, todos)
            )
        }
    }

    fun toggleShowCompleted() {
        _uiState.value = _uiState.value.copy(
            showCompletedOnly = !_uiState.value.showCompletedOnly
        )
        // 重新筛选todos
        viewModelScope.launch {
            val todos = todoRepository.getAllTodos().first()
            val categories = _uiState.value.categories
            _uiState.value = _uiState.value.copy(
                todos = getFilteredTodos(categories, todos)
            )
        }
    }

    fun addTodo(
        title: String,
        description: String = "",
        categoryId: Long,
        priority: TodoPriority = TodoPriority.MEDIUM,
        dueDate: Long? = null
    ) {
        viewModelScope.launch {
            try {
                val todo = TodoItem(
                    title = title,
                    description = description,
                    categoryId = categoryId,
                    priority = priority,
                    dueDate = dueDate
                )
                todoRepository.insertTodo(todo)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "添加TODO失败: ${e.message}"
                )
            }
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            try {
                todoRepository.updateTodo(todo.copy(updatedAt = System.currentTimeMillis()))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "更新TODO失败: ${e.message}"
                )
            }
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "删除TODO失败: ${e.message}"
                )
            }
        }
    }

    fun toggleTodoCompletion(todoId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.updateTodoCompletionStatus(todoId, !currentCompleted)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "更新完成状态失败: ${e.message}"
                )
            }
        }
    }

    fun addCategory(name: String, color: String, icon: String) {
        viewModelScope.launch {
            try {
                val category = TodoCategory(
                    name = name,
                    color = color,
                    icon = icon
                )
                todoRepository.insertCategory(category)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "添加分类失败: ${e.message}"
                )
            }
        }
    }

    fun updateCategory(category: TodoCategory) {
        viewModelScope.launch {
            try {
                todoRepository.updateCategory(category.copy(updatedAt = System.currentTimeMillis()))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "更新分类失败: ${e.message}"
                )
            }
        }
    }

    fun updateCategory(categoryId: Long, name: String, color: String, icon: String) {
        viewModelScope.launch {
            try {
                val categories = _uiState.value.categories
                val existingCategory = categories.find { it.id == categoryId }
                if (existingCategory != null) {
                    val updatedCategory = existingCategory.copy(
                        name = name,
                        color = color,
                        icon = icon,
                        updatedAt = System.currentTimeMillis()
                    )
                    todoRepository.updateCategory(updatedCategory)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "更新分类失败: ${e.message}"
                )
            }
        }
    }

    fun deleteCategory(category: TodoCategory) {
        viewModelScope.launch {
            try {
                todoRepository.deleteCategory(category)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "删除分类失败: ${e.message}"
                )
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val categories = _uiState.value.categories
                val category = categories.find { it.id == categoryId }
                if (category != null) {
                    todoRepository.deleteCategory(category)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "删除分类失败: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}