package com.example.dicodingevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.source.EventRepository

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return repository.getFavoriteEvents()
    }
    fun toggleBookmarkStatus(eventItem: ListEventsItem) {
        repository.updateBookmarkStatus(eventItem.id, !eventItem.isBookmarked)
    }
}