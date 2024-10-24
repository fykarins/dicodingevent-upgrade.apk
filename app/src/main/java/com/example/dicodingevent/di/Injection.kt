package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.local.room.EventDatabase
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }

    fun provideEventDao(context: Context): EventDao {
        val database = EventDatabase.getInstance(context)
        return database.eventDao()
    }
}