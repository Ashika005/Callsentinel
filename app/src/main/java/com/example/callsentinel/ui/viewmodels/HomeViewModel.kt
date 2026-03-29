package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.model.TrustedNumber
import com.example.callsentinel.data.repository.CallRepository
import com.example.callsentinel.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _recentSuspiciousCalls = MutableStateFlow<List<SuspiciousCall>>(emptyList())
    val recentSuspiciousCalls: StateFlow<List<SuspiciousCall>> = _recentSuspiciousCalls

    private val _isProtectionActive = MutableStateFlow(prefsManager.isProtectionActive)
    val isProtectionActive: StateFlow<Boolean> = _isProtectionActive

    data class HomeStats(
        val callsScanned: Int = 0,
        val threatsBlocked: Int = 0,
        val trustedCount: Int = 0
    )

    private val _stats = MutableStateFlow(HomeStats())
    val stats: StateFlow<HomeStats> = _stats

    // Keep references so we can remove observers in onCleared()
    private val suspiciousCallsObserver = Observer<List<SuspiciousCall>> { calls ->
        _recentSuspiciousCalls.value = calls
        updateStats(calls)
    }
    private val trustedNumbersObserver = Observer<List<TrustedNumber>> { trusted ->
        _stats.value = _stats.value.copy(trustedCount = trusted.size)
    }

    init {
        // observeForever must be called on the main thread — done here in init, which runs
        // on the main thread when the ViewModel is first created.
        callRepository.allSuspiciousCalls.observeForever(suspiciousCallsObserver)
        callRepository.allTrustedNumbers.observeForever(trustedNumbersObserver)
    }

    private fun updateStats(calls: List<SuspiciousCall>) {
        val blocked = calls.count { it.wasBlocked }
        _stats.value = _stats.value.copy(
            callsScanned = calls.size,
            threatsBlocked = blocked
        )
    }

    fun setProtectionActive(active: Boolean) {
        _isProtectionActive.value = active
        prefsManager.isProtectionActive = active
    }

    override fun onCleared() {
        super.onCleared()
        // Properly remove observers to prevent memory leaks
        callRepository.allSuspiciousCalls.removeObserver(suspiciousCallsObserver)
        callRepository.allTrustedNumbers.removeObserver(trustedNumbersObserver)
    }
}