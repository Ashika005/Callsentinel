package com.example.callsentinel.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.databinding.ItemBlockedNumberBinding
import com.example.callsentinel.utils.PhoneNumberUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlockListAdapter(
    private val onRemoveClick: (BlockedNumber) -> Unit
) : ListAdapter<BlockedNumber, BlockListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlockedNumberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemBlockedNumberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(blockedNumber: BlockedNumber) {
            binding.apply {
                tvPhoneNumber.text = PhoneNumberUtils.formatNumber(blockedNumber.number)
                tvBlockedAt.text = "Blocked: ${formatTimestamp(blockedNumber.blockedAt)}"
                btnRemove.setOnClickListener { onRemoveClick(blockedNumber) }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BlockedNumber>() {
        override fun areItemsTheSame(oldItem: BlockedNumber, newItem: BlockedNumber): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: BlockedNumber, newItem: BlockedNumber): Boolean {
            return oldItem == newItem
        }
    }
}