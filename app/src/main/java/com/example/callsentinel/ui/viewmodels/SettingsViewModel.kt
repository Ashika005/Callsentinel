package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _isAutoBlockEnabled = MutableStateFlow(prefsManager.isAutoBlockActive)
    val isAutoBlockEnabled: StateFlow<Boolean> = _isAutoBlockEnabled

    private val _sensitivity = MutableStateFlow(prefsManager.sensitivityThreshold)
    val sensitivity: StateFlow<Int> = _sensitivity

    private val _areNotificationsEnabled = MutableStateFlow(true)
    val areNotificationsEnabled: StateFlow<Boolean> = _areNotificationsEnabled

    fun setAutoBlock(enabled: Boolean) {
        viewModelScope.launch {
            prefsManager.isAutoBlockActive = enabled
            _isAutoBlockEnabled.value = enabled
        }
    }

    fun setSensitivity(value: Int) {
        viewModelScope.launch {
            prefsManager.sensitivityThreshold = value
            _sensitivity.value = value
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _areNotificationsEnabled.value = enabled
        }
    }
}