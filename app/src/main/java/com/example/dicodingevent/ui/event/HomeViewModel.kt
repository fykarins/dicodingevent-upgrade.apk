package com.example.dicodingevent.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.data.source.Result
import com.example.dicodingevent.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _events = MutableLiveData<Result<List<EventEntity>>>()
    val events: LiveData<Result<List<EventEntity>>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getHeadlineEvent()
    }

    private fun getHeadlineEvent() {
        viewModelScope.launch {
            _events.value = Result.Loading
            try {
                _events.postValue(repository.getHeadlineEvent().value)
            } catch (e: Exception) {
                _events.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.setBookmarkedEvent(event, true)
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.setBookmarkedEvent(event, false)
        }
    }
}
