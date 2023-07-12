package com.example.simplecomposetimer.data

data class TimerString(
    val hourString: String? = null,
    val minuteString: String? = null,
    val secondString: String? = null
)

fun TimerString.toOneString():String{
    var oneString = ""
    if(hourString != null){
        oneString = "${oneString}${hourString}"
    }
    if(minuteString != null){
        oneString = "${oneString}${minuteString}"
    }
    if(secondString != null){
        oneString = "${oneString}${secondString}"
    }
    return oneString
}

fun TimerString.toCharArray():MutableList<Char>{
    val oneString = this.toOneString()
    val chars: MutableList<Char> = mutableListOf()
    for(i in oneString.indices) chars.add(oneString[i])
    return chars
}

fun TimerString.toDurationTime(): DurationTime {
    val hour = hourString?.toIntOrNull() ?: 0
    val minute = minuteString?.toIntOrNull() ?: 0
    val second = secondString?.toIntOrNull() ?: 0
    return DurationTime(hour, minute, second)
}
