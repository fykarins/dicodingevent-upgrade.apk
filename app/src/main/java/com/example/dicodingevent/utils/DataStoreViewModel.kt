package com.example.dicodingevent.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DataStoreViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val darkMode = dataStoreManager.darkModeFlow.asLiveData()
    val dailyReminder = dataStoreManager.dailyReminderFlow.asLiveData()

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDarkMode(enabled)
        }
    }

    fun setDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDailyReminder(enabled)
        }
    }

    class Factory(private val dataStoreManager: DataStoreManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DataStoreViewModel(dataStoreManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
