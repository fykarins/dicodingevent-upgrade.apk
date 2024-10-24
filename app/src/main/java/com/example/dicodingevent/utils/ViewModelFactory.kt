package com.example.dicodingevent.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.source.EventRepository
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.ui.detail.DetailViewModel
import com.example.dicodingevent.ui.event.FinishedViewModel
import com.example.dicodingevent.ui.event.UpcomingViewModel
import com.example.dicodingevent.ui.favorite.FavoriteViewModel
import com.example.dicodingevent.ui.main.MainViewModel

class ViewModelFactory private constructor(
    private val EventRepository: EventRepository,
    private val eventDao: EventDao

) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(EventRepository) as T
        } else if (modelClass.isAssignableFrom(UpcomingViewModel::class.java)) {
            return UpcomingViewModel(EventRepository) as T
        }
        else if (modelClass.isAssignableFrom(FinishedViewModel::class.java)) {
            return FinishedViewModel(EventRepository) as T
        }
        else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(EventRepository) as T
        }
        else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel() as T
        }
        else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideEventDao(context)
                )
            }.also { instance = it }
    }
}