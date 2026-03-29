package com.example.callsentinel.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.databinding.ItemCallLogBinding
import com.example.callsentinel.utils.PhoneNumberUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogAdapter(
    private val onItemClick: (SuspiciousCall) -> Unit
) : ListAdapter<SuspiciousCall, CallLogAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCallLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCallLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(call: SuspiciousCall) {
            binding.apply {
                tvPhoneNumber.text = PhoneNumberUtils.formatNumber(call.number)
                tvRiskScore.text = "${call.riskScore}"
                tvTimestamp.text = formatTimestamp(call.timestamp)
                
                // Risk color
                val riskColor = when (call.riskScore) {
                    in 0..30 -> com.example.callsentinel.R.color.risk_low
                    in 31..60 -> com.example.callsentinel.R.color.risk_medium
                    in 61..80 -> com.example.callsentinel.R.color.risk_high
                    else -> com.example.callsentinel.R.color.risk_critical
                }
                tvRiskScore.setTextColor(root.context.getColor(riskColor))
                
                // Status
                if (call.wasBlocked) {
                    tvStatus.text = "BLOCKED"
                    tvStatus.setTextColor(root.context.getColor(com.example.callsentinel.R.color.accent_green))
                } else {
                    tvStatus.text = "WARNING"
                    tvStatus.setTextColor(root.context.getColor(com.example.callsentinel.R.color.risk_medium))
                }

                root.setOnClickListener { onItemClick(call) }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SuspiciousCall>() {
        override fun areItemsTheSame(oldItem: SuspiciousCall, newItem: SuspiciousCall): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SuspiciousCall, newItem: SuspiciousCall): Boolean {
            return oldItem == newItem
        }
    }
}