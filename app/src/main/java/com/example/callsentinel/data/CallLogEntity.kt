package com.example.callsentinel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val riskScore: Int,
    val timestamp: Long,
    val status: String // E.g., "Suspicious", "Safe"
)
