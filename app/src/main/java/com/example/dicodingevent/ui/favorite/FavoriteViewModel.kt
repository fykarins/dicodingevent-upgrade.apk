package com.example.dicodingevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.source.EventRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    val favoriteEvents: LiveData<List<EventEntity>> = repository.getFavoriteEvents()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addFavoriteEvent(eventItem: EventEntity) {
        viewModelScope.launch {
            try {
                repository.addEventToFavorites(eventItem.id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add favorite event: ${e.message}"
            }
        }
    }

    fun deleteFavoriteEvent(eventItem: EventEntity) {
        viewModelScope.launch {
            try {
                repository.removeEventFromFavorites(eventItem.id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove favorite event: ${e.message}"
            }
        }
    }
}
