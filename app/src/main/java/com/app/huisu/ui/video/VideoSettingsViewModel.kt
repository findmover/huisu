package com.app.huisu.ui.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.VideoLink
import com.app.huisu.data.repository.VideoLinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoSettingsViewModel @Inject constructor(
    private val videoLinkRepository: VideoLinkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoSettingsUiState())
    val uiState: StateFlow<VideoSettingsUiState> = _uiState.asStateFlow()

    init {
        loadVideoLinks()
    }

    private fun loadVideoLinks() {
        viewModelScope.launch {
            videoLinkRepository.getAllVideoLinks().collect { links ->
                _uiState.update { it.copy(videoLinks = links) }
            }
        }
    }

    fun addVideoLink(title: String, link: String) {
        viewModelScope.launch {
            val videoLink = VideoLink(
                title = title,
                link = link,
                isDefault = _uiState.value.videoLinks.isEmpty()
            )
            videoLinkRepository.insertVideoLink(videoLink)
        }
    }

    fun updateVideoLink(videoLink: VideoLink) {
        viewModelScope.launch {
            videoLinkRepository.updateVideoLink(videoLink)
        }
    }

    fun deleteVideoLink(videoLink: VideoLink) {
        viewModelScope.launch {
            videoLinkRepository.deleteVideoLink(videoLink)
        }
    }

    fun setDefaultVideoLink(videoLink: VideoLink) {
        viewModelScope.launch {
            videoLinkRepository.setDefaultVideoLink(videoLink)
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(videoLink: VideoLink) {
        _uiState.update {
            it.copy(
                showEditDialog = true,
                editingVideoLink = videoLink
            )
        }
    }

    fun hideEditDialog() {
        _uiState.update {
            it.copy(
                showEditDialog = false,
                editingVideoLink = null
            )
        }
    }
}

data class VideoSettingsUiState(
    val videoLinks: List<VideoLink> = emptyList(),
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingVideoLink: VideoLink? = null
)
