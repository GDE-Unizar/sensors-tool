package com.unizar.practica.fragment

import android.widget.Button
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * Manages the experiments
 */
class Experiments(
        val acc: Accelerometer,
        val mic: Microphone,
        val spk: Speaker,
        val vib: Vibrator,
        val cntx: MainActivity,
) {

    // utils
    val file = FileWriter(cntx, "exp")

    /**
     * When created
     */
    fun onCreate() {
        // add a button with experiment 1
        Button(cntx).apply {
            text = "experiment"
            setOnClickListener { thread { experiment() } }
            cntx.main.addView(this)
        }
    }

    /**
     * An experiment
     */
    fun experiment() {
        // wait first
        sleep(10000)

        // initialize
        screen { cntx.showAll() }
        file.openNew()
        screen { cntx.spk_toggle.isChecked = true }

        // for each frequency
        for (hz in 10..22000 step 100) {

            // play requency and wait
            screen { cntx.spk_hz.progress = hz }
            sleep(1000)

            // record values
            val micVal = mic.average
            val accXVal = acc.serieX.average
            val accYVal = acc.serieY.average
            val accZVal = acc.serieZ.average
            file.writeLine("$hz $micVal $accXVal $accYVal $accZVal")
        }
        screen { cntx.spk_toggle.isChecked = false }
        screen { file.close() }
        screen { cntx.hideAll() }
    }

    inline fun screen(noinline f: () -> Unit) {
        cntx.runOnUiThread(f)
    }
}