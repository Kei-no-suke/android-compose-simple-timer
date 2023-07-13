package com.example.simplecomposetimer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerItemDao {

    @Query("SELECT * from timerItems ORDER BY totalSecond ASC")
    fun getAllTimerItems(): Flow<List<TimerItem>>

    @Query("SELECT * from timerItems WHERE id = :id")
    fun getTimerItem(id: Int): Flow<TimerItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(timerItem: TimerItem)

    @Update
    suspend fun update(timerItem: TimerItem)

    @Delete
    suspend fun delete(timerItem: TimerItem)
}