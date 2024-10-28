package com.example.dicodingevent.data.source

import androidx.lifecycle.LiveData
import com.example.dicodingevent.BuildConfig
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.asFlow

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) {

    fun getHeadlineEvents(): Flow<Result<List<EventEntity>>> = flow {
        emit(Result.Loading)
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
                emit(Result.Success(eventEntities))
            } else {
                emit(Result.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

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

    fun getEvents(): Flow<Result<List<EventEntity>>> = flow {
        emit(Result.Loading)

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

                eventDao.getAllEvents().asFlow().collect { localData ->
                    emit(Result.Success(localData))
                }
            } else {
                emit(Result.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun searchEvents(query: String): Flow<Result<List<EventEntity>>> = flow {
        emit(Result.Loading)

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
                val searchResults = eventDao.searchEvents(query)
                emit(Result.Success(searchResults))
            } else {
                emit(Result.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

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
            eventDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}
