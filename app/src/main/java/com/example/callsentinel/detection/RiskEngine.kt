package com.example.callsentinel.detection

import com.example.callsentinel.data.model.TrustedNumber
import com.example.callsentinel.utils.ContactsHelper.Contact

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RiskResult(
    val score: Int,
    val reasons: List<String>,
    val isSuspicious: Boolean,
    val matchedContact: String? = null
) : Parcelable

object RiskEngine {
    fun calculateScore(
        incomingNumber: String,
        contacts: List<Contact>,
        trustedNumbers: List<TrustedNumber>,
        recentCallsFromNumber: Int,
        threshold: Int = 50
    ): RiskResult {
        var score = 0
        val reasons = mutableListOf<String>()

        // 1. Check if Trusted Number
        val isTrusted = trustedNumbers.any { it.number == incomingNumber }
        if (isTrusted) return RiskResult(0, emptyList(), false)

        // 2. Check if Exact Contact
        val exactMatch = contacts.find { it.number == incomingNumber }
        if (exactMatch != null) return RiskResult(0, emptyList(), false, exactMatch.number)

        // 3. Check for visually similar number (neighbor spoofing)
        val similar = contacts.find { isSimilar(it.number, incomingNumber) }
        if (similar != null) {
            score += 40
            reasons.add("Similar to ${similar.name} but not exact")
        } else {
            score += 30
            reasons.add("Unknown number not in contacts")
        }

        // 4. Check for Foreign Country Code
        if (isDifferentCountryCode(incomingNumber)) {
            score += 25
            reasons.add("Different country code")
        }

        // 5. Check for Repeated Calls
        if (recentCallsFromNumber >= 3) {
            score += 20
            reasons.add("Repeated calls in short time")
        }

        return RiskResult(score, reasons, score >= threshold, similar?.number)
    }

    private fun isSimilar(known: String, incoming: String): Boolean {
        val a = known.filter { it.isDigit() }
        val b = incoming.filter { it.isDigit() }
        if (a.length != b.length) return false
        val diffCount = a.zip(b).count { it.first != it.second }
        // If it's identical, it would be caught by exactMatch. We're looking for slight deviations
        return diffCount in 1..2
    }

    private fun isDifferentCountryCode(number: String): Boolean {
        // Simple heuristic: assuming US (+1) as home country. In a real app, this would be dynamic block
        return number.startsWith("+") && !number.startsWith("+1")
    }
}
