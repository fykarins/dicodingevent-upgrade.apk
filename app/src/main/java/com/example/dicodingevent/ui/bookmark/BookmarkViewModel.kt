package com.example.dicodingevent.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.source.EventRepository

class BookmarkViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

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

    fun saveEvent(event: ListEventsItem) {
        _isLoading.value = true
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

    fun deleteEvent(event: ListEventsItem) {
        _isLoading.value = true
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
