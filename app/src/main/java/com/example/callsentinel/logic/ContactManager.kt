package com.example.callsentinel.logic

import android.content.Context
import android.provider.ContactsContract
import android.net.Uri

class ContactManager(private val context: Context) {

    fun isNumberInContacts(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // Similarity logic: get all distinct normalized phone numbers,
    // if a number matches area code + prefix but differs in last 2-4 digits.
    fun isSimilarToAnyContact(incomingNumber: String): Boolean {
        if (incomingNumber.length < 10) return false
        
        val normalizedIncoming = incomingNumber.replace(Regex("[^\\d]"), "")
        if (normalizedIncoming.length < 10) return false

        // First 6 digits (e.g., 123456xxxx)
        val incomingPrefix = normalizedIncoming.substring(0, 6)

        try {
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val rawNum = cursor.getString(numberIndex) ?: continue
                    val contactNum = rawNum.replace(Regex("[^\\d]"), "")
                    if (contactNum.length >= 10 && contactNum != normalizedIncoming) {
                        if (contactNum.startsWith(incomingPrefix)) {
                            // Spoofing risk: shares same area code and prefix, but different number
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
