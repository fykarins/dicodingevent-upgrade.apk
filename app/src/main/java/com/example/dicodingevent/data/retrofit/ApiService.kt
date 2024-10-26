package com.example.dicodingevent.data.retrofit

import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.response.EventResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/events")
    suspend fun getEvents(@Query("active") active: Int, limit: Int): Response<EventResponse>

    @GET("/events")
    suspend fun getEvent(@Query("apiKey") apiKey: String): Response<EventResponse>

    @GET("/events/{id}")
    suspend fun getDetail(@Path("id") id: String): Response<DetailEventResponse>

    @GET("/events")
    suspend fun searchEvents(
        @Query("active") active: String,
        @Query("q") query: String
    ): Response<EventResponse>

    companion object {
        private const val BASE_URL = "https://event-api.dicoding.dev/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
