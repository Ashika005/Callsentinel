package com.example.callsentinel.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callsentinel.databinding.ItemContactBinding
import com.example.callsentinel.utils.ContactsHelper

class SystemContactsAdapter(private var contacts: List<ContactsHelper.Contact>) :
    RecyclerView.Adapter<SystemContactsAdapter.ContactViewHolder>() {

    fun submitList(newList: List<ContactsHelper.Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.binding.tvContactName.text = contact.name
        holder.binding.tvContactNumber.text = contact.number
    }

    override fun getItemCount(): Int = contacts.size

    class ContactViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)
}
