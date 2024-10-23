package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY beginTime DESC")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isBookmarked = 1")
    fun getBookmarkedEvents(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvents(events: List<EventEntity>)

    @Update
    fun updateEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE isBookmarked = 0")
    fun deleteAllNonBookmarked()

    @Query("SELECT EXISTS(SELECT * FROM events WHERE name = :name AND isBookmarked = 1)")
    fun isEventBookmarked(name: String): Boolean

    @Query("SELECT * FROM events WHERE active = :status")
    fun getEventsByStatus(status: Boolean): LiveData<List<EventEntity>>

}
