package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.databinding.FragmentSystemCallLogBinding
import com.example.callsentinel.ui.adapters.SystemCallLogAdapter
import com.example.callsentinel.utils.CallLogHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SystemCallLogFragment : Fragment() {

    private var _binding: FragmentSystemCallLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SystemCallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemCallLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = SystemCallLogAdapter(emptyList())
        binding.rvCallLog.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCallLog.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val calls = CallLogHelper.getSystemCalls(requireContext())
            withContext(Dispatchers.Main) {
                adapter.submitList(calls)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
