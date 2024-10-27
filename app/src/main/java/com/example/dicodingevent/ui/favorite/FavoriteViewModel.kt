package com.example.dicodingevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.source.EventRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    private val _favoriteEvents = MutableLiveData<List<EventEntity>>()
    val favoriteEvents: LiveData<List<EventEntity>> get() = _favoriteEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchFavoriteEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Adjust this to get favorite events based on a separate "favorite" flag if you add it
                repository.getFavoriteEvents().observeForever { events ->
                    _favoriteEvents.value = events
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load favorite events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavoriteEvent(eventItem: ListEventsItem) {
        viewModelScope.launch {
            try {
                repository.addEventToFavorites(eventItem.id)
                fetchFavoriteEvents()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add favorite event: ${e.message}"
            }
        }
    }

    fun deleteFavoriteEvent(eventItem: ListEventsItem) {
        viewModelScope.launch {
            try {
                repository.removeEventFromFavorites(eventItem.id)
                fetchFavoriteEvents()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove favorite event: ${e.message}"
            }
        }
    }
}