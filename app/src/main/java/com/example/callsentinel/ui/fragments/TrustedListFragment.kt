package com.example.callsentinel.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.databinding.FragmentTrustedListBinding
import com.example.callsentinel.ui.adapters.TrustedListAdapter
import com.example.callsentinel.ui.viewmodels.TrustedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrustedListFragment : Fragment() {

    private var _binding: FragmentTrustedListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrustedListViewModel by viewModels()
    private lateinit var adapter: TrustedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrustedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        
        binding.fabAdd.setOnClickListener {
            showAddTrustedNumberDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = TrustedListAdapter { trustedNumber ->
            viewModel.removeTrustedNumber(trustedNumber)
        }
        binding.rvTrustedList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trustedNumbers.collect { numbers ->
                    adapter.submitList(numbers)
                    binding.tvEmptyState.visibility = 
                        if (numbers.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showAddTrustedNumberDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.example.callsentinel.R.layout.dialog_add_number, null)
        
        val etName = dialogView.findViewById<EditText>(com.example.callsentinel.R.id.etName)
        val etNumber = dialogView.findViewById<EditText>(com.example.callsentinel.R.id.etNumber)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Trusted Number")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val number = etNumber.text.toString()
                if (name.isNotBlank() && number.isNotBlank()) {
                    viewModel.addTrustedNumber(name, number)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}