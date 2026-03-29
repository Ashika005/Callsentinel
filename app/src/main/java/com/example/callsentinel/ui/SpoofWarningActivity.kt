package com.example.callsentinel.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.callsentinel.R

class SpoofWarningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // This makes the activity an overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setContentView(R.layout.activity_spoof_warning)

        val number = intent.getStringExtra(EXTRA_NUMBER) ?: "Unknown"
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val matchedContact = intent.getStringExtra(EXTRA_MATCHED_CONTACT)

        findViewById<TextView>(R.id.tvPhoneNumber).text = number
        
        val tvMatchedInfo = findViewById<TextView>(R.id.tvMatchedContactInfo)
        if (matchedContact.isNullOrBlank()) {
            tvMatchedInfo.visibility = View.GONE
        } else {
            tvMatchedInfo.text = "Similar to contact: $matchedContact"
            tvMatchedInfo.visibility = View.VISIBLE
        }

        findViewById<com.google.android.material.chip.Chip>(R.id.chipRiskScore).text = "Risk Score: $score"

        findViewById<Button>(R.id.btnReject).setOnClickListener {
            rejectCall()
            finish()
        }

        findViewById<Button>(R.id.btnAnswer).setOnClickListener {
            // Let the system handle answering or the user can answer via system UI
            finish()
        }

        findViewById<TextView>(R.id.tvViewDetails).setOnClickListener {
            // Open full app / details fragment
            val intent = Intent(this, com.example.callsentinel.MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun rejectCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            try {
                // Requires Manifest.permission.ANSWER_PHONE_CALLS
                telecomManager.endCall()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_MATCHED_CONTACT = "extra_matched_contact"

        fun start(context: Context, number: String, score: Int, matchedContact: String?) {
            val intent = Intent(context, SpoofWarningActivity::class.java).apply {
                putExtra(EXTRA_NUMBER, number)
                putExtra(EXTRA_SCORE, score)
                putExtra(EXTRA_MATCHED_CONTACT, matchedContact)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(intent)
        }
    }
}
