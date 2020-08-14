package com.unizar.practica.fragment

import android.content.Context
import android.os.Vibrator
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.onCheckedChange
import com.unizar.practica.tools.onProgressChange
import kotlinx.android.synthetic.main.activity_main.*

class Vibrator(
        val cntx: MainActivity
) : Fragment {

    private lateinit var vibrator: Vibrator
    private val vibconfig = longArrayOf(0, 0)

    override fun onCreate() {
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        cntx.vib_tog.onCheckedChange {
            vibrator.run { if (it) vibrate(vibconfig, 0) else cancel() }
        }

        cntx.vib_on.run {
            onProgressChange { value -> updateProgressBar(0, value) }
            progress = max / 2
        }
        cntx.vib_off.run {
            onProgressChange { value -> updateProgressBar(1, value) }
            progress = max / 2
        }


        updateUI()

    }

    fun updateProgressBar(id: Int, progress: Int) {
        vibconfig[id] = (progress + 1).toLong()
        updateUI()
    }

    private fun updateUI() {
        cntx.vib_tog.text = "off=${vibconfig[0]} - on=${vibconfig[1]}"
    }

    override fun onPause() {
        cntx.vib_tog.isChecked = false
    }
}

