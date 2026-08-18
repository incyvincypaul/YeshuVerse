package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 0,
    val lastStepIndex: Int = 0,
    val mysteryType: String = "JOYFUL",
    val timestamp: Long = System.currentTimeMillis()
)
