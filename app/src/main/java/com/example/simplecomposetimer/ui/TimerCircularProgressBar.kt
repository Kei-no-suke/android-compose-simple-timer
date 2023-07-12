package com.example.simplecomposetimer.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecomposetimer.ui.data.DurationTime
import com.example.simplecomposetimer.ui.data.formatDuration
import com.example.simplecomposetimer.ui.data.toSeconds
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// startAngle of drawArc(0 position of the second hand of a clock)
const val TOP_ANGLE = 270f
// number of lines in the meter
const val METER_LINE_NUM = 100

// circular progress bar for timer
@Composable
fun TimerCircularProgressBar(
    // initial duration time
    setDurationTime: DurationTime,
    // current duration time
    currentDurationTime: DurationTime,
    // size value
    height: Float = 600f,
    arcWidth: Float = 30f,
    lineLength: Float = 30f,
    gap: Float = 2f,
    // color
    trackColor: Color,
    centerColor: Color,
    barColor: Color,
    meterLineOffColor: Color,
    meterLineOnColor: Color
){

    Canvas(modifier = Modifier.fillMaxSize()){

        val width = size.width
        val maxValue: Int = setDurationTime.toSeconds()
        val minValue: Int = 0
        val currentValue = currentDurationTime.toSeconds()

        val innerRadius = (height - (arcWidth * 4 + lineLength + gap)) / 2f
        val outerRadius = innerRadius + arcWidth / 2f + gap + lineLength
        val innerDiameter = innerRadius * 2f
        val centerX = width/2f
        val centerY = height/2f

        val hmsText = formatDuration(currentDurationTime)

        drawCircle(
            color = trackColor,
            radius = innerRadius + arcWidth / 2f,
            center = Offset(x = centerX, y = centerY)
        )

        drawCircle(
            color = centerColor,
            radius = innerRadius - arcWidth / 2f,
            center = Offset(x = centerX, y = centerY)
        )

        drawArc(
            color = barColor,
            startAngle = TOP_ANGLE,
            sweepAngle = 360f * currentValue.toFloat() / (maxValue - minValue).toFloat(),
            style = Stroke(
                width = arcWidth,
                cap = StrokeCap.Round
            ),
            useCenter = false,
            size = Size(
                width = innerDiameter,
                height = innerDiameter
            ),
            topLeft = Offset(
                (width - innerRadius * 2f)/2f,
                (height - innerRadius * 2f)/2f
            )
        )

        for(i in 0 until METER_LINE_NUM){
            val angleInRad = (-i.toFloat() * 2f * PI / METER_LINE_NUM + PI / 2f)

            val start = Offset(
                x = (centerX + (innerRadius + arcWidth / 2f + gap ) * cos(angleInRad)).toFloat(),
                y = (centerY + (innerRadius + arcWidth / 2f + gap ) * -sin(angleInRad)).toFloat()
            )

            val end = Offset(
                x = (centerX + outerRadius * cos(angleInRad)).toFloat(),
                y = (centerY + outerRadius * -sin(angleInRad)).toFloat()
            )

            drawLine(
                color = if(i * (maxValue - minValue) / METER_LINE_NUM <= currentValue) meterLineOnColor else meterLineOffColor,
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx()
            )
        }

        drawContext.canvas.nativeCanvas.apply {
            drawIntoCanvas {
                drawText(
                    hmsText,
                    centerX,
                    centerY + 35.sp.toPx() / 3f,
                    Paint().apply {
                        textSize = 35.sp.toPx()
                        textAlign = Paint.Align.CENTER
                        color = Color(0xFF515254).toArgb()
                        isFakeBoldText = true
                    }
                )
            }
        }
    }
}