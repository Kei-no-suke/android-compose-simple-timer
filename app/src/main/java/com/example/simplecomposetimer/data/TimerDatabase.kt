package com.example.simplecomposetimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimerItem::class], version = 2, exportSchema = false)
abstract class TimerDatabase : RoomDatabase() {

    abstract fun timerItemDao(): TimerItemDao

    companion object {
        @Volatile
        private var Instance: TimerDatabase? = null

        fun getDatabase(context: Context): TimerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TimerDatabase::class.java, "timer_item_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}