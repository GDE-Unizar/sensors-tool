package com.unizar.practica.tools

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button

/**
 * A button that auto-clicks itself repeatedly when log tapped
 */
@SuppressLint("ClickableViewAccessibility")
class RepeatButton(context: Context?, attrs: AttributeSet?) : Button(context, attrs) {

    /**
     * Delay between repeated clicks
     */
    private val DELAY = 100L

    init {
        setOnLongClickListener {
            // start
            handler.post(action)
        }
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                // stop
                handler.removeCallbacks(action)
            }
            false
        }
    }

    private val action: Runnable = object : Runnable {
        override fun run() {
            // repeat
            performClick()
            handler.postDelayed(this, DELAY)
        }

    }

}