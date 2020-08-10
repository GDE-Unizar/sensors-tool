package com.unizar.practica

import android.app.Activity
import android.os.Bundle

const val SAMPLES = 100

class MainActivity : Activity() {

    val accelerometer = Accelerometer(this)

    val vibrator = Vibrator(this)

    val microphone = Microphone(this)


    override fun onResume() {
        super.onResume()
        accelerometer.onResume()
    }

    override fun onPause() {
        super.onPause()
        accelerometer.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        accelerometer.onCreate()
        vibrator.onCreate()
        microphone.onCreate()
    }


}


