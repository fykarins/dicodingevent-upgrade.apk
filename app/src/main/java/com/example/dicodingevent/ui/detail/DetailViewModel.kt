package com.example.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.retrofit.ApiConfig

class DetailViewModel : ViewModel() {
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
}
