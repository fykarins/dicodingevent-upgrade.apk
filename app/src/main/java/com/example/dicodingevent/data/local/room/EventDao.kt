package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {

    // Queries to get events
    @Query("SELECT * FROM events ORDER BY beginTime DESC")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isBookmarked = 1")
    fun getBookmarkedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE active = :status")
    fun getEventsByStatus(status: Boolean): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<EventEntity?>

    @Query("SELECT EXISTS(SELECT * FROM events WHERE name = :name AND isBookmarked = 1)")
    fun isEventBookmarked(name: String): Boolean

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%'")
    fun searchEvents(query: String): List<EventEntity>

    // Insert operations
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    // Update operations
    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE events SET isBookmarked = :bookmarked WHERE id = :eventId")
    suspend fun updateBookmarkStatus(eventId: Int, bookmarked: Boolean)

    // Update for favorite status
    @Query("UPDATE events SET isFavorite = 1 WHERE id = :eventId")
    suspend fun addToFavorites(eventId: Int)

    @Query("UPDATE events SET isFavorite = 0 WHERE id = :eventId")
    suspend fun removeFromFavorites(eventId: Int)

    // Delete operations
    @Query("DELETE FROM events WHERE isBookmarked = 0")
    suspend fun deleteAllNonBookmarked()

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Int)

    companion object {
        fun getInstance(): EventDao {
            throw NotImplementedError("Implement using Room database instance")
        }
    }
}