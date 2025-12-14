package com.example.grindbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.grindbuddy.data.UserStatsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Add 'repository' to the constructor
class TimerViewModel(private val repository: UserStatsRepository) : ViewModel() {

    private val _timeLeft = MutableStateFlow(5L) // Still in Test Mode (5 sec)
    val timeLeft = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished = _isSessionFinished.asStateFlow()

    // 2. EXPOSE XP TO UI
    // We convert the Repository's Flow into a StateFlow so Compose can read it easily
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

    // 3. CLAIM REWARD FUNCTION
    fun claimReward() {
        viewModelScope.launch {
            repository.addXp(50)     // +50 XP
            repository.addCoins(10)  // +10 Coins (New!)
            resetSession()
        }
    }

    private fun resetSession() {
        pauseTimer()
        _timeLeft.value = 5L // Reset to 5s for testing
        _isSessionFinished.value = false
    }

    fun getFormattedTime(): String {
        val totalSeconds = _timeLeft.value
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    // 4. FACTORY (Boilerplate to inject the Repository)
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // This creates the Repo using the Application Context
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application).applicationContext
                val repository = UserStatsRepository(context)
                TimerViewModel(repository)
            }
        }
    }
}