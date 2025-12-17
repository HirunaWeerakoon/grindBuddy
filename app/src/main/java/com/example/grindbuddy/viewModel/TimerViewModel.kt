package com.example.grindbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.grindbuddy.GrindBuddyApplication // <--- Make sure this import exists
import com.example.grindbuddy.data.FocusSession
import com.example.grindbuddy.data.SessionDao
import com.example.grindbuddy.data.UserStatsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. We added 'sessionDao' to the constructor
class TimerViewModel(
    private val repository: UserStatsRepository,
    private val sessionDao: SessionDao
) : ViewModel() {

    private val _timeLeft = MutableStateFlow(5L) // Test Mode (5 sec)
    val timeLeft = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished = _isSessionFinished.asStateFlow()

    // --- DATASTORE (Sticky Notes) ---
    val totalXp = repository.totalXp.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val totalCoins = repository.totalCoins.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // --- DATABASE (The Excel Sheet) ---

    // A. The Raw List (For the "History" Tab)
    val sessionHistory = sessionDao.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // B. The Graph Data (For the "Weekly" Tab)
    val weeklyStats = sessionHistory.map { history ->
        calculateWeeklyStats(history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_isRunning.value) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        _isRunning.value = true
        _isSessionFinished.value = false
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
            _isSessionFinished.value = true
        }
    }

    private fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun claimReward() {
        viewModelScope.launch {
            // 1. Update DataStore (Sticky Notes)
            repository.addXp(50)
            repository.addCoins(10)

            // 2. Update Database (Excel Sheet) <--- THIS WAS MISSING
            val session = FocusSession(
                dateTimestamp = System.currentTimeMillis(),
                durationMinutes = 25, // Hardcoded for now
                xpEarned = 50
            )
            sessionDao.insertSession(session)

            resetSession()
        }
    }

    private fun resetSession() {
        pauseTimer()
        _timeLeft.value = 5L
        _isSessionFinished.value = false
    }

    fun getFormattedTime(): String {
        val totalSeconds = _timeLeft.value
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // --- HELPER FUNCTION: Calculate Graph Data ---
    private fun calculateWeeklyStats(history: List<FocusSession>): List<Pair<String, Int>> {
        val stats = mutableListOf<Pair<String, Int>>()
        val today = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // Loop backwards for the last 7 days
        for (i in 6 downTo 0) {
            val targetDayStart = today - (i * oneDayMillis)


            val dayName = SimpleDateFormat("EEE", Locale.getDefault())
                .format(Date(targetDayStart))
                .take(2)


            val minutesThatDay = history.filter { session ->
                val sessionDay = SimpleDateFormat("EEE", Locale.getDefault())
                    .format(Date(session.dateTimestamp))
                    .take(2)
                sessionDay == dayName
            }.sumOf { it.durationMinutes }

            stats.add(dayName to minutesThatDay)
        }
        return stats
    }

    // --- FACTORY ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GrindBuddyApplication)
                val repository = UserStatsRepository(application.applicationContext)

                // Get the DAO from the Database <--- THIS WAS MISSING
                val sessionDao = application.database.sessionDao()

                TimerViewModel(repository, sessionDao)
            }
        }
    }
}