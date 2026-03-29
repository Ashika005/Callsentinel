package com.example.callsentinel.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.callsentinel.data.model.BlockedNumber
import com.example.callsentinel.data.model.SuspiciousCall
import com.example.callsentinel.data.model.TrustedNumber
import com.example.callsentinel.data.model.AppNotification

@Dao
interface CallSentinelDao {

    // --- Suspicious Calls ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuspiciousCall(call: SuspiciousCall)

    @Query("SELECT * FROM suspicious_calls ORDER BY timestamp DESC")
    fun getAllSuspiciousCalls(): LiveData<List<SuspiciousCall>>

    @Query("SELECT * FROM suspicious_calls WHERE number LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchSuspiciousCalls(query: String): LiveData<List<SuspiciousCall>>

    @Query("DELETE FROM suspicious_calls")
    suspend fun deleteAllSuspiciousCalls()
    
    @Query("SELECT COUNT(*) FROM suspicious_calls WHERE number = :number AND timestamp >= :since")
    suspend fun getRecentCallCount(number: String, since: Long): Int

    // --- Blocked Numbers ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedNumber(blockedNumber: BlockedNumber)

    @Delete
    suspend fun deleteBlockedNumber(blockedNumber: BlockedNumber)

    @Query("SELECT * FROM blocked_numbers ORDER BY blockedAt DESC")
    fun getAllBlockedNumbers(): LiveData<List<BlockedNumber>>

    @Query("SELECT COUNT(*) > 0 FROM blocked_numbers WHERE number = :number")
    suspend fun isNumberBlocked(number: String): Boolean

    // --- Trusted Numbers ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrustedNumber(trustedNumber: TrustedNumber)

    @Delete
    suspend fun deleteTrustedNumber(trustedNumber: TrustedNumber)

    @Query("SELECT * FROM trusted_numbers ORDER BY name ASC")
    fun getAllTrustedNumbers(): LiveData<List<TrustedNumber>>

    /** Direct suspend version — safe to call from background threads (e.g., in a Service). */
    @Query("SELECT * FROM trusted_numbers ORDER BY name ASC")
    suspend fun getAllTrustedNumbersDirect(): List<TrustedNumber>

    @Query("SELECT COUNT(*) > 0 FROM trusted_numbers WHERE number = :number")
    suspend fun isNumberTrusted(number: String): Boolean

    // --- Notifications ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): LiveData<List<AppNotification>>

    @Query("UPDATE app_notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsAsRead()
}
