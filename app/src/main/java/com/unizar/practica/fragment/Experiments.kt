package com.unizar.practica.fragment

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
        screen { cntx.showAll() }

        sleep(10000)
        file.openNew()
        screen { cntx.spk_toggle.isChecked = true }
        var hz = 10
        while (hz <= 22000) {
            screen { cntx.spk_hz.progress = hz }
            sleep(1000)
            val micVal = mic.getAvgAmplitude()
            val accXVal = acc.serieX.getAverage()
            val accYVal = acc.serieY.getAverage()
            val accZVal = acc.serieZ.getAverage()
            file.writeLine("$hz $micVal $accXVal $accYVal $accZVal")

            hz = (hz + 100).toInt()
        }
        screen { cntx.spk_toggle.isChecked = false }
        screen { file.close() }
        screen { cntx.hideAll() }
    }

    inline fun screen(noinline f: () -> Unit) {
        cntx.runOnUiThread(f)
    }
}