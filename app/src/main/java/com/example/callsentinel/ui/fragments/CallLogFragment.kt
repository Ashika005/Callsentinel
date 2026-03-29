package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.databinding.FragmentCallLogBinding
import com.example.callsentinel.ui.adapters.CallLogAdapter
import com.example.callsentinel.ui.viewmodels.CallLogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CallLogFragment : Fragment() {

    private var _binding: FragmentCallLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CallLogViewModel by viewModels()
    private lateinit var callLogAdapter: CallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        callLogAdapter = CallLogAdapter { call ->
            val action = CallLogFragmentDirections.actionCallLogToCallDetails(
                callId = call.id,
                phoneNumber = call.number
            )
            findNavController().navigate(action)
        }
        binding.rvCallLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = callLogAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchCalls(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchCalls(it) }
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calls.collect { calls ->
                    callLogAdapter.submitList(calls)
                    binding.tvEmptyState.visibility = 
                        if (calls.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}