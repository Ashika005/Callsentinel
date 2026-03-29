package com.example.callsentinel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_numbers")
data class BlockedNumber(
    @PrimaryKey val number: String,
    val blockedAt: Long,
    val attemptCount: Int = 0
)
