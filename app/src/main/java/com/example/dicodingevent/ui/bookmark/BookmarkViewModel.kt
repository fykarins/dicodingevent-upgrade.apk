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

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val bookmarkedEvents: LiveData<List<ListEventsItem>> = eventRepository.getBookmarkedEvents().map { events ->
        events.map { event ->
            ListEventsItem(
                id = event.id,
                name = event.name,
                description = event.description,
                imageLogo = event.imageLogo,
                isBookmarked = true,
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

    fun saveEvent(event: ListEventsItem) {
        viewModelScope.launch {
            try {
                eventRepository.saveEvent(
                    EventEntity(
                        id = event.id,
                        name = event.name,
                        description = event.description,
                        imageLogo = event.imageLogo,
                        isBookmarked = true,
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
                _errorMessage.value = "Failed to save bookmark: ${e.message}"
            }
        }
    }

    fun deleteEvent(event: ListEventsItem) {
        viewModelScope.launch {
            try {
                eventRepository.deleteEvent(
                    EventEntity(
                        id = event.id,
                        name = event.name,
                        description = event.description,
                        imageLogo = event.imageLogo,
                        isBookmarked = false,
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
                _errorMessage.value = "Failed to delete bookmark: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
