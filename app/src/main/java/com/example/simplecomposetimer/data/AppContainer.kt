package com.example.simplecomposetimer.data

import android.content.Context

interface AppContainer {
    val timerItemsRepository: TimerItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val timerItemsRepository: TimerItemsRepository by lazy {
        OfflineTimerItemsRepository(TimerDatabase.getDatabase(context).timerItemDao())
    }
}