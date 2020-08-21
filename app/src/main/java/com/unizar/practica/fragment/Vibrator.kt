package com.unizar.practica.fragment

import android.content.Context
import android.os.Vibrator
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.onCheckedChange
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Makes the device vibrate
 */
class Vibrator(
        val cntx: MainActivity,
) : Fragment {

    // variables
    private lateinit var vibrator: Vibrator

    private val vibconfig = longArrayOf(0, Integer.MAX_VALUE.toLong())

    /**
     * Initialization
     */
    override fun onCreate() {
        // service
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // toggle to start/stop
        cntx.vib_tog.onCheckedChange { setState(it) }
    }

    /**
     * Update ui (text)
     */
    private fun setState(state: Boolean) {

        // start/stop
        if (state) vibrator.vibrate(vibconfig, 0)
        else vibrator.cancel()
    }


    /**
     * On pause, stop
     */
    override fun onPause() {
        cntx.vib_tog.isChecked = false
    }
}

