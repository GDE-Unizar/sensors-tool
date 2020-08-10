package com.unizar.practica

import android.app.Activity
import android.os.Bundle
import com.unizar.practica.fragment.Accelerometer
import com.unizar.practica.fragment.Microphone
import com.unizar.practica.fragment.Speaker
import com.unizar.practica.fragment.Vibrator

const val SAMPLES = 100

class MainActivity : Activity() {

    val accelerometer = Accelerometer(this)
    val vibrator = Vibrator(this)
    val microphone = Microphone(this)
    val speaker = Speaker(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        accelerometer.onCreate()
        vibrator.onCreate()
        microphone.onCreate()
        speaker.onCreate()
    }

    override fun onResume() {
        super.onResume()
        accelerometer.onResume()
    }

    override fun onPause() {
        super.onPause()
        accelerometer.onPause()
    }


}


