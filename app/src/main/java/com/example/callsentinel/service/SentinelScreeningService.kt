package com.example.callsentinel.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.callsentinel.data.db.AppDatabase
import com.example.callsentinel.data.repository.CallRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class SentinelScreeningService : CallScreeningService() {

    private lateinit var callRepository: CallRepository
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        callRepository = CallRepository(AppDatabase.getDatabase(this).callSentinelDao())
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = getPhoneNumber(callDetails)
        
        if (phoneNumber == null) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
            Log.d("SentinelScreening", "Incoming call: $phoneNumber")

            scope.launch {
                val isBlocked = callRepository.isNumberBlocked(phoneNumber)
                if (isBlocked) {
                    Log.d("SentinelScreening", "Rejecting blocked number: $phoneNumber")
                    rejectCall(callDetails)
                } else {
                    // Let the CallMonitorService handle the risk scoring / overlay for now
                    allowCall(callDetails)
                }
            }
        } else {
            allowCall(callDetails)
        }
    }

    private fun rejectCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(true)
            .build()
        respondToCall(callDetails, response)
    }

    private fun allowCall(callDetails: Call.Details) {
        val response = CallResponse.Builder().build()
        respondToCall(callDetails, response)
    }

    private fun getPhoneNumber(callDetails: Call.Details): String? {
        return callDetails.handle?.schemeSpecificPart
    }
}
