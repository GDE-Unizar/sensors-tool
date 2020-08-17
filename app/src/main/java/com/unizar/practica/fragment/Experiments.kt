package com.unizar.practica.fragment

import android.view.View
import android.widget.Button
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class Experiments(
        val acc: Accelerometer,
        val mic: Microphone,
        val spk: Speaker,
        val vib: Vibrator,
        val cntx: MainActivity,
) {

    val file = FileWriter(cntx, "exp")

    fun onCreate() {
        Button(cntx).apply {
            text = "experiment"
            setOnClickListener { thread { experiment() } }
            cntx.main.addView(this)
        }
    }

    fun experiment() {
        if (cntx.mic_box.visibility == View.GONE) screen { cntx.mic_head.performClick() }
        if (cntx.spk_box.visibility == View.GONE) screen { cntx.spk_head.performClick() }
        if (cntx.acc_box.visibility == View.GONE) screen { cntx.acc_head.performClick() }

        file.openNew()
        sleep(1000)
        screen { cntx.spk_toggle.isChecked = true }
        for (hz in 10..2000 step 10) {
            screen { cntx.spk_hz.progress = hz }
            sleep(1000)
            val micVal = mic.getAvgAmplitude()
            val accXVal = acc.serieX.getAverage()
            val accYVal = acc.serieY.getAverage()
            val accZVal = acc.serieZ.getAverage()
            file.writeLine("$hz $micVal $accXVal $accYVal $accZVal")
        }
        screen { cntx.spk_toggle.isChecked = false }
        screen { file.close() }
    }

    inline fun screen(noinline f: () -> Unit) {
        cntx.runOnUiThread(f)
    }
}