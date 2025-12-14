package com.example.grindbuddy.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates a small file named "user_stats" on the phone
private val Context.dataStore by preferencesDataStore(name = "user_stats")

class UserStatsRepository(private val context: Context) {

    companion object {
        val XP_KEY = intPreferencesKey("total_xp")
        val COINS_KEY = intPreferencesKey("total_coins")
    }

    // 1. READ XP (Stream of data)
    // If no XP exists yet, default to 0
    val totalXp: Flow<Int> = context.dataStore.data.map { it[XP_KEY] ?: 0 }
    val totalCoins: Flow<Int> = context.dataStore.data.map { it[COINS_KEY] ?: 0 }

    suspend fun addXp(amount: Int) {
        context.dataStore.edit { it[XP_KEY] = (it[XP_KEY] ?: 0) + amount }
    }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { it[COINS_KEY] = (it[COINS_KEY] ?: 0) + amount }
    }
}