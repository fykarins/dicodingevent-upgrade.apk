package com.example.dicodingevent.ui.bookmark

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.source.EventRepository

class BookmarkViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getBookmarkedEvents(): LiveData<List<EventEntity>> {
        return eventRepository.getBookmarkedEvents()
    }
}
