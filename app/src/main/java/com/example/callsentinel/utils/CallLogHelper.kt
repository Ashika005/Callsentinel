package com.example.callsentinel.utils

import android.content.Context
import android.provider.CallLog
import android.util.Log

object CallLogHelper {

    data class SystemCall(val name: String, val number: String, val type: Int, val date: Long, val duration: Long)

    fun getSystemCalls(context: Context): List<SystemCall> {
        val callsList = mutableListOf<SystemCall>()
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC LIMIT 100" // Get last 100 calls
            )

            cursor?.use {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

                while (it.moveToNext()) {
                    val number = it.getString(numberIndex) ?: "Unknown"
                    val type = it.getInt(typeIndex)
                    val date = it.getLong(dateIndex)
                    val duration = it.getLong(durationIndex)
                    val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                    val finalName = if (name.isNullOrEmpty()) "Unknown" else name

                    callsList.add(SystemCall(finalName, number, type, date, duration))
                }
            }
        } catch (e: Exception) {
            Log.e("CallLogHelper", "Error reading call log: ${e.message}")
        }
        return callsList
    }
}
