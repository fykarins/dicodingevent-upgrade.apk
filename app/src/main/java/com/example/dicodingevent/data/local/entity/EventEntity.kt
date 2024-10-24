package com.example.dicodingevent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val ownerName: String,
    val cityName: String,
    val quota: Int,
    val registrants: Int,
    val imageLogo: String,
    val imageUrl: String?,
    val beginTime: String,
    val endTime: String,
    val link: String,
    val mediaCover: String,
    val summary: String,
    val category: String,
    val active: Boolean,
    var isBookmarked: Boolean,
    var isFavorite: Boolean = false
)