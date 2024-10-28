package com.example.dicodingevent.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.source.EventRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _favoriteEvents = MutableLiveData<List<EventEntity>>()
    val favoriteEvents: LiveData<List<EventEntity>> get() = _favoriteEvents

    val bookmarkedEvents: LiveData<List<ListEventsItem>> = eventRepository.getBookmarkedEvents().map { events ->
        events.map { event ->
            ListEventsItem(
                id = event.id,
                name = event.name,
                description = event.description,
                imageLogo = event.imageLogo,
                isBookmarked = event.isBookmarked,
                beginTime = event.beginTime,
                category = event.category,
                cityName = event.cityName,
                endTime = event.endTime,
                link = event.link,
                mediaCover = event.mediaCover,
                ownerName = event.ownerName,
                quota = event.quota,
                registrants = event.registrants,
                summary = event.summary,
                imageUrl = event.imageUrl,
                active = event.active
            )
        }
    }

    fun fetchFavoriteEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val favoriteEventsList = eventRepository.getFavoriteEvents().value ?: emptyList()
                _favoriteEvents.postValue(favoriteEventsList)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load favorite events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveEvent(event: ListEventsItem) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                eventRepository.saveEvent(
                    EventEntity(
                        id = event.id,
                        name = event.name,
                        description = event.description,
                        imageLogo = event.imageLogo,
                        isBookmarked = event.isBookmarked,
                        beginTime = event.beginTime,
                        category = event.category,
                        cityName = event.cityName,
                        endTime = event.endTime,
                        link = event.link,
                        mediaCover = event.mediaCover,
                        ownerName = event.ownerName,
                        quota = event.quota,
                        registrants = event.registrants,
                        summary = event.summary,
                        imageUrl = event.imageUrl,
                        active = event.active
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(event: ListEventsItem) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                eventRepository.deleteEvent(
                    EventEntity(
                        id = event.id,
                        name = event.name,
                        description = event.description,
                        imageLogo = event.imageLogo,
                        isBookmarked = event.isBookmarked,
                        beginTime = event.beginTime,
                        category = event.category,
                        cityName = event.cityName,
                        endTime = event.endTime,
                        link = event.link,
                        mediaCover = event.mediaCover,
                        ownerName = event.ownerName,
                        quota = event.quota,
                        registrants = event.registrants,
                        summary = event.summary,
                        imageUrl = event.imageUrl,
                        active = event.active
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
