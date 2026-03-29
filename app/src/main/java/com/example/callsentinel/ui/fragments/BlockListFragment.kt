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
import com.example.callsentinel.databinding.FragmentBlockListBinding
import com.example.callsentinel.ui.adapters.BlockListAdapter
import com.example.callsentinel.ui.viewmodels.BlockListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockListFragment : Fragment() {

    private var _binding: FragmentBlockListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BlockListViewModel by viewModels()
    private lateinit var adapter: BlockListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        
        binding.fabAdd.setOnClickListener {
            showAddBlockNumberDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = BlockListAdapter { blockedNumber ->
            viewModel.removeBlockedNumber(blockedNumber)
        }
        binding.rvBlockList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blockedNumbers.collect { numbers ->
                    adapter.submitList(numbers)
                    binding.tvEmptyState.visibility = 
                        if (numbers.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showAddBlockNumberDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.example.callsentinel.R.layout.dialog_add_number, null)
        
        val etName = dialogView.findViewById<EditText>(com.example.callsentinel.R.id.etName)
        val etNumber = dialogView.findViewById<EditText>(com.example.callsentinel.R.id.etNumber)
        etName.hint = "Label (optional)"

        AlertDialog.Builder(requireContext())
            .setTitle("Block Number")
            .setView(dialogView)
            .setPositiveButton("Block") { _, _ ->
                val number = etNumber.text.toString()
                if (number.isNotBlank()) {
                    viewModel.addBlockedNumber(number)
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