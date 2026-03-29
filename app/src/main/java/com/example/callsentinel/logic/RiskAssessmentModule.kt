package com.example.callsentinel.logic

import android.content.Context
import com.example.callsentinel.data.AppDatabase

class RiskAssessmentModule(private val context: Context) {

    private val contactManager = ContactManager(context)
    private val callLogDao = AppDatabase.getDatabase(context).callLogDao()

    suspend fun calculateRiskScore(phoneNumber: String): Int {
        var score = 0

        // Rule 1: Unknown number (not in contacts)
        if (!contactManager.isNumberInContacts(phoneNumber)) {
            score += 30
        } else {
            // If it's in contacts exactly, it's safe. Return 0.
            return 0
        }

        // Rule 2: Similar to contact but not exact (Spoof risk)
        if (contactManager.isSimilarToAnyContact(phoneNumber)) {
            score += 40
        }

        // Rule 3: Repeated calls in short time (e.g. within last 24 hours)
        val recentLogs = callLogDao.getLogsForNumber(phoneNumber)
        
        // Let's say repeated calls is > 2 calls in last 24 hours
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val currentTime = System.currentTimeMillis()
        val recentCount = recentLogs.count { it.timestamp > (currentTime - oneDayMillis) }
        
        if (recentCount >= 2) {
            score += 20
        }

        return score
    }
}
