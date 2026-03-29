package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    init {
        viewModelScope.launch {
            delay(2000) // Show splash for 2 seconds
            if (prefsManager.isFirstLaunch) {
                _navigationEvent.emit(NavigationEvent.NavigateToOnboarding)
            } else {
                _navigationEvent.emit(NavigationEvent.NavigateToHome)
            }
        }
    }

    sealed class NavigationEvent {
        object NavigateToOnboarding : NavigationEvent()
        object NavigateToHome : NavigationEvent()
    }
}