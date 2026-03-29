package com.example.callsentinel.ui.adapters

import android.provider.CallLog
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callsentinel.databinding.ItemSystemCallLogBinding
import com.example.callsentinel.utils.CallLogHelper
import java.util.Date

class SystemCallLogAdapter(private var calls: List<CallLogHelper.SystemCall>) :
    RecyclerView.Adapter<SystemCallLogAdapter.CallLogViewHolder>() {

    fun submitList(newList: List<CallLogHelper.SystemCall>) {
        calls = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val binding = ItemSystemCallLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CallLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val call = calls[position]
        holder.binding.tvCallerName.text = call.name
        holder.binding.tvCallerNumber.text = call.number
        
        val dateString = DateFormat.format("MMM dd, hh:mm a", Date(call.date)).toString()
        holder.binding.tvCallTime.text = dateString
        
        // Basic type labeling
        val typeStr = when (call.type) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            CallLog.Calls.REJECTED_TYPE -> "Rejected"
            else -> "Call"
        }
        
        holder.binding.tvCallerNumber.text = "${call.number} • $typeStr"
    }

    override fun getItemCount(): Int = calls.size

    class CallLogViewHolder(val binding: ItemSystemCallLogBinding) : RecyclerView.ViewHolder(binding.root)
}
