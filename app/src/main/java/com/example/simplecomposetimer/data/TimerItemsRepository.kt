package com.example.simplecomposetimer.data

import kotlinx.coroutines.flow.Flow

interface TimerItemsRepository {

    // retrieve all the timer items from the given data source
    fun getAllTimerItemsStream(): Flow<List<TimerItem>>

    // retrieve an timer item from the given data source that matches with the [id]
    fun getTimerItemStream(id: Int): Flow<TimerItem>

    // insert timer item in the data source
    suspend fun insertTimerItem(timerItem: TimerItem)

    // update timer item in the data source
    suspend fun updateTimerItem(timerItem: TimerItem)

    // delete timer item from data source
    suspend fun deleteTimerItem(timerItem: TimerItem)
}