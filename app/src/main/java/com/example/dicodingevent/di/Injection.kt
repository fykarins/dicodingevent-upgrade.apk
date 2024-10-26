package com.example.dicodingevent.di

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.local.room.EventDatabase
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.utils.AppExecutors
import com.example.dicodingevent.utils.ReminderWorker
import java.util.concurrent.TimeUnit

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

    fun setupDailyReminder(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
