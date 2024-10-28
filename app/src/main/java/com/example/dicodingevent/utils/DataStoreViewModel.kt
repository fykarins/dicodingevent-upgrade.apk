package com.example.dicodingevent.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DataStoreViewModel(private val settingPreferences: SettingPreferences) : ViewModel() {

    // LiveData for dark mode and daily reminder settings
    val darkMode = settingPreferences.getThemeSetting().asLiveData()
    val dailyReminder = settingPreferences.isReminderEnabled().asLiveData()

    // Set methods for dark mode and daily reminder settings
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveThemeSetting(enabled)
        }
    }

    fun setDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingPreferences.setReminderEnabled(enabled)
        }
    }

    // Factory class for creating DataStoreViewModel instances
    class Factory(private val settingPreferences: SettingPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DataStoreViewModel(settingPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
