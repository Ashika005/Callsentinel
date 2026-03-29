package com.example.callsentinel.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callsentinel.databinding.FragmentSystemContactsBinding
import com.example.callsentinel.ui.adapters.SystemContactsAdapter
import com.example.callsentinel.utils.ContactsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SystemContactsFragment : Fragment() {

    private var _binding: FragmentSystemContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SystemContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSystemContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = SystemContactsAdapter(emptyList())
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val contacts = ContactsHelper.getContacts(requireContext())
            withContext(Dispatchers.Main) {
                adapter.submitList(contacts)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
