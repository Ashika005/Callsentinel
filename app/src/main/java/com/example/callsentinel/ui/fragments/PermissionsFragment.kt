package com.example.callsentinel.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.callsentinel.R
import com.example.callsentinel.databinding.FragmentPermissionsBinding
import com.example.callsentinel.utils.PrefsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionsFragment : Fragment() {

    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefsManager: PrefsManager

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.CALL_PHONE
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        updatePermissionStatus()
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            prefsManager.isFirstLaunch = false
            findNavController().navigate(R.id.action_permissions_to_home)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        updatePermissionStatus()
        
        binding.btnGrantPermissions.setOnClickListener {
            val permissionsToRequest = requiredPermissions.filter {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }
            
            if (permissionsToRequest.isNotEmpty()) {
                permissionLauncher.launch(permissionsToRequest.toTypedArray())
            } else {
                prefsManager.isFirstLaunch = false
                findNavController().navigate(R.id.action_permissions_to_home)
            }
        }
    }

    private fun updatePermissionStatus() {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        
        binding.tvPermissionStatus.text = if (allGranted) {
            "All permissions granted!"
        } else {
            "Some permissions are required for the app to function properly"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}