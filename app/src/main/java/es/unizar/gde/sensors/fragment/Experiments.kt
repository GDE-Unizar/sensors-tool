package es.unizar.gde.sensors.fragment

import android.widget.Button
import android.widget.Toast
import es.unizar.gde.sensors.MainActivity
import es.unizar.gde.sensors.R
import es.unizar.gde.sensors.helpOnLongTap
import es.unizar.gde.sensors.tools.FileWriter
import es.unizar.gde.sensors.tools.Volume
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
    private val views = cntx.views

    // utils
    val file = FileWriter(cntx, "exp")
    val experiments = sequenceOf(
        cntx.getString(R.string.exp_freq) to ::freqExperiment,
        cntx.getString(R.string.exp_vol) to ::volumeExperiment,
    )

    // variables
    var current: Thread? = null
        set(value) {
            field?.interrupt()
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

        // add a button for each experiment
        experiments.forEach { (name, function) ->
            createButton {
                text = cntx.getString(R.string.exp_title, name)
                setOnClickListener {
                    current = thread {
                        screen { cntx.hideAll() }
                        try {
                            // wait before
                            screen { Toast.makeText(cntx, R.string.exp_start, Toast.LENGTH_SHORT).show() }
                            sleep(5_000)
                            // initialize
                            screen { cntx.showAll() }
                            screen { file.openNew(name) }
                            // run
                            function()
                        } catch (ignored: InterruptedException) {
                        }
                        // finalize
                        screen { file.close() }
                        screen { cntx.hideAll() }
                    }
                }
                tag = "exp_$name"
                btn_exps.add(this)
            }
        }

        // add stop button
        btn_stop = createButton {
            text = context.getString(R.string.exp_stop)
            isEnabled = false
            setOnClickListener { stop() }
            tag = "exp_stop"
        }


        cntx.helpOnLongTap(*btn_exps.toTypedArray(), btn_stop)

    }

    /**
     * Creates a new button in the layout. Includes callback as 'apply'
     */
    private fun createButton(callback: Button.() -> Unit) =
        (cntx.layoutInflater.inflate(R.layout.experiment_button, null) as Button).apply {
            callback()
            views.main.addView(this)
        }

    /**
     * @return true is there is an experiment running
     */
    fun isRunning() = current != null

    /**
     * Stops the current experiment (or does nothing if none active)
     */
    fun stop() {
        current = null
    }

    /**
     * An experiment
     */
    fun freqExperiment() {
        // initialize
        screen { views.spkToggle.isChecked = true }
        file.writeLine("hz mic_amp acc_x_raw acc_y_raw acc_z_raw")

        // for each frequency
        for (hz in 10..22010 step 100) {

            // play requency and wait
            screen { views.spkHz.progress = hz }
            screen { views.micClr.performClick() }
            screen { views.accClr.performClick() }
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
    fun volumeExperiment() {
        // initialize
        file.writeLine("hz vol mic_amp acc_x_raw acc_y_raw acc_z_raw")
        val volume = Volume(cntx)
        screen { views.spkToggle.isChecked = true }

        // perform
        for (hz in sequenceOf(100, 1000, 10000)) {
            screen { views.spkHz.progress = hz }

            for (vol in 0..volume.maxVolume) {
                screen { volume.setVolume(vol) }
                screen { views.micClr.performClick() }
                sleep(1000)
                val micVal = mic.average
                val accXVal = acc.serieX.average
                val accYVal = acc.serieY.average
                val accZVal = acc.serieZ.average
                file.writeLine("$hz $vol $micVal $accXVal $accYVal $accZVal")
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