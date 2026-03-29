package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallLogViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    val calls: StateFlow<List<SuspiciousCall>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            callRepository.allSuspiciousCalls.observeForever { callsList ->
                (calls as MutableStateFlow).value = callsList
            }
        }
    }

    fun searchCalls(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                callRepository.allSuspiciousCalls.observeForever { callsList ->
                    (calls as MutableStateFlow).value = callsList
                }
            } else {
                callRepository.searchSuspiciousCalls(query).observeForever { callsList ->
                    (calls as MutableStateFlow).value = callsList
                }
            }
        }
    }
}