package com.example.simplecomposetimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simplecomposetimer.TimerApplication
import com.example.simplecomposetimer.alarm.AlarmScheduler
import com.example.simplecomposetimer.alarm.toAlarmItem
import com.example.simplecomposetimer.data.DurationTime
import com.example.simplecomposetimer.data.TimerItem
import com.example.simplecomposetimer.data.TimerItemsRepository
import com.example.simplecomposetimer.data.TimerScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.simplecomposetimer.data.TimerString
import com.example.simplecomposetimer.data.toCharArray
import com.example.simplecomposetimer.data.toDurationTime
import com.example.simplecomposetimer.data.toSeconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class TimerViewModel(
    private val timerItemsRepository: TimerItemsRepository,
    private val androidAlarmScheduler: AlarmScheduler
    ): ViewModel() {

    // property
    private var timerMap : MutableMap<Int, Timer> = mutableMapOf()

    // State
    // StateFlow from TimerDatabase
    val timerItemsUiState: StateFlow<TimerItemsUiState> =
        timerItemsRepository.getAllTimerItemsStream().map{ TimerItemsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TimerItemsUiState()
            )

    private val _setTimerUiState = MutableStateFlow(SetTimerUiState())
    val setTimerUiState: StateFlow<SetTimerUiState> = _setTimerUiState

    private val _timerListUiState = MutableStateFlow(TimerListUiState())
    val timerListUiState: StateFlow<TimerListUiState> = _timerListUiState

    private val _currentScreenState = MutableStateFlow(CurrentTimerScreenState())
    val currentScreenState: StateFlow<CurrentTimerScreenState> = _currentScreenState

    // initial process end flag
    private val _initProcessEndState = MutableStateFlow(false)

    // method of database
    fun setTimerListItemUiState(){
        val currentList = _timerListUiState.value.timerItemUiList
        val newList: MutableList<TimerUiState> = mutableListOf()
        val activeIdList: MutableList<Int> = mutableListOf()
        for(i in 0 until timerItemsUiState.value.timerItemList.size){
            val currentElement = timerItemsUiState.value.timerItemList[i]
            if(currentList.find{ it.timerId ==  currentElement.id} == null){
                if(currentElement.timerActive){     // timer is active
                    // Synchronization with current time (get diff)
                    Log.d("setTimerListItemUiState", currentElement.startDate.toString())
                    val diffDurationTime = getDiffDurationTime(
                        DurationTime(
                            currentElement.stopHour,
                            currentElement.stopMinute,
                            currentElement.stopSecond
                        ),
                        Date(currentElement.startDate!!)
                    )
                    newList.add(TimerUiState(
                        timerId = currentElement.id,
                        durationTime = DurationTime(
                            hour = diffDurationTime.hour,
                            minute = diffDurationTime.minute,
                            second = diffDurationTime.second
                        ),
                        firstDurationTime = DurationTime(
                            hour = currentElement.hour,
                            minute = currentElement.minute,
                            second = currentElement.second
                        ),
                        isTimerStop = false
                    ))

                    activeIdList.add(currentElement.id)
                }else{                              // timer is not active
                    newList.add(TimerUiState(
                        timerId = currentElement.id,
                        durationTime = DurationTime(
                            hour = currentElement.stopHour,
                            minute = currentElement.stopMinute,
                            second = currentElement.stopSecond
                        ),
                        firstDurationTime = DurationTime(
                            hour = currentElement.hour,
                            minute = currentElement.minute,
                            second = currentElement.second
                        ),
                        isTimerStop = true
                    ))
                }

            }else{
                val findElement = currentList.find{ it.timerId ==  currentElement.id}
                newList.add(
                    findElement!!.copy(
                        firstDurationTime = DurationTime(
                            hour = currentElement.hour,
                            minute = currentElement.minute,
                            second = currentElement.second
                        )
                    )
                )
            }
        }
        _timerListUiState.update {
            _timerListUiState.value.copy(
                timerItemUiList = newList
            )
        }
        if(activeIdList.isNotEmpty()){
            for(i in 0 until activeIdList.size){
                restartTimer(activeIdList[i])
            }
        }
    }

    suspend fun deleteTimerListItem(id: Int){
        val deleteTarget = timerItemsUiState.value.timerItemList.find { it.id == id }!!
        timerItemsRepository.deleteTimerItem(deleteTarget)
    }

    fun updateDurationTime(durationTime: DurationTime, id: Int){
        val currentIndex = _timerListUiState.value.timerItemUiList.indices.find {
            _timerListUiState.value.timerItemUiList[it].timerId == id
        }!!
        val currentState = _timerListUiState.value.timerItemUiList.find {
            it.timerId == id
        }!!
        _timerListUiState.update {
            it.copy(
                timerItemUiList = createNewMutableList(
                    it.timerItemUiList,
                    currentState.copy(
                        durationTime = durationTime
                    ),
                    currentIndex
                )
            )
        }
    }

    private fun oneSecondPassDurationTime(id: Int){
        val currentState = _timerListUiState.value.timerItemUiList.find {
            it.timerId == id
        }
        val currentDurationTime = currentState!!.durationTime.copy()
        val currentIndex = _timerListUiState.value.timerItemUiList.indices.find {
            _timerListUiState.value.timerItemUiList[it].timerId == id
        }!!
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
        _timerListUiState.update {
            it.copy(
                timerItemUiList = createNewMutableList(
                    it.timerItemUiList,
                    currentState.copy(
                        durationTime = currentDurationTime
                    ),
                    currentIndex
                )
            )
        }
    }

    private fun updateIsTimerStop(id: Int){
        val currentState = _timerListUiState.value.timerItemUiList.find {
            it.timerId == id
        }!!
        val currentIsTimerStop = currentState.isTimerStop
        val currentIndex = _timerListUiState.value.timerItemUiList.indices.find {
            _timerListUiState.value.timerItemUiList[it].timerId == id
        }!!
        _timerListUiState.update {
            it.copy(
                timerItemUiList = createNewMutableList(
                    it.timerItemUiList,
                    currentState.copy(
                        isTimerStop = !currentIsTimerStop
                    ),
                    currentIndex
                )
            )
        }
    }

    fun updateTimerScreenState(timerScreenState: TimerScreenState){
        val currentState = _currentScreenState.value
        _currentScreenState.update { currentState.copy(
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

    suspend fun saveDurationTime(timerString: TimerString) {
        val durationTime = totalSecondsToDurationTime(timerString.toDurationTime().toSeconds())
        timerItemsRepository.insertTimerItem(
            TimerItem(
                name = "Timer",
                hour = durationTime.hour,
                minute = durationTime.minute,
                second = durationTime.second,
                totalSecond = (durationTime.hour * 3600 + durationTime.minute * 60 + durationTime.second).toLong(),
                stopHour = durationTime.hour,
                stopMinute = durationTime.minute,
                stopSecond = durationTime.second,
            )
        )
    }

    // method of timer
    fun pauseTimer(id: Int){
        updateIsTimerStop(id)
        val currentState = _timerListUiState.value.timerItemUiList.find {
            it.timerId == id
        }!!
        androidAlarmScheduler.cancel(currentState.toAlarmItem())
        viewModelScope.launch {
            timerItemsRepository.updateTimerItem(
                TimerItem(
                    id = id,
                    name = "Timer",
                    hour = currentState.firstDurationTime.hour,
                    minute = currentState.firstDurationTime.minute,
                    second = currentState.firstDurationTime.second,
                    totalSecond = (currentState.firstDurationTime.hour * 3600 +
                            currentState.firstDurationTime.minute * 60 +
                            currentState.firstDurationTime.second).toLong(),
                    timerActive = false,
                    startDate = null,
                    stopHour = currentState.durationTime.hour,
                    stopMinute = currentState.durationTime.minute,
                    stopSecond = currentState.durationTime.second,
                )
            )
        }
        timerMap[id]?.cancel()
        timerMap.remove(id)
    }

    fun startTimer(id: Int){
        updateIsTimerStop(id)
        timerMap += id to Timer()
        timerMap[id]?.scheduleAtFixedRate( object : TimerTask() {
            override fun run() {
                oneSecondPassDurationTime(id)
            }
        }, 0, 1000)
        val currentState = _timerListUiState.value.timerItemUiList.find {
            it.timerId == id
        }!!
        androidAlarmScheduler.schedule(currentState.toAlarmItem())
        viewModelScope.launch {
            timerItemsRepository.updateTimerItem(
                TimerItem(
                    id = id,
                    name = "Timer",
                    hour = currentState.firstDurationTime.hour,
                    minute = currentState.firstDurationTime.minute,
                    second = currentState.firstDurationTime.second,
                    totalSecond = (currentState.firstDurationTime.hour * 3600 +
                            currentState.firstDurationTime.minute * 60 +
                            currentState.firstDurationTime.second).toLong(),
                    timerActive = true,
                    startDate = Date().time,
                    stopHour = currentState.durationTime.hour,
                    stopMinute = currentState.durationTime.minute,
                    stopSecond = currentState.durationTime.second,
                )
            )
        }
    }

    private fun restartTimer(id: Int){
        timerMap += id to Timer()
        timerMap[id]?.scheduleAtFixedRate( object : TimerTask() {
            override fun run() {
                oneSecondPassDurationTime(id)
            }
        }, 0, 1000)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TimerApplication)
                TimerViewModel(
                    application.container.timerItemsRepository,
                    application.container.androidAlarmScheduler
                    )
            }
        }
    }

}

