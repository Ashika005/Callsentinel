package com.example.callsentinel.data.repository

import androidx.lifecycle.LiveData
import com.example.callsentinel.data.db.CallSentinelDao
import com.example.callsentinel.data.model.AppNotification
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.model.TrustedNumber

class CallRepository(private val dao: CallSentinelDao) {

    // --- Suspicious Calls ---
    val allSuspiciousCalls: LiveData<List<SuspiciousCall>> = dao.getAllSuspiciousCalls()

    fun searchSuspiciousCalls(query: String): LiveData<List<SuspiciousCall>> {
        return dao.searchSuspiciousCalls(query)
    }

    suspend fun insertSuspiciousCall(call: SuspiciousCall) {
        dao.insertSuspiciousCall(call)
    }

    suspend fun deleteAllSuspiciousCalls() {
        dao.deleteAllSuspiciousCalls()
    }

    suspend fun getRecentCallCount(number: String, windowMinutes: Int): Int {
        val since = System.currentTimeMillis() - (windowMinutes * 60 * 1000)
        return dao.getRecentCallCount(number, since)
    }

    // --- Blocked Numbers ---
    val allBlockedNumbers: LiveData<List<BlockedNumber>> = dao.getAllBlockedNumbers()

    suspend fun insertBlockedNumber(number: BlockedNumber) {
        dao.insertBlockedNumber(number)
    }

    suspend fun deleteBlockedNumber(number: BlockedNumber) {
        dao.deleteBlockedNumber(number)
    }

    suspend fun isNumberBlocked(number: String): Boolean {
        return dao.isNumberBlocked(number)
    }

    // --- Trusted Numbers ---
    val allTrustedNumbers: LiveData<List<TrustedNumber>> = dao.getAllTrustedNumbers()

    suspend fun insertTrustedNumber(number: TrustedNumber) {
        dao.insertTrustedNumber(number)
    }

    suspend fun deleteTrustedNumber(number: TrustedNumber) {
        dao.deleteTrustedNumber(number)
    }

    suspend fun isNumberTrusted(number: String): Boolean {
        return dao.isNumberTrusted(number)
    }

    // --- Notifications ---
    val allNotifications: LiveData<List<AppNotification>> = dao.getAllNotifications()

    suspend fun insertNotification(notification: AppNotification) {
        dao.insertNotification(notification)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }
}
