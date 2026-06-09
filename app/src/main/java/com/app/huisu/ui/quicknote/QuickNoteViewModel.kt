package com.app.huisu.ui.quicknote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.QuickNote
import com.app.huisu.data.entity.QuickNoteImage
import com.app.huisu.data.entity.QuickNoteSpace
import com.app.huisu.data.entity.QuickNoteStatus
import com.app.huisu.data.entity.QuickNoteType
import com.app.huisu.data.entity.generateQuickNoteTitle
import com.app.huisu.data.entity.tagList
import com.app.huisu.data.repository.QuickNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuickNoteUiState(
    val notes: List<QuickNote> = emptyList(),
    val imagesByNoteId: Map<Long, List<QuickNoteImage>> = emptyMap(),
    val popularTags: List<QuickNoteTagCount> = emptyList(),
    val query: String = "",
    val selectedSpace: QuickNoteSpaceFilter = QuickNoteSpaceFilter.ALL,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

data class QuickNoteTagCount(
    val label: String,
    val count: Int
)

enum class QuickNoteSpaceFilter(val label: String) {
    ALL("全部"),
    PERSONAL("个人"),
    WORK("工作"),
    KEY("密钥")
}

@HiltViewModel
class QuickNoteViewModel @Inject constructor(
    private val quickNoteRepository: QuickNoteRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedSpace = MutableStateFlow(QuickNoteSpaceFilter.ALL)
    private val error = MutableStateFlow<String?>(null)
    private val isRefreshing = MutableStateFlow(false)

    private val filters = combine(
        query,
        selectedSpace,
        error,
        isRefreshing
    ) { queryText, spaceFilter, errorMessage, refreshing ->
        QuickNoteFilters(
            query = queryText,
            selectedSpace = spaceFilter,
            error = errorMessage,
            isRefreshing = refreshing
        )
    }

    val uiState: StateFlow<QuickNoteUiState> = combine(
        quickNoteRepository.getAllNotes(),
        quickNoteRepository.getAllImages(),
        filters
    ) { notes, images, filters ->
        val visibleNotes = notes
            .asSequence()
            .filter { it.status != QuickNoteStatus.DELETED }
            .filter { matchesSpace(it, filters.selectedSpace) }
            .filter { matchesQuery(it, filters.query) }
            .sortedWith(
                compareByDescending<QuickNote> { it.isPinned }
                    .thenByDescending { it.updatedAt }
            )
            .toList()
        val visibleNoteIds = visibleNotes.map { it.id }.toSet()

        QuickNoteUiState(
            notes = visibleNotes,
            imagesByNoteId = images
                .filter { it.noteId in visibleNoteIds }
                .groupBy { it.noteId },
            popularTags = buildPopularTags(notes),
            query = filters.query,
            selectedSpace = filters.selectedSpace,
            isRefreshing = filters.isRefreshing,
            error = filters.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuickNoteUiState()
    )

    fun updateQuery(value: String) {
        query.value = value
    }

    fun selectSpace(filter: QuickNoteSpaceFilter) {
        selectedSpace.value = filter
    }

    fun applyTagQuery(tag: String) {
        query.value = tag
    }

    fun clearFilters() {
        query.value = ""
        selectedSpace.value = QuickNoteSpaceFilter.ALL
    }

    fun refreshFromCloud() {
        viewModelScope.launch {
            if (isRefreshing.value) return@launch
            isRefreshing.value = true
            val startedAt = System.currentTimeMillis()
            val result = runCatching { quickNoteRepository.refreshFromCloud() }
            val remainingDelay = 700L - (System.currentTimeMillis() - startedAt)
            if (remainingDelay > 0) {
                delay(remainingDelay)
            }
            result.onFailure { error.value = "刷新云端数据失败：${it.message}" }
            isRefreshing.value = false
        }
    }

    fun addNote(
        title: String,
        content: String,
        type: QuickNoteType,
        space: QuickNoteSpace,
        tags: String,
        isFavorite: Boolean,
        isPinned: Boolean
    ) {
        val cleanContent = content.trim()
        if (cleanContent.isBlank()) {
            error.value = "正文不能为空"
            return
        }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                quickNoteRepository.insert(
                    QuickNote(
                        title = title.trim().ifBlank { generateQuickNoteTitle(now) },
                        content = cleanContent,
                        type = type,
                        space = space,
                        tags = normalizeTags(tags),
                        isFavorite = isFavorite,
                        isPinned = isPinned,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } catch (exception: Exception) {
                error.value = "保存记录失败：${exception.message}"
            }
        }
    }

    fun updateNote(
        note: QuickNote,
        title: String,
        content: String,
        type: QuickNoteType,
        space: QuickNoteSpace,
        tags: String,
        isFavorite: Boolean,
        isPinned: Boolean
    ) {
        val cleanContent = content.trim()
        if (cleanContent.isBlank()) {
            error.value = "正文不能为空"
            return
        }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                quickNoteRepository.update(
                    note.copy(
                        title = title.trim().ifBlank { generateQuickNoteTitle(note.createdAt) },
                        content = cleanContent,
                        type = type,
                        space = space,
                        tags = normalizeTags(tags),
                        isFavorite = isFavorite,
                        isPinned = isPinned,
                        updatedAt = now
                    )
                )
            } catch (exception: Exception) {
                error.value = "更新记录失败：${exception.message}"
            }
        }
    }

    fun toggleFavorite(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.toggleFavorite(noteId) }
                .onFailure { error.value = "更新收藏状态失败：${it.message}" }
        }
    }

    fun togglePinned(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.togglePinned(noteId) }
                .onFailure { error.value = "更新置顶状态失败：${it.message}" }
        }
    }

    fun delete(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.softDelete(noteId) }
                .onFailure { error.value = "删除失败：${it.message}" }
        }
    }

    fun clearError() {
        error.value = null
    }

    private data class QuickNoteFilters(
        val query: String,
        val selectedSpace: QuickNoteSpaceFilter,
        val error: String?,
        val isRefreshing: Boolean
    )

    companion object {
        private fun matchesSpace(note: QuickNote, filter: QuickNoteSpaceFilter): Boolean {
            return when (filter) {
                QuickNoteSpaceFilter.ALL -> true
                QuickNoteSpaceFilter.PERSONAL -> note.space == QuickNoteSpace.PERSONAL
                QuickNoteSpaceFilter.WORK -> note.space == QuickNoteSpace.WORK
                QuickNoteSpaceFilter.KEY -> note.space == QuickNoteSpace.KEY
            }
        }

        private fun matchesQuery(note: QuickNote, query: String): Boolean {
            if (query.isBlank()) return true

            val normalizedQuery = query.trim().lowercase()
            val searchText = buildString {
                append(note.title.lowercase())
                append('\n')
                append(note.content.lowercase())
                append('\n')
                append(note.tags.lowercase())
                append('\n')
                append(note.space.displayName.lowercase())
            }
            return searchText.contains(normalizedQuery)
        }

        private fun buildPopularTags(notes: List<QuickNote>): List<QuickNoteTagCount> {
            return notes
                .asSequence()
                .filter { it.status != QuickNoteStatus.DELETED }
                .flatMap { it.tagList().asSequence() }
                .groupingBy { it }
                .eachCount()
                .map { QuickNoteTagCount(label = it.key, count = it.value) }
                .sortedWith(
                    compareByDescending<QuickNoteTagCount> { it.count }
                        .thenBy { it.label }
                )
                .take(8)
                .toList()
        }

        private fun normalizeTags(raw: String): String {
            return raw
                .split(Regex("[,，、\\n]"))
                .map { it.trim().trimStart('#').trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .joinToString(", ")
        }
    }
}
