package com.sourav.streaks.ui.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sourav.streaks.data.FrequencyType
import com.sourav.streaks.data.Streak
import com.sourav.streaks.data.StreakExportDto
import com.sourav.streaks.data.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StreakRepository.getInstance()
    private val context = getApplication<Application>().applicationContext

    private val THEME_KEY = stringPreferencesKey("theme")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

    private val _theme = MutableStateFlow("system")
    val theme: StateFlow<String> = _theme

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // Expose streaks for export/import
    val streaksLiveData: LiveData<List<Streak>> = repository.streaks

    init {
        viewModelScope.launch {
            context.dataStore.data.map { prefs -> prefs[THEME_KEY] ?: "system" }.collect {
                _theme.value = it
            }
        }
        viewModelScope.launch {
            context.dataStore.data.map { prefs -> prefs[NOTIFICATIONS_KEY] ?: false }.collect {
                _notificationsEnabled.value = it
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { context.dataStore.edit { prefs -> prefs[THEME_KEY] = theme } }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs -> prefs[NOTIFICATIONS_KEY] = enabled }
        }
    }

    fun addStreak(
            name: String,
            emoji: String,
            frequency: FrequencyType,
            frequencyCount: Int,
            color: String = "#FF9900"
    ) {
        repository.addStreak(name, emoji, frequency, frequencyCount, context, color)
    }

    fun getStreaksForExport(): List<Streak> {
        return streaksLiveData.value ?: emptyList()
    }

    fun setStreaksFromImport(streaks: List<Streak>) {
        repository.setStreaksFromImport(streaks, context)
    }

    fun loadStreaksFromFile() {
        repository.loadStreaksFromFile(context)
    }

    fun getStreaksForExportDto(): List<StreakExportDto> {
        return (streaksLiveData.value ?: emptyList()).map { streak ->
            StreakExportDto(
                    id = streak.id,
                    name = streak.name,
                    emoji = streak.emoji,
                    frequency = streak.frequency,
                    frequencyCount = streak.frequencyCount,
                    createdDate = streak.createdDate,
                    lastCompletedDate = streak.lastCompletedDate,
                    currentStreak = streak.currentStreak,
                    bestStreak = streak.bestStreak,
                    completions = streak.completions,
                    reminder = streak.reminder,
                    color = streak.color
            )
        }
    }
}
