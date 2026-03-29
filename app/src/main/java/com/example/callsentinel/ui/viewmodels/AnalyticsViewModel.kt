package com.example.callsentinel.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callsentinel.data.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    data class Stats(
        val totalScanned: Int = 0,
        val totalBlocked: Int = 0,
        val highRiskCount: Int = 0,
        val spoofAttempts: Int = 0
    )

    data class RiskBreakdown(
        val low: Int = 0,
        val medium: Int = 0,
        val high: Int = 0,
        val critical: Int = 0
    )

    private val _weeklyData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val weeklyData: StateFlow<Map<String, Int>> = _weeklyData

    private val _stats = MutableStateFlow(Stats())
    val stats: StateFlow<Stats> = _stats

    private val _riskBreakdown = MutableStateFlow(RiskBreakdown())
    val riskBreakdown: StateFlow<RiskBreakdown> = _riskBreakdown

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            callRepository.allSuspiciousCalls.observeForever { calls ->
                // Calculate stats
                val totalScanned = calls.size
                val totalBlocked = calls.count { it.wasBlocked }
                val highRiskCount = calls.count { it.riskScore >= 60 }
                val spoofAttempts = calls.count { it.matchedContact != null }

                _stats.value = Stats(
                    totalScanned = totalScanned,
                    totalBlocked = totalBlocked,
                    highRiskCount = highRiskCount,
                    spoofAttempts = spoofAttempts
                )

                // Calculate risk breakdown
                val low = calls.count { it.riskScore in 0..30 }
                val medium = calls.count { it.riskScore in 31..60 }
                val high = calls.count { it.riskScore in 61..80 }
                val critical = calls.count { it.riskScore > 80 }

                _riskBreakdown.value = RiskBreakdown(low, medium, high, critical)

                // Calculate weekly data
                _weeklyData.value = calculateWeeklyData(calls)
            }
        }
    }

    private fun calculateWeeklyData(calls: List<com.example.callsentinel.data.model.SuspiciousCall>): Map<String, Int> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())

        val last7Days = (0..6).map { daysAgo ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            sdf.format(calendar.time)
        }.reversed()

        return last7Days.associateWith { day ->
            calls.count { call ->
                sdf.format(java.util.Date(call.timestamp)) == day
            }
        }
    }
}