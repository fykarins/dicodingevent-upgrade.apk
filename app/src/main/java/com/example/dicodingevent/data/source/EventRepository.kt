package com.example.dicodingevent.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.dicodingevent.BuildConfig
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiService
import com.example.dicodingevent.utils.AppExecutors
import kotlinx.coroutines.*

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<EventEntity>>>()

    fun getHeadlineEvent(): LiveData<Result<List<EventEntity>>> {
        // Avoid shadowing the class-level 'result' by renaming this
        val liveResult = MutableLiveData<Result<List<EventEntity>>>()
        liveResult.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getEvent(BuildConfig.API_KEY)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventEntities = events.map { event ->
                        EventEntity(
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
                            isBookmarked = false
                        )
                    }
                    withContext(Dispatchers.Main) {
                        liveResult.value = Result.Success(eventEntities)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        liveResult.value = Result.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    liveResult.value = Result.Error(e.message ?: "An error occurred")
                }
            }
        }
        return liveResult
    }

    fun getBookmarkedEvents(): LiveData<List<EventEntity>> {
        return eventDao.getBookmarkedEvents()
    }

    fun setBookmarkedEvent(event: EventEntity, bookmarkState: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            event.isBookmarked = bookmarkState
            eventDao.updateEvent(event)
        }
    }

    fun getFinishedEvents(): LiveData<List<EventEntity>> {
        return eventDao.getEventsByStatus(false)
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    suspend fun addEventToFavorites(eventId: Int) {
        eventDao.addToFavorites(eventId)
    }

    suspend fun removeEventFromFavorites(eventId: Int) {
        eventDao.removeFromFavorites(eventId)
    }

    fun updateBookmarkStatus(eventId: Int, isBookmarked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.updateBookmarkStatus(eventId, isBookmarked)
        }
    }

    fun getEvents(): LiveData<Result<List<EventEntity>>> {
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getEvent(BuildConfig.API_KEY)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventEntities = events.map { event ->
                        EventEntity(
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
                            isBookmarked = eventDao.isEventBookmarked(event.name)
                        )
                    }
                    eventDao.insertEvents(eventEntities)
                    eventDao.deleteAllNonBookmarked()
                } else {
                    withContext(Dispatchers.Main) {
                        result.value = Result.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result.value = Result.Error(e.message ?: "An error occurred")
                }
            }
        }

        val localData = eventDao.getAllEvents()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    fun searchEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val searchResult = MediatorLiveData<Result<List<EventEntity>>>()
        searchResult.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.searchEvents(BuildConfig.API_KEY, query)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventEntities = events.map { event ->
                        EventEntity(
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
                            isBookmarked = eventDao.isEventBookmarked(event.name)
                        )
                    }
                    eventDao.insertEvents(eventEntities)
                    withContext(Dispatchers.Main) {
                        searchResult.value = Result.Success(eventDao.searchEvents(query))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        searchResult.value = Result.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    searchResult.value = Result.Error(e.message ?: "An error occurred")
                }
            }
        }

        return searchResult
    }

    suspend fun getUpcomingEvent(limit: Int = 1): EventResponse? {
        return try {
            val response = apiService.getEvents(active = -1, limit = limit)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun saveEvent(event: EventEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.insert(event)
        }
    }

    fun deleteEvent(event: EventEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            eventDao.deleteById(event.id)
        }
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