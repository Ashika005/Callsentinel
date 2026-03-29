package com.example.callsentinel.ui

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.callsentinel.data.CallLogEntity
import com.example.callsentinel.databinding.ItemCallLogBinding
import java.util.Date

class LogsAdapter : ListAdapter<CallLogEntity, LogsAdapter.LogViewHolder>(LogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LogViewHolder(private val binding: ItemCallLogBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(log: CallLogEntity) {
            binding.tvPhoneNumber.text = log.phoneNumber
            binding.tvRiskScore.text = log.riskScore.toString()
            binding.tvStatus.text = log.status.uppercase()
            
            val dateStr = DateFormat.format("MMM dd, HH:mm", Date(log.timestamp)).toString()
            binding.tvTimestamp.text = dateStr
        }
    }

    class LogDiffCallback : DiffUtil.ItemCallback<CallLogEntity>() {
        override fun areItemsTheSame(oldItem: CallLogEntity, newItem: CallLogEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CallLogEntity, newItem: CallLogEntity) = oldItem == newItem
    }
}
