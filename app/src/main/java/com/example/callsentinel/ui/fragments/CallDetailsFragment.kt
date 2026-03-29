package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.callsentinel.R
import com.example.callsentinel.databinding.FragmentCallDetailsBinding
import com.example.callsentinel.ui.viewmodels.CallDetailsViewModel
import com.example.callsentinel.utils.PhoneNumberUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CallDetailsFragment : Fragment() {

    private var _binding: FragmentCallDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CallDetailsViewModel by viewModels()
    private val args: CallDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        args.phoneNumber?.let { phoneNumber ->
            viewModel.loadCallDetails(args.callId, phoneNumber)
        }
        
        observeViewModel()
        setupActions()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.callDetails.collect { call ->
                    call?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(call: com.example.callsentinel.data.model.SuspiciousCall) {
        binding.apply {
            tvPhoneNumber.text = PhoneNumberUtils.formatNumber(call.number)
            tvRiskScore.text = call.riskScore.toString()
            tvTimestamp.text = formatTimestamp(call.timestamp)
            
            // Set risk color and indicator
            val (riskColor, riskText) = when (call.riskScore) {
                in 0..30 -> Pair(R.color.risk_low, "Low Risk")
                in 31..60 -> Pair(R.color.risk_medium, "Medium Risk")
                in 61..80 -> Pair(R.color.risk_high, "High Risk")
                else -> Pair(R.color.risk_critical, "Critical Risk")
            }
            
            tvRiskLevel.text = riskText
            tvRiskScore.setTextColor(ContextCompat.getColor(requireContext(), riskColor))
            tvRiskLevel.setTextColor(ContextCompat.getColor(requireContext(), riskColor))
            progressRisk.setIndicatorColor(ContextCompat.getColor(requireContext(), riskColor))
            progressRisk.progress = call.riskScore
            
            // Reasons
            tvReasons.text = call.reasons.split(",").joinToString("\n• ", "• ")
            
            // Status
            if (call.wasBlocked) {
                tvStatus.text = "BLOCKED"
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_green))
            } else {
                tvStatus.text = "WARNING SHOWN"
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.risk_medium))
            }
            
            call.matchedContact?.let {
                tvSimilarTo.text = "Similar to: $it"
                tvSimilarTo.visibility = View.VISIBLE
            } ?: run {
                tvSimilarTo.visibility = View.GONE
            }
        }
    }

    private fun setupActions() {
        binding.apply {
            btnBlock.setOnClickListener {
                viewModel.blockNumber()
            }
            
            btnTrust.setOnClickListener {
                viewModel.trustNumber()
            }
            
            btnDelete.setOnClickListener {
                viewModel.deleteCall()
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}