package com.example.callsentinel.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Insert
    suspend fun insertCallLog(callLog: CallLogEntity)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntity>>
    
    @Query("SELECT * FROM call_logs WHERE phoneNumber = :number ORDER BY timestamp DESC")
    suspend fun getLogsForNumber(number: String): List<CallLogEntity>
}
