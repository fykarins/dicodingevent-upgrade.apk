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
    suspend fun insertEvents(events: List<EventEntity>) // Added `suspend` for better async handling

    @Update
    suspend fun updateEvent(event: EventEntity) // Added `suspend` for better async handling

    @Query("DELETE FROM events WHERE isBookmarked = 0")
    suspend fun deleteAllNonBookmarked() // Added `suspend` for better async handling

    @Query("SELECT EXISTS(SELECT * FROM events WHERE name = :name AND isBookmarked = 1)")
    fun isEventBookmarked(name: String): Boolean

    @Query("SELECT * FROM events WHERE active = :status")
    fun getEventsByStatus(status: Boolean): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Int)

    @Query("SELECT * FROM events WHERE isBookmarked = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("UPDATE events SET isBookmarked = :bookmarked WHERE id = :eventId")
    suspend fun updateBookmarkStatus(eventId: Int, bookmarked: Boolean) // Fixed missing function body

    companion object {
        fun getInstance(): EventDao {
            throw NotImplementedError("Implement using Room database instance")
        }
    }
}
