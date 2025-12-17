package com.example.grindbuddy.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_stats")

class UserStatsRepository(private val context: Context) {

    companion object {
        val XP_KEY = intPreferencesKey("total_xp")
        val COINS_KEY = intPreferencesKey("total_coins")

        val DAILY_LEVEL_KEY = intPreferencesKey("daily_level")   // Tracks "Study 4 hrs" -> "Study 6 hrs"
        val WEEKLY_LEVEL_KEY = intPreferencesKey("weekly_level") // Tracks "Study 20 hrs/week"

        val STREAK_KEY = intPreferencesKey("current_streak")
        val LAST_DATE_KEY = longPreferencesKey("last_study_date") // To check if you studied yesterday
    }

    // 1. READERS (The Flow of Data)
    val totalXp: Flow<Int> = context.dataStore.data.map { it[XP_KEY] ?: 0 }
    val totalCoins: Flow<Int> = context.dataStore.data.map { it[COINS_KEY] ?: 0 }

    // Default to Level 1 for quests
    val dailyLevel: Flow<Int> = context.dataStore.data.map { it[DAILY_LEVEL_KEY] ?: 1 }
    val weeklyLevel: Flow<Int> = context.dataStore.data.map { it[WEEKLY_LEVEL_KEY] ?: 1 }

    // Default to 0 for streak
    val currentStreak: Flow<Int> = context.dataStore.data.map { it[STREAK_KEY] ?: 0 }
    val lastStudyDate: Flow<Long> = context.dataStore.data.map { it[LAST_DATE_KEY] ?: 0L }

    // 2. WRITERS (The Actions)
    suspend fun addXp(amount: Int) {
        context.dataStore.edit { it[XP_KEY] = (it[XP_KEY] ?: 0) + amount }
    }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { it[COINS_KEY] = (it[COINS_KEY] ?: 0) + amount }
    }

    // --- NEW HELPERS ---

    suspend fun increaseDailyLevel() {
        context.dataStore.edit { it[DAILY_LEVEL_KEY] = (it[DAILY_LEVEL_KEY] ?: 1) + 1 }
    }

    suspend fun increaseWeeklyLevel() {
        context.dataStore.edit { it[WEEKLY_LEVEL_KEY] = (it[WEEKLY_LEVEL_KEY] ?: 1) + 1 }
    }

    suspend fun updateStreak(newStreak: Int, todayDate: Long) {
        context.dataStore.edit {
            it[STREAK_KEY] = newStreak
            it[LAST_DATE_KEY] = todayDate
        }
    }
}