package com.example.grindbuddy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    // 1. SAVE: Write a new row
    // 'suspend' means "do this in background" so the app doesn't freeze
    @Insert
    suspend fun insertSession(session: FocusSession)

    // 2. READ ALL: Get the whole history
    // return a 'Flow' (Live Stream). If  add a row, this updates automatically!
    @Query("SELECT * FROM session_history ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    // 3. READ TODAY: Get total minutes for today
    // This is SQL magic. We ask for the sum of duration where the date is bigger than today's start.
    @Query("SELECT SUM(durationMinutes) FROM session_history WHERE dateTimestamp >= :startTime")
    fun getMinutesFocusedSince(startTime: Long): Flow<Int?>
}