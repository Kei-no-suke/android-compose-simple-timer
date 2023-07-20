package com.example.simplecomposetimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timerItems")
data class TimerItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val totalSecond: Long,
    val timerActive: Boolean = false,
    val startDate: Long? = null,
    val stopHour: Int,
    val stopMinute: Int,
    val stopSecond: Int
)
