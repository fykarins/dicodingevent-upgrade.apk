package com.example.dicodingevent.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig

class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchEvents(query: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEvents(-1)
                if (response.isSuccessful && response.body() != null) {
                    _events.value = response.body()?.listEvents?.filter { it.name.contains(query, true) }
                    if (_events.value.isNullOrEmpty()) {
                        _error.value = "No events found for query: $query"
                    }
                } else {
                    _error.value = "Failed to retrieve events."
                }
            } catch (e: IOException) {
                _error.value = "No internet connection."
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }
}
