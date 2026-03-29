package com.example.callsentinel.utils

object PhoneNumberUtils {
    
    fun normalizeNumber(number: String): String {
        return number.replace(Regex("[^\\d+]"), "")
    }

    fun formatNumber(number: String): String {
        // Basic US formatting for display, e.g. +12345678900 -> +1 (234) 567-8900
        val clean = normalizeNumber(number)
        if (clean.length == 10 && !clean.startsWith("+")) {
            return "(${clean.substring(0, 3)}) ${clean.substring(3, 6)}-${clean.substring(6)}"
        } else if (clean.length == 11 && clean.startsWith("1")) {
            return "+1 (${clean.substring(1, 4)}) ${clean.substring(4, 7)}-${clean.substring(7)}"
        } else if (clean.length == 12 && clean.startsWith("+1")) {
            return "+1 (${clean.substring(2, 5)}) ${clean.substring(5, 8)}-${clean.substring(8)}"
        }
        return number // Return original if not US format
    }
}
