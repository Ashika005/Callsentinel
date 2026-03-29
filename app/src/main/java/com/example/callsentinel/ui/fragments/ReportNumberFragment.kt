package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.callsentinel.databinding.FragmentReportNumberBinding
import com.example.callsentinel.ui.viewmodels.BlockListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportNumberFragment : Fragment() {

    private var _binding: FragmentReportNumberBinding? = null
    private val binding get() = _binding!!
    private val args: ReportNumberFragmentArgs by navArgs()
    private val viewModel: BlockListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Pre-fill the phone number if passed via navigation args
        args.phoneNumber?.let { number ->
            binding.tvPhoneNumber.text = number
        }

        binding.btnSubmitReport.setOnClickListener {
            val number = binding.tvPhoneNumber.text.toString()
            if (number.isBlank()) {
                Toast.makeText(requireContext(), "No number to report", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reasonId = binding.rgReason.checkedRadioButtonId
            if (reasonId == -1) {
                Toast.makeText(requireContext(), "Please select a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Since it's a simulated crowdsourcing app for now, we just add it to the local blocklist.
            viewModel.addBlockedNumber(number)
            Toast.makeText(requireContext(), "Report submitted & Number Blocked", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
