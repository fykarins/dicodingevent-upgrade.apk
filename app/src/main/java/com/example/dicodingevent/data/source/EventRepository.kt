package com.example.dicodingevent.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.retrofit.ApiService
import com.example.dicodingevent.utils.AppExecutors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<EventEntity>>>()
    private val apiKey = "https://event-api.dicoding.dev/"

    fun getHeadlineEvent(): LiveData<Result<List<EventEntity>>> {
        result.value = Result.Loading

        GlobalScope.launch {
            try {
                val response = apiService.getEvent(apiKey)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventList = ArrayList<EventEntity>()

                    appExecutors.diskIO.execute {
                        events.forEach { event ->
                            val isBookmarked = eventDao.isEventBookmarked(event.name)
                            val eventEntity = EventEntity(
                                id = event.id,
                                name = event.name,
                                description = event.description,
                                ownerName = event.ownerName,
                                cityName = event.cityName,
                                quota = event.quota,
                                registrants = event.registrants,
                                imageLogo = event.imageLogo,
                                beginTime = event.beginTime,
                                endTime = event.endTime,
                                link = event.link,
                                mediaCover = event.mediaCover,
                                summary = event.summary,
                                category = event.category,
                                imageUrl = event.imageUrl,
                                active = event.active,
                                isBookmarked = isBookmarked
                            )
                            eventList.add(eventEntity)
                        }
                        eventDao.deleteAllNonBookmarked()
                        eventDao.insertEvents(eventList)
                    }
                }
            } catch (e: Exception) {
                result.value = Result.Error(e.message.toString())
            }
        }

        val localData = eventDao.getAllEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }

        return result
    }
    fun getBookmarkedEvents(): LiveData<List<EventEntity>> {
        return eventDao.getBookmarkedEvents()
    }
    fun setBookmarkedEvent(event: EventEntity, bookmarkState: Boolean) {
        appExecutors.diskIO.execute {
            event.isBookmarked = bookmarkState
            eventDao.updateEvent(event)
        }
    }
    fun getFinishedEvents(): LiveData<List<EventEntity>> {
        return eventDao.getEventsByStatus(false) // Misalkan "false" berarti event sudah selesai
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }

}