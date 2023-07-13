package com.example.simplecomposetimer.data

import kotlinx.coroutines.flow.Flow

class OfflineTimerItemsRepository(private val timerItemDao: TimerItemDao) : TimerItemsRepository {
    override fun getAllTimerItemsStream(): Flow<List<TimerItem>> = timerItemDao.getAllTimerItems()

    override fun getTimerItemStream(id: Int): Flow<TimerItem> = timerItemDao.getTimerItem(id)

    override suspend fun insertTimerItem(timerItem: TimerItem) = timerItemDao.insert(timerItem)

    override suspend fun updateTimerItem(timerItem: TimerItem) = timerItemDao.update(timerItem)

    override suspend fun deleteTimerItem(timerItem: TimerItem) = timerItemDao.delete(timerItem)

}