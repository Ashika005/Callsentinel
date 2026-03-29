package com.example.callsentinel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // e.g., SPOOF_BLOCKED, SUSPICIOUS_ACTIVITY, PROTECTION_ON, WEEKLY_REPORT
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val linkedNumber: String? = null
)
