package com.example.callsentinel.ui.overlay

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.callsentinel.databinding.ActivityIncomingSafeCallBinding
import com.example.callsentinel.utils.PhoneNumberUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingSafeCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncomingSafeCallBinding

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        const val EXTRA_CONTACT_NAME = "extra_contact_name"
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

        binding = ActivityIncomingSafeCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: "Unknown"
        val contactName = intent.getStringExtra(EXTRA_CONTACT_NAME)

        setupUI(phoneNumber, contactName)

        // Auto-dismiss after 5 seconds
        binding.root.postDelayed({ finish() }, 5000)
    }

    private fun setupUI(phoneNumber: String, contactName: String?) {
        binding.apply {
            if (contactName != null) {
                tvCallerName.text = contactName
                tvPhoneNumber.text = PhoneNumberUtils.formatNumber(phoneNumber)
            } else {
                tvCallerName.text = PhoneNumberUtils.formatNumber(phoneNumber)
                tvPhoneNumber.text = "Unknown Contact"
            }

            tvStatus.text = "SAFE CALL"
            tvStatus.setTextColor(getColor(com.example.callsentinel.R.color.accent_green))

            btnDismiss.setOnClickListener {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}