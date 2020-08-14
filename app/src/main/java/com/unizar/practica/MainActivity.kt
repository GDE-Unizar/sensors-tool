package com.unizar.practica

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.unizar.practica.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val acc by lazy { Accelerometer(this) }
    val mic by lazy { Microphone(this) }
    val spk by lazy { Speaker(this) }
    val vib by lazy { Vibrator(this) }

    val fragments by lazy {
        sequenceOf(
                Triple(acc_head, acc_box, acc),
                Triple(mic_head, mic_box, mic),
                Triple(spk_head, spk_box, spk),
                Triple(vib_head, vib_box, vib)
        )
    }

    val exp by lazy { Experiments(acc, mic, spk, vib, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for ((_, _, fragment) in fragments) fragment.onCreate()
        initHideable()

        exp.onCreate()

        container.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }
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


