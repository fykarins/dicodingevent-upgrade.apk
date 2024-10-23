package com.example.dicodingevent.ui.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.source.EventRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class FinishedViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchFinishedEvents()
    }

    fun getFinishedEvents() {
        _isLoading.value = true
        eventRepository.getFinishedEvents().observeForever { events ->
            _finishedEvents.value = events.map { eventEntityToListEventsItem(it) }
            _isLoading.value = false
        }
    }

    fun deleteEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(eventEntity, false)
        }
    }

    fun saveEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(eventEntity, true)
        }
    }

    private fun eventEntityFromListEventsItem(item: ListEventsItem): EventEntity {
        return EventEntity(
            id = item.id,
            name = item.name,
            description = item.description,
            ownerName = item.ownerName,
            cityName = item.cityName,
            quota = item.quota,
            registrants = item.registrants,
            imageLogo = item.imageLogo,
            beginTime = item.beginTime,
            endTime = item.endTime,
            link = item.link,
            mediaCover = item.mediaCover,
            summary = item.summary,
            category = item.category,
            imageUrl = item.imageUrl,
            active = true,
            isBookmarked = item.isBookmarked
        )
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<EventResponse> = ApiConfig.getApiService().getEvents(active = 0)
                if (response.isSuccessful && response.body() != null) {
                    _finishedEvents.value = response.body()?.listEvents ?: listOf()
                } else {
                    Log.e("FinishedViewModel", "Failed to fetch finished events: ${response.message()}")
                    _finishedEvents.value = listOf()
                }
            } catch (e: Exception) {
                Log.e("FinishedViewModel", "Exception during fetch: ${e.message}")
                _finishedEvents.value = listOf()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun eventEntityToListEventsItem(eventEntity: EventEntity): ListEventsItem {
        return ListEventsItem(
            id = eventEntity.id,
            name = eventEntity.name,
            description = eventEntity.description,
            imageLogo = eventEntity.imageLogo,
            cityName = eventEntity.cityName,
            endTime = eventEntity.endTime,
            beginTime = eventEntity.beginTime,
            category = eventEntity.category,
            imageUrl = eventEntity.imageUrl,
            link = eventEntity.link,
            mediaCover = eventEntity.mediaCover,
            ownerName = eventEntity.ownerName,
            quota = eventEntity.quota,
            registrants = eventEntity.registrants,
            summary = eventEntity.summary,
            active = eventEntity.active,
            isBookmarked = eventEntity.isBookmarked
        )
    }
}
