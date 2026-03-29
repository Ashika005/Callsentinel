package com.example.callsentinel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suspicious_calls")
data class SuspiciousCall(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: String,
    val riskScore: Int,
    val reasons: String, // Stored as JSON array string
    val timestamp: Long,
    val wasBlocked: Boolean,
    val matchedContact: String?
)
