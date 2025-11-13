package com.app.huisu.ui.affirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.AffirmationRecord
import com.app.huisu.data.repository.AffirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationRecordsViewModel @Inject constructor(
    private val affirmationRepository: AffirmationRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<AffirmationRecord>>(emptyList())
    val records: StateFlow<List<AffirmationRecord>> = _records.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            affirmationRepository.getAllRecords().collect { recordList ->
                _records.value = recordList
            }
        }
    }

    fun deleteRecord(record: AffirmationRecord) {
        viewModelScope.launch {
            affirmationRepository.deleteRecord(record)
        }
    }
}
