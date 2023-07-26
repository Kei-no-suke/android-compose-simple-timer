package com.example.simplecomposetimer.alarm

import com.example.simplecomposetimer.data.toSeconds
import com.example.simplecomposetimer.ui.TimerUiState
import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val id: Int
)

fun TimerUiState.toAlarmItem(): AlarmItem{
    val alarmTime = LocalDateTime.now().plusSeconds(this.durationTime.toSeconds().toLong())
    return AlarmItem(
        time = alarmTime,
        id = timerId
    )
}