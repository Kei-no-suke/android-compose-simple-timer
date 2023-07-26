package com.example.simplecomposetimer.data

import android.content.Context
import com.example.simplecomposetimer.alarm.AndroidAlarmScheduler

interface AppContainer {
    val timerItemsRepository: TimerItemsRepository
    val androidAlarmScheduler: AndroidAlarmScheduler
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val timerItemsRepository: TimerItemsRepository by lazy {
        OfflineTimerItemsRepository(TimerDatabase.getDatabase(context).timerItemDao())
    }
    override val androidAlarmScheduler: AndroidAlarmScheduler by lazy {
        AndroidAlarmScheduler(context)
    }
}