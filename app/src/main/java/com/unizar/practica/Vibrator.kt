package com.unizar.practica

import android.content.Context
import android.os.Vibrator
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class Vibrator(
        val cntx: MainActivity
) {

    private lateinit var vibrator: Vibrator
    private val vibconfig = longArrayOf(50, 50)

    fun onCreate() {
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        cntx.swt_vib.setOnCheckedChangeListener { _, c -> toggleVibration(c) }

        cntx.skb_on.setOnProgressChangedListener { value -> updateProgressBar(0, value) }
        cntx.skb_off.setOnProgressChangedListener { value -> updateProgressBar(1, value) }

        updateVibrationUI()

    }


    fun updateProgressBar(id: Int, progress: Int) {
        vibconfig[id] = (progress + 1).toLong()
        updateVibrationUI()
    }

    fun toggleVibration(state: Boolean) = if (state) vibrator.vibrate(vibconfig, 0) else vibrator.cancel()


    private fun updateVibrationUI() {
        cntx.swt_vib.text = "off=${vibconfig[0]} - on=${vibconfig[1]}"
    }
}

inline fun SeekBar.setOnProgressChangedListener(crossinline listener: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            listener(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    })
}