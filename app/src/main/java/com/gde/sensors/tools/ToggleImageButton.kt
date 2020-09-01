package com.gde.sensors.tools

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import com.gde.sensors.R

/**
 * There is ToggleButton, and ImageButton, but no ToggleImageButton.
 * An ImageButton with a checked state.
 * Hardcoded images
 */
class ToggleImageButton(context: Context?, attrs: AttributeSet?) : ImageButton(context, attrs) {

    private var listener: ((Boolean) -> Unit)? = null

    /**
     * The checked state. Getter and setter
     */
    var isChecked = false
        set(value) {
            field = value
            update()
        }

    /**
     * Initialization
     */
    init {
        setOnClickListener { isChecked = !isChecked }
        update()
    }

    /**
     * Set listener for checked change
     */
    fun onCheckedChange(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    /**
     * Internal update
     */
    private fun update() {
        setImageResource(if (isChecked) R.drawable.ic_stop else R.drawable.ic_record)
        listener?.invoke(isChecked)
    }
}