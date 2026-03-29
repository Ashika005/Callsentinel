package com.example.callsentinel.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.callsentinel.R

class IncomingSafeCallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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

        setContentView(R.layout.activity_incoming_safe)

        val name = intent.getStringExtra(EXTRA_NAME) ?: "Unknown"
        val number = intent.getStringExtra(EXTRA_NUMBER) ?: "Unknown"

        findViewById<TextView>(R.id.tvSafeName).text = name
        findViewById<TextView>(R.id.tvSafeNumber).text = number

        findViewById<Button>(R.id.btnDecline).setOnClickListener {
            rejectCall()
            finish()
        }

        findViewById<Button>(R.id.btnAnswerSafe).setOnClickListener {
            finish()
        }
    }

    private fun rejectCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            try {
                telecomManager.endCall()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_NUMBER = "extra_number"

        fun start(context: Context, name: String, number: String) {
            val intent = Intent(context, IncomingSafeCallActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_NUMBER, number)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(intent)
        }
    }
}
