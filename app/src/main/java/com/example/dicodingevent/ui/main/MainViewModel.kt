package com.example.dicodingevent.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.EventResponse
import kotlinx.coroutines.launch
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.utils.SettingPreferences

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "MainViewModel"
        private const val LIMIT = 10
    }

    init {
        findEvents()
    }

    private fun findEvents(active: Int = 0) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEvents(active, LIMIT)
                if (response.isSuccessful) {
                    val eventResponse: EventResponse? = response.body()
                    _listEvents.value = eventResponse?.listEvents ?: emptyList()
                } else {
                    handleError(response.message())
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents("-1", query)
                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()?.listEvents ?: emptyList()
                    _listEvents.value = events
                    if (events.isEmpty()) {
                        _errorMessage.value = "No events found."
                    }
                } else {
                    handleError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(message: String) {
        _errorMessage.value = message
        Log.e(TAG, "Error: $message")
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}
