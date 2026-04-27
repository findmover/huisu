package com.app.huisu.ui.quicknote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.QuickNote
import com.app.huisu.data.entity.QuickNoteSpace
import com.app.huisu.data.entity.QuickNoteStatus
import com.app.huisu.data.entity.QuickNoteType
import com.app.huisu.data.entity.generateQuickNoteTitle
import com.app.huisu.data.entity.tagList
import com.app.huisu.data.repository.QuickNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuickNoteUiState(
    val notes: List<QuickNote> = emptyList(),
    val popularTags: List<QuickNoteTagCount> = emptyList(),
    val query: String = "",
    val selectedSpace: QuickNoteSpaceFilter = QuickNoteSpaceFilter.ALL,
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

    private val filters = combine(
        query,
        selectedSpace,
        error
    ) { queryText, spaceFilter, errorMessage ->
        QuickNoteFilters(
            query = queryText,
            selectedSpace = spaceFilter,
            error = errorMessage
        )
    }

    val uiState: StateFlow<QuickNoteUiState> = combine(
        quickNoteRepository.getAllNotes(),
        filters
    ) { notes, filters ->
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

        QuickNoteUiState(
            notes = visibleNotes,
            popularTags = buildPopularTags(notes),
            query = filters.query,
            selectedSpace = filters.selectedSpace,
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
                error.value = "保存记录失败: ${exception.message}"
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
                error.value = "更新记录失败: ${exception.message}"
            }
        }
    }

    fun toggleFavorite(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.toggleFavorite(noteId) }
                .onFailure { error.value = "更新收藏状态失败: ${it.message}" }
        }
    }

    fun togglePinned(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.togglePinned(noteId) }
                .onFailure { error.value = "更新置顶状态失败: ${it.message}" }
        }
    }

    fun delete(noteId: Long) {
        viewModelScope.launch {
            runCatching { quickNoteRepository.softDelete(noteId) }
                .onFailure { error.value = "删除失败: ${it.message}" }
        }
    }

    fun clearError() {
        error.value = null
    }

    private data class QuickNoteFilters(
        val query: String,
        val selectedSpace: QuickNoteSpaceFilter,
        val error: String?
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
