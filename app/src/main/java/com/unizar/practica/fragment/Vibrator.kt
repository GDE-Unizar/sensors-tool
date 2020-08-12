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
    private val vibconfig = longArrayOf(50, 50)

    override fun onCreate() {
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        cntx.swt_vib.onCheckedChange {
            vibrator.run { if (it) vibrate(vibconfig, 0) else cancel() }
        }

        cntx.skb_on.onProgressChange { value -> updateProgressBar(0, value) }
        cntx.skb_off.onProgressChange { value -> updateProgressBar(1, value) }

        updateVibrationUI()

    }

    fun updateProgressBar(id: Int, progress: Int) {
        vibconfig[id] = (progress + 1).toLong()
        updateVibrationUI()
    }

    private fun updateVibrationUI() {
        cntx.swt_vib.text = "off=${vibconfig[0]} - on=${vibconfig[1]}"
    }

    override fun onResume() {}

    override fun onPause() {
        cntx.swt_vib.isChecked = false
    }
}

