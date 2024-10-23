package com.example.dicodingevent.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.data.source.Result
import kotlinx.coroutines.launch
import java.io.IOException

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _events = MutableLiveData<Result<List<EventEntity>>>()
    val events: LiveData<Result<List<EventEntity>>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getHeadlineEvent() {
        viewModelScope.launch {
            _events.value = Result.Loading
            try {
                eventRepository.getHeadlineEvent().observeForever { result ->
                    when (result) {
                        is Result.Loading -> {
                            // No action needed
                        }
                        is Result.Success -> {
                            _events.value = result
                        }
                        is Result.Error -> {
                            _events.value = Result.Error(result.error)
                        }
                    }
                }
            } catch (e: IOException) {
                _error.value = "No internet connection."
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun getBookmarkedEvents() = eventRepository.getBookmarkedEvents()

    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(event, true)
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(event, false)
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            val result = _events.value
            if (result is Result.Success) {
                val filteredEvents = result.data.filter { it.name.contains(query, true) }
                _events.value = Result.Success(filteredEvents)
                if (filteredEvents.isEmpty()) {
                    _error.value = "No events found for query: $query"
                }
            } else {
                _error.value = "Failed to retrieve events."
            }
        }
    }
}
