package com.example.dicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dicodingevent.data.local.entity.EventEntity

@Database(entities = [EventEntity::class], version = 2, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE events ADD COLUMN mediaCover TEXT DEFAULT '' NOT NULL")
                    db.execSQL("ALTER TABLE events ADD COLUMN summary TEXT DEFAULT '' NOT NULL")
                    db.execSQL("ALTER TABLE events ADD COLUMN isBookmarked INTEGER DEFAULT 0 NOT NULL")
                    db.execSQL("ALTER TABLE events ADD COLUMN imageLogo TEXT DEFAULT '' NOT NULL")

                    db.execSQL("ALTER TABLE events ADD COLUMN imageUrl TEXT DEFAULT NULL")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun getInstance(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "Event.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}