package com.example.simplecomposetimer.data

data class DurationTime(
    var hour: Int = 0,
    var minute: Int = 0,
    var second: Int = 0
)

fun DurationTime.toSeconds(): Int{
    var seconds: Int = 0
    seconds += this.hour * 3600
    seconds += this.minute * 60
    seconds += this.second

    return seconds
}

fun DurationTime.equal(durationTime: DurationTime): Boolean{
    return this.hour == durationTime.hour &&
            this.minute == durationTime.minute &&
            this.second == durationTime.second
}

fun formatDuration(durationTime: DurationTime): String{
    val hour = durationTime.hour
    val minute = durationTime.minute
    val second = durationTime.second

    var hourText: String?
    var minuteText: String?
    var secondText: String?
    var hmsText: String

    if(hour == 0 && minute == 0){
        hourText = null
        minuteText = null
        secondText = second.toString()
        hmsText = secondText
    }else if(hour == 0){
        hourText = null
        minuteText = minute.toString()
        secondText = if(second < 10){ "0$second" }else{ second.toString() }
        hmsText = "${minuteText}:${secondText}"
    }else {
        hourText = hour.toString()
        minuteText = if(minute < 10){ "0$minute" }else{ minute.toString() }
        secondText = if(second < 10){ "0$second" }else{ second.toString() }
        hmsText = "${hourText}:${minuteText}:${secondText}"
    }

    return hmsText
}

