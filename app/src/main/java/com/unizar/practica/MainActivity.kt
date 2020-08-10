package com.unizar.practica

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.unizar.practica.fragment.Accelerometer
import com.unizar.practica.fragment.Microphone
import com.unizar.practica.fragment.Speaker
import com.unizar.practica.fragment.Vibrator
import kotlinx.android.synthetic.main.activity_main.*

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

        initHideable()
    }

    override fun onResume() {
        super.onResume()
        accelerometer.onResume()
    }

    override fun onPause() {
        super.onPause()
        accelerometer.onPause()
    }

    private fun initHideable() {
        for ((head, box) in mapOf(
                Pair(vib_head, vib_box),
                Pair(mic_head, mic_box),
                Pair(acc_head, acc_box),
                Pair(spk_head, spk_box),
        )) {
            head.setOnClickListener {
                box.visibility = if (box.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
            }
        }
    }

}


