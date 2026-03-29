package com.example.callsentinel.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.callsentinel.R
import com.example.callsentinel.data.db.AppDatabase
import com.example.callsentinel.data.repository.CallRepository
import com.example.callsentinel.MainActivity
import com.example.callsentinel.utils.ContactsHelper
import com.example.callsentinel.detection.RiskEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallMonitorService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callRepository: CallRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "CallMonitorServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            // Using older PhoneStateListener for broader compatibility minSdk=26
            if (state == TelephonyManager.CALL_STATE_RINGING && !phoneNumber.isNullOrBlank()) {
                Log.d("CallMonitor", "Incoming call from: $phoneNumber")
                handleIncomingCall(phoneNumber)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        callRepository = CallRepository(AppDatabase.getDatabase(this).callSentinelDao())
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CallSentinel Active")
            .setContentText("Monitoring incoming calls for spoofing.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        // API 34+ requires foregroundServiceType to be specified both in the manifest
        // and when calling startForeground. For call monitoring, "phoneCall" is appropriate.
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
                } else {
                    0
                }
            )
        } catch (e: Exception) {
            Log.e("CallMonitor", "startForeground failed: ${e.message}", e)
            // If we can't go foreground, run as a short-lived service instead
            // However, starting with startForegroundService means we MUST call startForeground
            // or the app will crash. So we try again with 0 type if the specific one fails.
            try {
                ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, 0)
            } catch (inner: Exception) {
                Log.e("CallMonitor", "Final startForeground attempt failed.", inner)
            }
        }
        return START_STICKY
    }

    private fun handleIncomingCall(number: String) {
        serviceScope.launch {
            val contacts = ContactsHelper.getContacts(this@CallMonitorService)
            // Bug fix: LiveData.value is unsafe off the main thread and is often null before
            // the first emission. Use a direct suspend DAO query on Dispatchers.IO instead.
            val dao = AppDatabase.getDatabase(this@CallMonitorService).callSentinelDao()
            val trustedNumbers = withContext(Dispatchers.IO) {
                dao.getAllTrustedNumbersDirect()
            }
            val recentCount = callRepository.getRecentCallCount(number, 60)

            val result = RiskEngine.calculateScore(
                incomingNumber = number,
                contacts = contacts,
                trustedNumbers = trustedNumbers,
                recentCallsFromNumber = recentCount
            )

            if (result.isSuspicious) {
                // Show warning overlay
                com.example.callsentinel.ui.SpoofWarningActivity.start(
                    context = this@CallMonitorService,
                    number = number,
                    score = result.score,
                    matchedContact = result.matchedContact
                )
                Log.d("CallMonitor", "Suspicious Call Detected: Score ${result.score}")
            } else {
                if (result.matchedContact != null) {
                    com.example.callsentinel.ui.IncomingSafeCallActivity.start(
                        context = this@CallMonitorService,
                        name = result.matchedContact,
                        number = number
                    )
                }
                Log.d("CallMonitor", "Safe Call Detected")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Call Sentinel Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
