package com.example.simplecomposetimer.alarm

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.simplecomposetimer.ui.theme.SimpleComposeTimerTheme

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vibrator = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        }else{
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val vibrationEffect =
            VibrationEffect.createWaveform(
                longArrayOf(100, 100, 100,
                    100, 500, 100,
                    100, 100, 100),
                intArrayOf(
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE, 0,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                    VibrationEffect.DEFAULT_AMPLITUDE
                ),
                0
            )
        vibrator.vibrate(vibrationEffect)
        setContent {
            SimpleComposeTimerTheme {
                Column {
                    Text("Notification")
                    Button(onClick = {
                        vibrator.cancel()
                        finish()
                    }) {
                        Text("停止")
                    }
                }
            }
        }
    }
}