package com.unizar.practica

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.unizar.practica.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity
 */
class MainActivity : Activity() {

    // fragments
    val acc by lazy { Accelerometer(this) }
    val mic by lazy { Microphone(this) }
    val spk by lazy { Speaker(this) }
    val vib by lazy { Vibrator(this) }

    val exp by lazy { Experiments(acc, mic, spk, vib, this) }

    val fragments by lazy {
        sequenceOf(
                Triple(acc_head, acc_box, acc),
                Triple(mic_head, mic_box, mic),
                Triple(spk_head, spk_box, spk),
                Triple(vib_head, vib_box, vib)
        )
    }

    /**
     * When the activity is created
     */
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

    /**
     * The activity is resumed
     */
    override fun onResume() {
        super.onResume()

        // resume visible fragments
        for ((_, box, fragment) in fragments)
            if (box.visibility == View.VISIBLE)
                fragment.onResume()
    }

    /**
     * The activity is paused
     */
    override fun onPause() {
        super.onPause()

        // pause all fragments
        for ((_, _, fragment) in fragments) fragment.onPause()
    }

    /**
     * Initialize the Hideable parts
     */
    private fun initHideable() {
        for ((head, box, fragment) in fragments) {

            // click header to toggle
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

            // start hided
            box.visibility = View.GONE
        }
    }

}


