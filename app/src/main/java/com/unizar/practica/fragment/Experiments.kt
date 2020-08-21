package com.unizar.practica.fragment

import android.widget.Button
import android.widget.Toast
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
            "Frequency" to ::freqGraph,
            "Volume" to ::volumeGraph,
    )

    // variables
    var current: Thread? = null
        set(value) {
            field = value
            btn_stop.isEnabled = value != null
            btn_exps.forEach { it.isEnabled = value == null }
        }
    lateinit var btn_stop: Button
    val btn_exps = ArrayList<Button>()

    /**
     * When created
     */
    fun onCreate() {

        // add a button foreach experiment
        experiments.forEach { (name, function) ->
            Button(cntx).apply {
                text = "Experiment:\n$name"
                setOnClickListener {
                    current?.interrupt()
                    current = thread {
                        screen { cntx.hideAll() }
                        try {
                            // wait before
                            screen { Toast.makeText(cntx, "Initiating experiment in 5 seconds...", Toast.LENGTH_SHORT).show() }
                            sleep(5_000)
                            // initialize
                            screen { cntx.showAll() }
                            file.openNew(name)
                            // run
                            function()
                        } catch (ignored: InterruptedException) {
                        }
                        // finalize
                        screen { file.close() }
                        screen { cntx.hideAll() }
                    }
                }
                cntx.main.addView(this)
                btn_exps.add(this)
            }
        }

        // add stop button
        btn_stop = Button(cntx).apply {
            text = "Stop experiment"
            isEnabled = false
            setOnClickListener {
                current?.interrupt()
                current = null
            }
            cntx.main.addView(this)
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

    /**
     * Another experiment
     */
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