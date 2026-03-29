package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.callsentinel.databinding.FragmentSettingsBinding
import com.example.callsentinel.ui.IncomingSafeCallActivity
import com.example.callsentinel.ui.SpoofWarningActivity
import com.example.callsentinel.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwitches()
        observeViewModel()
        setupNavigation()
    }

    private fun setupSwitches() {
        binding.switchAutoBlock.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoBlock(isChecked)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationsEnabled(isChecked)
        }

        binding.sliderSensitivity.addOnChangeListener { _, value, _ ->
            viewModel.setSensitivity(value.toInt())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isAutoBlockEnabled.collect { enabled ->
                        binding.switchAutoBlock.isChecked = enabled
                    }
                }

                launch {
                    viewModel.sensitivity.collect { value ->
                        binding.sliderSensitivity.value = value.toFloat()
                        binding.tvSensitivityValue.text = "$value%"
                    }
                }

                launch {
                    viewModel.areNotificationsEnabled.collect { enabled ->
                        binding.switchNotifications.isChecked = enabled
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        binding.cardTrustedList.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToTrustedList())
        }

        binding.cardBlockList.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToBlockList())
        }

        binding.cardRiskGuide.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToRiskScoreGuide())
        }

        binding.cardHowItWorks.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsToHowItWorks())
        }

        binding.cardTestSimulation.setOnClickListener {
            val options = arrayOf("Simulate Safe Call", "Simulate Spoofed Call")
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Simulation Engine")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            IncomingSafeCallActivity.start(
                                context = requireContext(),
                                name = "Mom (Simulated)",
                                number = "(555) 123-4567"
                            )
                        }
                        1 -> {
                            SpoofWarningActivity.start(
                                context = requireContext(),
                                number = "+1 (555) 123-4599",
                                score = 90,
                                matchedContact = null
                            )
                        }
                    }
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}