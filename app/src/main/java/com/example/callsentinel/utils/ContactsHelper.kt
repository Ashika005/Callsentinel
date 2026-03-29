package com.example.callsentinel.utils

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

object ContactsHelper {

    data class Contact(val name: String, val number: String)

    fun getContacts(context: Context): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                null
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex) ?: ""
                    var number = it.getString(numberIndex) ?: ""
                    // Normalize number (remove spaces, dashes)
                    number = PhoneNumberUtils.normalizeNumber(number)
                    if (number.isNotEmpty()) {
                        contactsList.add(Contact(name, number))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ContactsHelper", "Error reading contacts: ${e.message}")
        }
        return contactsList
    }
}
