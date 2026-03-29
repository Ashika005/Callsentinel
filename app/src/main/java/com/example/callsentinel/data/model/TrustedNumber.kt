package com.example.callsentinel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_numbers")
data class TrustedNumber(
    @PrimaryKey val number: String,
    val name: String,
    val addedAt: Long
)
