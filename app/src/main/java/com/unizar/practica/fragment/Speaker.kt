package com.unizar.practica.fragment

import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Tune
import com.unizar.practica.tools.setOnProgressChangedListener
import kotlinx.android.synthetic.main.activity_main.*

class Speaker(
        val cntx: MainActivity
) {

    val tune = Tune()

    fun onCreate() {
        cntx.spk_toggle.setOnCheckedChangeListener { _, checked -> setState(checked) }

        cntx.spk_progress.setOnProgressChangedListener {
            tune.freq = it.toDouble()
            cntx.spk_toggle.text = "$it Hz"
        }
        cntx.spk_progress.progress = 440
    }

    private fun setState(enabled: Boolean) {
        if (enabled) {
            tune.start()
        } else {
            tune.stop()
        }
    }

}