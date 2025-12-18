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
import kotlinx.coroutines.flow.first
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

    val dailyLevel = repository.dailyLevel.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val minutesToday = weeklyStats.map { list ->
        // Find today's entry (e.g. "Tu")
        val todayName = SimpleDateFormat("EEE", Locale.getDefault()).format(Date()).take(2)
        list.find { it.first == todayName }?.second ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dailyTarget = dailyLevel.map { level ->
        level * 60 // Example: Level 1 = 60 mins, Level 4 = 240 mins
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    val weeklyLevel = repository.weeklyLevel.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val minutesThisWeek = weeklyStats.map { list ->
        list.sumOf { it.second } // Sum up all the bars
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyTarget = weeklyLevel.map { level ->
        level * 300 // Example: Level 1 = 300 mins (5 hours), Level 2 = 600 mins...
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)

    // --- STREAK QUEST LOGIC ---
    val currentStreak = repository.currentStreak.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val streakTarget = currentStreak.map { streak ->
        if (streak < 3) 3 else streak + 3 // Simple rule: Keep chasing +3 days
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    fun claimWeeklyQuest() {
        viewModelScope.launch {
            repository.addXp(200)
            repository.addCoins(100)
            repository.increaseWeeklyLevel()
        }
    }

    fun claimStreakQuest() {
        viewModelScope.launch {
            repository.addXp(50)
            repository.addCoins(20)
        }
    }

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
    fun claimDailyQuest() {
        viewModelScope.launch {
            repository.addXp(100)      // Big Reward!
            repository.addCoins(50)
            repository.increaseDailyLevel() // Level Up! (Next target will be harder)
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
            updateStreakLogic()

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
    // --- STREAK ALGORITHM ---
    private suspend fun updateStreakLogic() {
        // 1. Get the last saved date and streak
        // "first()" grabs the current value from the Flow just once (without listening forever)
        val lastDate = repository.lastStudyDate.first()
        val currentStreak = repository.currentStreak.first()

        // 2. Calculate "Today" and "Yesterday" (Strip out the time, keep only date)
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.timeInMillis

        // Helper to strip time:
        fun getStartOfDay(time: Long): Long {
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = time
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        val todayStart = getStartOfDay(today)
        val lastDateStart = getStartOfDay(lastDate)

        // 3. The Logic Tree
        if (todayStart == lastDateStart) {
            // Case A: You already studied today. No change.
            return
        }

        val oneDayMillis = 24 * 60 * 60 * 1000L
        if (todayStart == lastDateStart + oneDayMillis) {
            // Case B: You studied yesterday! Streak goes UP.
            repository.updateStreak(currentStreak + 1, today)
        } else {
            // Case C: You missed a day (or it's your first day). Reset to 1.
            repository.updateStreak(1, today)
        }
    }
}