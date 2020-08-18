package com.unizar.practica.fragment

import android.content.Context
import android.os.Vibrator
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.onCheckedChange
import com.unizar.practica.tools.onProgressChange
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Makes the device vibrate
 */
class Vibrator(
        val cntx: MainActivity
) : Fragment {

    // variables
    private lateinit var vibrator: Vibrator
    private val vibconfig = longArrayOf(0, 0)

    /**
     * Initialization
     */
    override fun onCreate() {
        // service
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // toggle to start/stop
        cntx.vib_tog.onCheckedChange {
            vibrator.run { if (it) vibrate(vibconfig, 0) else cancel() }
        }

        // on time
        cntx.vib_on.run {
            onProgressChange { value -> updateProgressBar(0, value) }
            progress = max / 2
        }

        // off time
        cntx.vib_off.run {
            onProgressChange { value -> updateProgressBar(1, value) }
            progress = max / 2
        }

        // init update
        updateUI()
    }

    /**
     * When a progress bar changes, update the array value
     */
    fun updateProgressBar(id: Int, progress: Int) {
        vibconfig[id] = (progress + 1).toLong()
        updateUI()
    }

    /**
     * Update ui (text)
     */
    private fun updateUI() {
        cntx.vib_tog.text = "off=${vibconfig[0]} - on=${vibconfig[1]}"
    }

    /**
     * On pause, stop
     */
    override fun onPause() {
        cntx.vib_tog.isChecked = false
    }
}

