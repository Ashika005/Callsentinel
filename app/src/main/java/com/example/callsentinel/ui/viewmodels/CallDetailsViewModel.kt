package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.model.TrustedNumber
import com.example.callsentinel.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallDetailsViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _callDetails = MutableStateFlow<SuspiciousCall?>(null)
    val callDetails: StateFlow<SuspiciousCall?> = _callDetails

    fun loadCallDetails(callId: Int, phoneNumber: String) {
        viewModelScope.launch {
            callRepository.allSuspiciousCalls.observeForever { calls ->
                _callDetails.value = calls.find { it.id == callId || it.number == phoneNumber }
            }
        }
    }

    fun blockNumber() {
        viewModelScope.launch {
            _callDetails.value?.let { call ->
                val blockedNumber = BlockedNumber(
                    number = call.number,
                    blockedAt = System.currentTimeMillis()
                )
                callRepository.insertBlockedNumber(blockedNumber)
            }
        }
    }

    fun trustNumber() {
        viewModelScope.launch {
            _callDetails.value?.let { call ->
                val trustedNumber = TrustedNumber(
                    number = call.number,
                    name = "Trusted Contact",
                    addedAt = System.currentTimeMillis()
                )
                callRepository.insertTrustedNumber(trustedNumber)
            }
        }
    }

    fun deleteCall() {
        viewModelScope.launch {
            // Note: Would need to add delete method to repository
        }
    }
}