package com.example.dicodingevent.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig

class MainViewModel : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        findEvents()
    }

    private fun findEvents(active: Int = 0) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEvents(active)
                if (response.isSuccessful) {
                    _listEvents.value = response.body()?.listEvents ?: listOf()
                    if (_listEvents.value.isNullOrEmpty()) {
                        _errorMessage.value = "No events found."
                    }
                } else {
                    handleError(response.message())
                }
            } catch (e: Exception) {
                handleError(e.message)
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
                val response = ApiConfig.getApiService().searchEvents(-1, query)
                if (response.isSuccessful && response.body() != null) {
                    _listEvents.value = response.body()?.listEvents ?: listOf()
                    if (_listEvents.value.isNullOrEmpty()) {
                        _errorMessage.value = "No events found."
                    }
                } else {
                    _errorMessage.value = "No events found."
                }
            } catch (e: Exception) {
                handleError(e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(message: String?) {
        _errorMessage.value = "Error: $message"
        Log.e(TAG, "onFailure: $message")
    }
}