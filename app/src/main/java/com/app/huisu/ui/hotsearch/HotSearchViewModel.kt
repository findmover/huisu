package com.app.huisu.ui.hotsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.HotSearchItem
import com.app.huisu.data.entity.HotSearchPlatform
import com.app.huisu.data.repository.HotSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 热搜页面UI状态
 */
data class HotSearchUiState(
    val isLoading: Boolean = false,
    val hotSearchList: List<HotSearchItem> = emptyList(),
    val selectedPlatform: HotSearchPlatform = HotSearchPlatform.WEIBO,
    val errorMessage: String? = null
)

/**
 * 热搜ViewModel
 */
@HiltViewModel
class HotSearchViewModel @Inject constructor(
    private val repository: HotSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotSearchUiState())
    val uiState: StateFlow<HotSearchUiState> = _uiState.asStateFlow()

    init {
        // 初始加载默认平台(微博)的热搜
        loadHotSearch(_uiState.value.selectedPlatform)
    }

    /**
     * 切换平台
     */
    fun selectPlatform(platform: HotSearchPlatform) {
        _uiState.update { it.copy(selectedPlatform = platform) }
        loadHotSearch(platform)
    }

    /**
     * 刷新当前平台的热搜
     */
    fun refresh() {
        loadHotSearch(_uiState.value.selectedPlatform)
    }

    /**
     * 加载热搜数据
     */
    private fun loadHotSearch(platform: HotSearchPlatform) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getHotSearchList(platform)
                .onSuccess { hotSearchList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hotSearchList = hotSearchList,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hotSearchList = emptyList(),
                            errorMessage = exception.message ?: "加载失败"
                        )
                    }
                }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
