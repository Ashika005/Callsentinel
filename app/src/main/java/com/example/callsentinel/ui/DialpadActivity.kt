package com.example.callsentinel.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callsentinel.databinding.ActivityDialpadBinding
import com.example.callsentinel.logic.RiskAssessmentModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialpadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialpadBinding
    private lateinit var riskModule: RiskAssessmentModule
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val CALL_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialpadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        riskModule = RiskAssessmentModule(this)

        binding.btnScan.setOnClickListener {
            val number = binding.etPhoneNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                scanNumber(number)
            } else {
                Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCall.setOnClickListener {
            val number = binding.etPhoneNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                initiateCall(number)
            } else {
                Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scanNumber(phoneNumber: String) {
        binding.btnScan.isEnabled = false
        binding.btnScan.text = "ANALYZING..."
        
        // Use Coroutines just like the CallSentinelService
        scope.launch {
            val riskScore = withContext(Dispatchers.IO) {
                riskModule.calculateRiskScore(phoneNumber)
            }
            
            showScanResult(riskScore)
            
            binding.btnScan.isEnabled = true
            binding.btnScan.text = "INITIATE SCAN"
        }
    }

    private fun showScanResult(score: Int) {
        binding.llScanResults.visibility = View.VISIBLE
        
        if (score >= 70) {
            binding.tvScanResultTitle.text = "[!] CRITICAL THREAT"
            binding.tvScanResultTitle.setTextColor(Color.parseColor("#FF1744"))
            binding.tvScanDetails.text = "> Risk Score: $score\n> Likely spoofed or repeated scam origin."
            binding.tvScanDetails.setTextColor(Color.parseColor("#FF1744"))
        } else if (score >= 40) {
            binding.tvScanResultTitle.text = "[!] WARNING: ANOMALY DETECTED"
            binding.tvScanResultTitle.setTextColor(Color.parseColor("#FFC107"))
            binding.tvScanDetails.text = "> Risk Score: $score\n> Origin signature resembles secure contact but checksum fails."
            binding.tvScanDetails.setTextColor(Color.parseColor("#FFC107"))
        } else {
            binding.tvScanResultTitle.text = "STATUS: CLEAR"
            binding.tvScanResultTitle.setTextColor(Color.parseColor("#00E676"))
            binding.tvScanDetails.text = "> Risk Score: $score\n> No immediate threats detected in routing prefix."
            binding.tvScanDetails.setTextColor(Color.parseColor("#00E676"))
        }
    }

    private fun initiateCall(phoneNumber: String) {
        // Need to check for CALL_PHONE permission right before making the call
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_REQUEST_CODE)
            return
        }

        try {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to initiate call: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val number = binding.etPhoneNumber.text.toString().trim()
                if (number.isNotEmpty()) {
                    initiateCall(number)
                }
            } else {
                Toast.makeText(this, "Call permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
