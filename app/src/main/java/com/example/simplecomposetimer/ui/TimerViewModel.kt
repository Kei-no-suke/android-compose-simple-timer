package com.example.simplecomposetimer.ui

import androidx.lifecycle.ViewModel
import com.example.simplecomposetimer.ui.data.DurationTime
import com.example.simplecomposetimer.ui.data.TimerScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.simplecomposetimer.ui.data.TimerString
import com.example.simplecomposetimer.ui.data.toCharArray
import com.example.simplecomposetimer.ui.data.toDurationTime
import java.util.Timer
import java.util.TimerTask

class TimerViewModel: ViewModel() {

    // property
    private var timer : Timer? = null

    // State
    private val _timerUiState = MutableStateFlow(TimerUiState())
    val timerUiState: StateFlow<TimerUiState> = _timerUiState

    private val _setTimerUiState = MutableStateFlow(SetTimerUiState())
    val setTimerUiState: StateFlow<SetTimerUiState> = _setTimerUiState

    // method of timerUiState
    private fun updateFirstDurationTime(durationTime: DurationTime){
        val currentState = _timerUiState.value
        _timerUiState.update { currentState.copy(
            firstDurationTime = durationTime
        ) }
    }

    fun updateDurationTime(durationTime: DurationTime){
        val currentState = _timerUiState.value
        _timerUiState.update { currentState.copy(
            durationTime = durationTime
        ) }
    }

    private fun oneSecondPassDurationTime(){
        val currentDurationTime = _timerUiState.value.durationTime.copy()
        if(currentDurationTime.second == 0){
            if(currentDurationTime.minute != 0){
                currentDurationTime.second = 59
                currentDurationTime.minute--
            }else{
                if(currentDurationTime.hour != 0){
                    currentDurationTime.second = 59
                    currentDurationTime.minute = 59
                    currentDurationTime.hour--
                }
            }
        }else{
            currentDurationTime.second--
        }
        _timerUiState.update {it.copy(
            durationTime = DurationTime(
                hour = currentDurationTime.hour,
                minute = currentDurationTime.minute,
                second = currentDurationTime.second,
            )
        )
        }
    }

    private fun updateIsTimerStop(){
        val currentState = _timerUiState.value
        _timerUiState.update { currentState.copy(
            isTimerStop = !currentState.isTimerStop
        ) }
    }

    fun updateTimerScreenState(timerScreenState: TimerScreenState){
        val currentState = _timerUiState.value
        _timerUiState.update { currentState.copy(
            currentTimerScreenState = timerScreenState
        ) }
    }

    // method of setTimerUiState
    fun addTimerString(text: String){
        val chars = _setTimerUiState.value.timerString.toCharArray()
        if(chars.size == 0 && (text == "0" || text == "00")) return
        if(chars.size > 5) return
        for(i in text.indices) chars.add(text[i])
        _setTimerUiState.update { _setTimerUiState.value.copy(
            timerString = charArrayToTimerString(chars)
        ) }
    }

    fun popTimerString(){
        val chars = _setTimerUiState.value.timerString.toCharArray()
        if(chars.size == 0) return
        chars.removeLast()
        _setTimerUiState.update { _setTimerUiState.value.copy(
            timerString = charArrayToTimerString(chars)
        ) }
    }

    private fun charArrayToTimerString(chars: MutableList<Char>): TimerString {
        var hourString: String? = null
        var minuteString: String? = null
        var secondString: String? = null

        val reversedChars: MutableList<Char> = chars.reversed().toMutableList()

        for(i in 0 until reversedChars.size){
            if(i > 3){
                hourString = reversedChars[i] + (hourString ?: "")
            }else if(i > 1){
                minuteString = reversedChars[i] + (minuteString ?: "")
            }else{
                secondString = reversedChars[i] + (secondString ?: "")
            }
        }

        return TimerString(hourString, minuteString, secondString)
    }

    fun saveDurationTime(timerString: TimerString) {
        val durationTime = timerString.toDurationTime()
        updateFirstDurationTime(durationTime)
        updateDurationTime(durationTime)
    }

    // method of timer
    fun pauseTimer(){
        updateIsTimerStop()
        timer?.cancel()
        timer = null
    }

    fun startTimer(){
        updateIsTimerStop()
        timer = Timer()
        timer?.scheduleAtFixedRate( object : TimerTask() {
            override fun run() {
                oneSecondPassDurationTime()
            }
        }, 0, 1000)
    }

}

data class TimerUiState(
    val durationTime: DurationTime = DurationTime(second = 0),
    val firstDurationTime: DurationTime = DurationTime(second = 0),
    val isTimerStop: Boolean = true,
    val currentTimerScreenState: TimerScreenState = TimerScreenState.Timer
)

data class SetTimerUiState(
    val timerString: TimerString = TimerString()
)

