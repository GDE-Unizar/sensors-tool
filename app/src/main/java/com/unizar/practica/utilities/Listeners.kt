package com.unizar.practica.utilities

import android.widget.CompoundButton
import android.widget.SeekBar

/**
 * Wrapper for the setOnSeekBarChangeListener#onProgressChanged
 */
inline fun SeekBar.onProgressChange(crossinline listener: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            listener(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    })
}

/**
 * Wrapper for the setOnCheckedChangeListener
 */
inline fun CompoundButton.onCheckedChange(crossinline listener: (Boolean) -> Unit) {
    this.setOnCheckedChangeListener { _, checked -> listener(checked) }
}