package com.example.dicodingevent.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import com.example.dicodingevent.data.response.EventResponse
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

    fun findEvents(active: Int = 0) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiService().getEvents(active)
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listEvents.value = response.body()?.listEvents ?: listOf()
                        if (_listEvents.value.isNullOrEmpty()) {
                            _errorMessage.value = "No events found."
                        } else {

                        }
                    } else {
                        _errorMessage.value = "Error: ${response.message()}"
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = "Exception: ${e.message}"
                    Log.e(TAG, "onFailure: ${e.message}")
                }
            }
        }
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().searchEvents(-1, query)
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        _listEvents.value = response.body()?.listEvents ?: listOf()

                        if (_listEvents.value.isNullOrEmpty()) {
                            _errorMessage.value = "No events found."
                        }
                    } else {
                        _errorMessage.value = "No events found."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = "Error occurred: ${e.message}"
                }
            }
        }
    }
}
