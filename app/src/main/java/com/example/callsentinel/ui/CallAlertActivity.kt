package com.example.callsentinel.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.callsentinel.databinding.ActivityCallAlertBinding

class CallAlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra("EXTRA_PHONE_NUMBER") ?: "UNKNOWN ORIGIN"
        val riskScore = intent.getIntExtra("EXTRA_RISK_SCORE", 0)

        binding.tvIncomingNumber.text = phoneNumber
        
        binding.tvAlertDetails.text = buildString {
            append("> Analyzing call signature...\n")
            if (riskScore >= 70) {
                append("> CRTICAL: High risk signature. Likely spoofed routing or repeated scam origin.")
            } else if (riskScore >= 40) {
                append("> WARNING: Medium risk. Origin signature resembles secure contact but checksum fails.")
            } else {
                append("> NOTICE: Low risk. Origin not found in local registries.")
            }
        }

        binding.btnAccept.setOnClickListener {
            // For MVP: simply finish the activity. 
            // The default dialer will handle the actual call answering/ringing.
            finish()
        }

        binding.btnReject.setOnClickListener {
            // Programmatic rejection involves TelecomManager depending on OS version
            // For MVP: Finish this popup so user can handle it in the default dialer.
            finish()
        }
    }
}
