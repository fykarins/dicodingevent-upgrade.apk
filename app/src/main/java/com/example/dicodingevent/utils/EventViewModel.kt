package com.example.dicodingevent.utils

import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.source.EventRepository

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getHeadlineEvents() = eventRepository.getHeadlineEvent()
}