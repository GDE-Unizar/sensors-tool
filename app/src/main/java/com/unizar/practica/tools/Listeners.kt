package com.unizar.practica.tools

import android.widget.SeekBar
import android.widget.Switch

inline fun SeekBar.onProgressChange(crossinline listener: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            listener(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    })
}


inline fun Switch.onCheckedChange(crossinline listener: (Boolean) -> Unit) {
    this.setOnCheckedChangeListener { _, checked -> listener(checked) }
}