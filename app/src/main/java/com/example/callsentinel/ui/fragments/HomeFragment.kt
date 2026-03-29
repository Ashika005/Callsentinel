package com.example.callsentinel.ui.fragments

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.R
import com.example.callsentinel.databinding.FragmentHomeBinding
import com.example.callsentinel.service.CallMonitorService
import com.example.callsentinel.ui.adapters.RecentAlertsAdapter
import com.example.callsentinel.ui.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recentAlertsAdapter: RecentAlertsAdapter

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.CALL_PHONE
    )

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val context = context ?: return@registerForActivityResult
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            logTerminal("SUCCESS: Role obtained.")
            Toast.makeText(context, "Shield Authorized", Toast.LENGTH_SHORT).show()
            checkAndRequestPermissions()
        } else {
            logTerminal("WARNING: Role request denied")
            updateSystemStatus()
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            logTerminal("SUCCESS: All permissions granted.")
            viewModel.setProtectionActive(true)
        } else {
            logTerminal("WARNING: Some permissions denied.")
        }
        updateSystemStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        observeViewModel()
        updateSystemStatus()
    }

    private fun setupUI() {
        binding.btnEnableProtection.setOnClickListener {
            initializeShield()
        }

        binding.btnViewLogs.setOnClickListener {
            findNavController().navigate(R.id.callLogFragment)
        }

        binding.btnViewAnalytics.setOnClickListener {
            findNavController().navigate(R.id.analyticsFragment)
        }

        binding.btnQuickSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        binding.btnSystemCallLogs.setOnClickListener {
            findNavController().navigate(R.id.systemCallLogFragment)
        }

        binding.btnSystemContacts.setOnClickListener {
            findNavController().navigate(R.id.systemContactsFragment)
        }
    }

    private fun setupRecyclerView() {
        val context = context ?: return
        recentAlertsAdapter = RecentAlertsAdapter { call ->
            val action = HomeFragmentDirections.actionHomeToCallDetails(
                callId = call.id,
                phoneNumber = call.number
            )
            findNavController().navigate(action)
        }
        binding.rvRecentAlerts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentAlertsAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recentSuspiciousCalls.collect { calls ->
                        recentAlertsAdapter.submitList(calls.take(5))
                        binding.tvRecentAlertsCount.text = "${calls.size} alerts today"
                    }
                }

                launch {
                    viewModel.stats.collect { stats ->
                        binding.tvCallsScanned.text = stats.callsScanned.toString()
                        binding.tvThreatsBlocked.text = stats.threatsBlocked.toString()
                        binding.tvTrustedNumbers.text = stats.trustedCount.toString()
                    }
                }

                launch {
                    viewModel.isProtectionActive.collect { isActive ->
                        updateProtectionUI(isActive)
                    }
                }
            }
        }
    }

    private fun initializeShield() {
        val context = context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            logTerminal("Detecting system framework: API >= 29")

            if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                try {
                    logTerminal("Executing Role Request Override...")
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    roleRequestLauncher.launch(intent)
                } catch (e: Exception) {
                    logTerminal("ERROR: Protocol failure - ${e.message}")
                    Toast.makeText(context, "System fault on Role request.", Toast.LENGTH_LONG).show()
                }
            } else {
                logTerminal("Role already secure.")
                checkAndRequestPermissions()
            }
        } else {
            logTerminal("API < 29 detected. Role override not required.")
            checkAndRequestPermissions()
        }
    }

    private fun checkAndRequestPermissions() {
        val context = context ?: return
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            logTerminal("All accesses granted. System armed.")
            viewModel.setProtectionActive(true)
            updateSystemStatus()
        }
    }

    private fun updateSystemStatus() {
        val context = context ?: return
        val roleGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        } else {
            false
        }

        var permissionsGranted = true
        var missingPermsCount = 0
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false
                missingPermsCount++
            }
        }

        val isFullyActive = roleGranted && permissionsGranted
        viewModel.setProtectionActive(isFullyActive)

        if (isFullyActive) {
            startCallMonitoringService()
        }
    }

    private fun startCallMonitoringService() {
        val context = context ?: return
        val serviceIntent = Intent(context, CallMonitorService::class.java)
        context.startForegroundService(serviceIntent)
        logTerminal("Call monitor service started.")
    }

    private fun updateProtectionUI(isActive: Boolean) {
        val binding = _binding ?: return
        val context = context ?: return
        
        if (isActive) {
            binding.tvStatus.text = "ONLINE & SECURE"
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.accent_green))
            binding.btnEnableProtection.isEnabled = false
            binding.btnEnableProtection.text = "SHIELD ACTIVE"
            binding.lottieRadar.playAnimation()
        } else {
            binding.tvStatus.text = "OFFLINE"
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.accent_red))
            binding.btnEnableProtection.isEnabled = true
            binding.btnEnableProtection.text = "INITIALIZE SHIELD"
            binding.lottieRadar.pauseAnimation()
        }
    }

    private fun logTerminal(message: String) {
        val binding = _binding ?: return
        val currentLogs = binding.tvLogsOutput.text.toString()
        val logLines = currentLogs.split("\n")
        val combined = if (logLines.size > 4) {
            logLines.takeLast(4).joinToString("\n") + "\n> $message"
        } else {
            "$currentLogs\n> $message"
        }
        binding.tvLogsOutput.text = combined
    }

    override fun onResume() {
        super.onResume()
        updateSystemStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}