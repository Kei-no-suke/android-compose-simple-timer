package com.example.simplecomposetimer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplecomposetimer.R
import com.example.simplecomposetimer.ui.data.DurationTime
import com.example.simplecomposetimer.ui.data.TimerScreenState
import com.example.simplecomposetimer.ui.data.TimerString

@Composable
fun TimerScreen(
    timerViewModel: TimerViewModel = viewModel()
){
    val timerUiState = timerViewModel.timerUiState.collectAsState()
    val setTimerUiState = timerViewModel.setTimerUiState.collectAsState()
    when(timerUiState.value.currentTimerScreenState){
        TimerScreenState.Timer -> {
            CanvasCard(
                timerUiState = timerUiState,
                updateCurrentDurationTime = { timerViewModel.updateDurationTime(it) },
                onEditButtonClick = { timerViewModel.updateTimerScreenState(TimerScreenState.Edit) },
                pauseTimer = { timerViewModel.pauseTimer() },
                startTimer = { timerViewModel.startTimer() }
            )
        }
        else -> {
            EditCard(
                addTimerString = { timerViewModel.addTimerString(it) },
                popTimerString = { timerViewModel.popTimerString() },
                setTimerUiState = setTimerUiState,
                saveDurationTime = { timerViewModel.saveDurationTime(it) },
                navigateToHome = { timerViewModel.updateTimerScreenState(TimerScreenState.Timer) }
            )
        }
    }

}

@Composable
fun CanvasCard(
    timerUiState: State<TimerUiState>,
    updateCurrentDurationTime: (DurationTime) -> Unit,
    pauseTimer: () -> Unit,
    startTimer: () -> Unit,
    onEditButtonClick: () -> Unit
){

    val durationTimeZeroFlag = (timerUiState.value.durationTime.second == 0) &&
            (timerUiState.value.durationTime.minute == 0) &&
            (timerUiState.value.durationTime.hour == 0)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier
            .height(240.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TimerCircularProgressBar(
                    setDurationTime = timerUiState.value.firstDurationTime,
                    currentDurationTime = timerUiState.value.durationTime,
                    trackColor = Color.LightGray,
                    centerColor = Color(0xFFa7d398),
                    barColor = Color.DarkGray,
                    meterLineOffColor = Color(0xFFa7d398),
                    meterLineOnColor = Color(0xFF00582d)
                )
            }
        }
        Row{
            Button(
                onClick = {
                    updateCurrentDurationTime(timerUiState.value.firstDurationTime)
                },
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_autorenew_24),
                    contentDescription = null
                )
            }
            Button(
                onClick = pauseTimer,
                modifier = Modifier.padding(horizontal = 2.dp),
                enabled = !timerUiState.value.isTimerStop && !durationTimeZeroFlag
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_pause_24),
                    contentDescription = null
                )
            }
            Button(
                onClick = startTimer,
                modifier = Modifier.padding(horizontal = 2.dp),
                enabled = timerUiState.value.isTimerStop && !durationTimeZeroFlag
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                    contentDescription = null
                )
            }
            Button(
                onClick = {
                    onEditButtonClick()
                },
                modifier = Modifier.padding(horizontal = 2.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mode_edit_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun EditCard(
    addTimerString: (String) -> Unit,
    popTimerString: () -> Unit,
    saveDurationTime: (TimerString) -> Unit,
    setTimerUiState: State<SetTimerUiState>,
    navigateToHome: () -> Unit
){
    val hmsTextFontSize = 38.sp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Row{
            if(setTimerUiState.value.timerString.hourString != null){
                Text(
                    text = if(setTimerUiState.value.timerString.hourString!!.length == 1)
                    { "0${setTimerUiState.value.timerString.hourString!!}h" }
                    else{ "${setTimerUiState.value.timerString.hourString!!}h" },
                    color = Color.Blue,
                    fontSize = hmsTextFontSize
                )
            }else{
                Text(
                    text = "00h",
                    color = Color.Black,
                    fontSize = hmsTextFontSize
                )
            }
            if(setTimerUiState.value.timerString.minuteString != null){
                Text(
                    text = if(setTimerUiState.value.timerString.minuteString!!.length == 1)
                    { "0${setTimerUiState.value.timerString.minuteString!!}m" }
                    else{ "${setTimerUiState.value.timerString.minuteString!!}m" },
                    color = Color.Blue,
                    fontSize = hmsTextFontSize
                )
            }else{
                Text(
                    text = "00m",
                    color = Color.Black,
                    fontSize = hmsTextFontSize
                )
            }
            if(setTimerUiState.value.timerString.secondString != null){
                Text(
                    text = if(setTimerUiState.value.timerString.secondString!!.length == 1)
                    { "0${setTimerUiState.value.timerString.secondString!!}s" }
                    else{ "${setTimerUiState.value.timerString.secondString!!}s" },
                    color = Color.Blue,
                    fontSize = hmsTextFontSize
                )
            }else{
                Text(
                    text = "00s",
                    color = Color.Black,
                    fontSize = hmsTextFontSize
                )
            }
        }
        Row{
            Button(onClick = { addTimerString("7") }) {
                Text(text="7")
            }
            Button(onClick = { addTimerString("8") }) {
                Text(text="8")
            }
            Button(onClick = { addTimerString("9") }) {
                Text(text="9")
            }
        }
        Row{
            Button(onClick = { addTimerString("4") }) {
                Text(text="4")
            }
            Button(onClick = { addTimerString("5") }) {
                Text(text="5")
            }
            Button(onClick = { addTimerString("6") }) {
                Text(text="6")
            }
        }
        Row{
            Button(onClick = { addTimerString("1") }) {
                Text(text="1")
            }
            Button(onClick = { addTimerString("2") }) {
                Text(text="2")
            }
            Button(onClick = { addTimerString("3") }) {
                Text(text="3")
            }
        }
        Row{
            Button(onClick = { addTimerString("00") }) {
                Text(text="00")
            }
            Button(onClick = { addTimerString("0") }) {
                Text(text="0")
            }
            Button(onClick = { popTimerString() }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_backspace_24),
                    contentDescription = null
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_check_circle_24),
            contentDescription = null,
            modifier = Modifier.clickable{
                saveDurationTime(setTimerUiState.value.timerString)
                navigateToHome()
            }.size(60.dp).padding(top = 16.dp),
            tint = Color(0xFF006d4d)
        )
    }
}

