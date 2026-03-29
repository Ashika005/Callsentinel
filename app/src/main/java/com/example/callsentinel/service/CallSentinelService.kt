package com.example.callsentinel.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.callsentinel.data.AppDatabase
import com.example.callsentinel.data.CallLogEntity
import com.example.callsentinel.logic.RiskAssessmentModule
import com.example.callsentinel.ui.CallAlertActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallSentinelService : CallScreeningService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var riskModule: RiskAssessmentModule
    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        riskModule = RiskAssessmentModule(this)
        db = AppDatabase.getDatabase(this)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        val callDirection = callDetails.callDirection
        
        if (callDirection != Call.Details.DIRECTION_INCOMING) {
            return
        }

        scope.launch {
            val riskScore = riskModule.calculateRiskScore(phoneNumber)
            val isSuspicious = riskScore > 50

            // Save to DB
            val logEntry = CallLogEntity(
                phoneNumber = phoneNumber,
                riskScore = riskScore,
                timestamp = System.currentTimeMillis(),
                status = if (isSuspicious) "Suspicious" else "Safe"
            )
            db.callLogDao().insertCallLog(logEntry)

            val responseBuilder = CallResponse.Builder()

            if (isSuspicious) {
                // Show warning popup
                showWarningPopup(phoneNumber, riskScore)
                
                // Allow call to ring so user can make a choice from our popup or default dialer
                responseBuilder.setDisallowCall(false)
                responseBuilder.setRejectCall(false)
                responseBuilder.setSkipCallLog(false)
                responseBuilder.setSkipNotification(false)
            } else {
                responseBuilder.setDisallowCall(false)
                responseBuilder.setRejectCall(false)
                responseBuilder.setSkipCallLog(false)
                responseBuilder.setSkipNotification(false)
            }

            respondToCall(callDetails, responseBuilder.build())
        }
    }

    private fun showWarningPopup(phoneNumber: String, score: Int) {
        val intent = Intent(this, CallAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_PHONE_NUMBER", phoneNumber)
            putExtra("EXTRA_RISK_SCORE", score)
        }
        startActivity(intent)
    }
}
