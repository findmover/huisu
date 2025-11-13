package com.app.huisu.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.MeditationRecord
import com.app.huisu.data.repository.MeditationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeditationRecordsViewModel @Inject constructor(
    private val meditationRepository: MeditationRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<MeditationRecord>>(emptyList())
    val records: StateFlow<List<MeditationRecord>> = _records.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            meditationRepository.getAllRecords().collect { recordList ->
                _records.value = recordList
            }
        }
    }

    fun deleteRecord(record: MeditationRecord) {
        viewModelScope.launch {
            meditationRepository.deleteRecord(record)
        }
    }
}
