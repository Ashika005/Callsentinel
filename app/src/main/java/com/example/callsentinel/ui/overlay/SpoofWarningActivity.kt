package com.example.callsentinel.ui.overlay

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.KeyguardManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.callsentinel.R
import com.example.callsentinel.data.db.AppDatabase
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.repository.CallRepository
import com.example.callsentinel.databinding.ActivitySpoofWarningBinding
import com.example.callsentinel.detection.RiskResult
import com.example.callsentinel.utils.PhoneNumberUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpoofWarningActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpoofWarningBinding
    private lateinit var callRepository: CallRepository
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val EXTRA_RISK_RESULT = "extra_risk_result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show above lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        // Overlay window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }

        binding = ActivitySpoofWarningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callRepository = CallRepository(AppDatabase.getDatabase(this).callSentinelDao())

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: "Unknown"
        val riskResult = intent.getParcelableExtra<RiskResult>(EXTRA_RISK_RESULT)

        setupUI(phoneNumber, riskResult)
        startWarningAnimation()
        playAlertSound()
        vibrate()

        // Auto-dismiss after 30 seconds
        binding.root.postDelayed({ finish() }, 30000)
    }

    private fun setupUI(phoneNumber: String, riskResult: RiskResult?) {
        binding.apply {
            tvPhoneNumber.text = PhoneNumberUtils.formatNumber(phoneNumber)
            chipRiskScore.text = "Risk Score: ${riskResult?.score ?: 0}"
            tvMatchedContactInfo.text = "Reasons: ${riskResult?.reasons?.joinToString() ?: "Unknown risk detected"}"
            
            // Set risk level color
            val riskColor = when (riskResult?.score ?: 0) {
                in 0..30 -> R.color.risk_low
                in 31..60 -> R.color.risk_medium
                in 61..80 -> R.color.risk_high
                else -> R.color.risk_critical
            }
            chipRiskScore.setChipBackgroundColorResource(riskColor)

            // Block / Reject button
            btnReject.setOnClickListener {
                blockNumber(phoneNumber, riskResult)
                finish()
            }

            // Ignore / Answer button
            btnAnswer.setOnClickListener {
                saveSuspiciousCall(phoneNumber, riskResult, false)
                finish()
            }
            
            tvViewDetails.setOnClickListener {
                saveSuspiciousCall(phoneNumber, riskResult, false)
                finish()
            }
        }
    }

    private fun startWarningAnimation() {
        // No-op for now as views were removed
    }

    private fun playAlertSound() {
        // Warning sound removed
    }

    private fun vibrate() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 500, 200, 500, 200, 500), 0)
        }
    }

    private fun blockNumber(phoneNumber: String, riskResult: RiskResult?) {
        lifecycleScope.launch {
            val blockedNumber = com.example.callsentinel.data.model.BlockedNumber(
                number = phoneNumber,
                blockedAt = System.currentTimeMillis()
            )
            callRepository.insertBlockedNumber(blockedNumber)
            saveSuspiciousCall(phoneNumber, riskResult, true)
            
            Toast.makeText(this@SpoofWarningActivity, "Number blocked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSuspiciousCall(phoneNumber: String, riskResult: RiskResult?, wasBlocked: Boolean) {
        lifecycleScope.launch {
            val suspiciousCall = SuspiciousCall(
                number = phoneNumber,
                riskScore = riskResult?.score ?: 50,
                reasons = riskResult?.reasons?.joinToString(",") ?: "Unknown",
                timestamp = System.currentTimeMillis(),
                wasBlocked = wasBlocked,
                matchedContact = riskResult?.matchedContact
            )
            callRepository.insertSuspiciousCall(suspiciousCall)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        
        vibrator?.cancel()
        vibrator = null
    }

    override fun onBackPressed() {
        // Prevent back button from closing
    }
}