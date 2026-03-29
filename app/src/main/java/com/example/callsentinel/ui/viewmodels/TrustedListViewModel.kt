package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.data.model.TrustedNumber
import com.example.callsentinel.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustedListViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _trustedNumbers = MutableStateFlow<List<TrustedNumber>>(emptyList())
    val trustedNumbers: StateFlow<List<TrustedNumber>> = _trustedNumbers

    init {
        viewModelScope.launch {
            callRepository.allTrustedNumbers.observeForever { numbers ->
                _trustedNumbers.value = numbers
            }
        }
    }

    fun addTrustedNumber(name: String, number: String) {
        viewModelScope.launch {
            val trustedNumber = TrustedNumber(
                number = number,
                name = name,
                addedAt = System.currentTimeMillis()
            )
            callRepository.insertTrustedNumber(trustedNumber)
        }
    }

    fun removeTrustedNumber(trustedNumber: TrustedNumber) {
        viewModelScope.launch {
            callRepository.deleteTrustedNumber(trustedNumber)
        }
    }
}