data class TimerUiState(
    val timerId: Int = 0,
    val durationTime: DurationTime = DurationTime(second = 0),
    val firstDurationTime: DurationTime = DurationTime(second = 0),
    val isTimerStop: Boolean = true,
)

data class SetTimerUiState(
    val timerString: TimerString = TimerString()
)

// data class for StateFlow from TimerDatabase
data class TimerItemsUiState(
    val timerItemList: List<TimerItem> = listOf()
)

// data class for StateFlow of timer list screen
data class TimerListUiState(
    val timerItemUiList: MutableList<TimerUiState> = mutableListOf()
)

data class CurrentTimerScreenState(
    val currentTimerScreenState: TimerScreenState = TimerScreenState.Timer
)

fun <T> createNewMutableList(currentMutableList: MutableList<T>, value: T, index: Int):
        MutableList<T>{
    val newMutableList: MutableList<T> = mutableListOf()

    for(i in 0 until currentMutableList.size){
        if(i == index){
            newMutableList.add(value)
        }else{
            newMutableList.add(currentMutableList[i])
        }
    }
    return newMutableList
}

fun getDiffDurationTime(currentDurationTime: DurationTime, startDate: Date): DurationTime{
    val diffSeconds = ((Date().time - startDate.time) / 1000).toInt()
    val diffDurationTime = totalSecondsToDurationTime(diffSeconds)
    return currentDurationTime.minus(diffDurationTime)
}

fun totalSecondsToDurationTime(totalSeconds: Int): DurationTime{
    val hour = totalSeconds / 3600
    val minute = (totalSeconds % 3600) / 60
    val second = totalSeconds % 60
    return DurationTime(hour, minute, second)
}


