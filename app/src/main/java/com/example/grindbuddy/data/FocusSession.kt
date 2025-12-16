package com.example.grindbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_history")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val dateTimestamp: Long,
    val durationMinutes: Int,
    val xpEarned: Int
)