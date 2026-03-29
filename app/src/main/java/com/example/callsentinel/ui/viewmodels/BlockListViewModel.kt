package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockListViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _blockedNumbers = MutableStateFlow<List<BlockedNumber>>(emptyList())
    val blockedNumbers: StateFlow<List<BlockedNumber>> = _blockedNumbers

    init {
        viewModelScope.launch {
            callRepository.allBlockedNumbers.observeForever { numbers ->
                _blockedNumbers.value = numbers
            }
        }
    }

    fun addBlockedNumber(number: String) {
        viewModelScope.launch {
            val blockedNumber = BlockedNumber(
                number = number,
                blockedAt = System.currentTimeMillis()
            )
            callRepository.insertBlockedNumber(blockedNumber)
        }
    }

    fun removeBlockedNumber(blockedNumber: BlockedNumber) {
        viewModelScope.launch {
            callRepository.deleteBlockedNumber(blockedNumber)
        }
    }
}