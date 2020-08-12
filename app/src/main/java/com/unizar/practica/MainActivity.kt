package com.unizar.practica

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.unizar.practica.fragment.Accelerometer
import com.unizar.practica.fragment.Microphone
import com.unizar.practica.fragment.Speaker
import com.unizar.practica.fragment.Vibrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val fragments by lazy {
        sequenceOf(
                Triple(vib_head, vib_box, Vibrator(this)),
                Triple(mic_head, mic_box, Microphone(this)),
                Triple(acc_head, acc_box, Accelerometer(this)),
                Triple(spk_head, spk_box, Speaker(this)),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for ((_, _, fragment) in fragments) fragment.onCreate()
        initHideable()
    }

    override fun onResume() {
        super.onResume()

        for ((_, box, fragment) in fragments)
            if (box.visibility == View.VISIBLE)
                fragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        for ((_, _, fragment) in fragments) fragment.onPause()
    }

    private fun initHideable() {
        for ((head, box, fragment) in fragments) {
            head.setOnClickListener {
                box.visibility = if (box.visibility == View.VISIBLE) {
                    // hide
                    fragment.onPause()
                    View.GONE
                } else {
                    // show
                    fragment.onResume()
                    View.VISIBLE
                }
            }
            box.visibility = View.GONE
        }
    }

}


