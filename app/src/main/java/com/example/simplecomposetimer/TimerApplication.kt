package com.example.simplecomposetimer

import android.app.Application
import com.example.simplecomposetimer.data.AppContainer
import com.example.simplecomposetimer.data.AppDataContainer

class TimerApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}