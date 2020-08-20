package com.unizar.practica.fragment

import android.widget.Button
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import com.unizar.practica.tools.Volume
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

    val experiments = sequenceOf(
            Pair("Frequency", ::freqGraph),
            Pair("Volume", ::volumeGraph),
    )

    /**
     * When created
     */
    fun onCreate() {
        // add a button foreach experiment
        experiments.forEach { (name, function) ->
            Button(cntx).apply {
                text = "Experiment:\n$name"
                setOnClickListener {
                    thread {
                        // wait before
                        sleep(10000)
                        // initialize
                        screen { cntx.showAll() }
                        file.openNew(name)
                        // run after
                        function()
                        // finalize
                        screen { file.close() }
                        screen { cntx.hideAll() }
                    }
                }
                cntx.main.addView(this)
            }
        }
    }

    /**
     * An experiment
     */
    fun freqGraph() {
        // initialize
        screen { cntx.spk_toggle.isChecked = true }

        // for each frequency
        for (hz in 10..22000 step 100) {

            // play requency and wait
            screen { cntx.spk_hz.progress = hz }
            screen { cntx.mic_clr.performClick() }
            screen { cntx.acc_clr.performClick() }
            sleep(1000)

            // record values
            val micVal = mic.average
            val accXVal = acc.serieX.average
            val accYVal = acc.serieY.average
            val accZVal = acc.serieZ.average
            file.writeLine("$hz $micVal $accXVal $accYVal $accZVal")
        }
    }

    fun volumeGraph() {
        val volume = Volume(cntx)
        screen { cntx.spk_toggle.isChecked = true }

        for (hz in sequenceOf(100, 1000, 10000)) {
            screen { cntx.spk_hz.progress = hz }

            for (vol in 0..volume.maxVolume) {
                screen { volume.setVolume(vol) }
                screen { cntx.mic_clr.performClick() }
                sleep(1000)
                val micVal = mic.average
                file.writeLine("$hz $vol $micVal")
            }
        }
    }

    /**
     * Context#runOnUiThread wrapper
     */
    inline fun screen(noinline f: () -> Unit) {
        cntx.runOnUiThread(f)
    }
}