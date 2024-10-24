package com.example.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailViewModel(private val eventDao: EventDao) : ViewModel() {
    private val _eventDetail = MutableLiveData<DetailEventResponse>()
    val eventDetail: LiveData<DetailEventResponse> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<DetailEventResponse> = ApiConfig.getApiService().getDetail(eventId)
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                } else {
                    Log.e("DetailViewModel", "Error fetching event detail: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addEventToFavorite(event: EventEntity) {
        viewModelScope.launch {
            try {
                eventDao.insert(event)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error adding event to favorites: ${e.message}")
            }
        }
    }

    fun removeEventFromFavorite(eventId: Int) {
        viewModelScope.launch {
            try {
                eventDao.deleteById(eventId)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error removing event from favorites: ${e.message}")
            }
        }
    }

    fun getFavoriteEventById(eventId: Int): LiveData<EventEntity?> {
        return eventDao.getFavoriteEventById(eventId)
    }
}
