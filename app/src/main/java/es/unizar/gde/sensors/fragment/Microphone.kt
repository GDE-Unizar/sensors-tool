package es.unizar.gde.sensors.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import es.unizar.gde.sensors.MainActivity
import es.unizar.gde.sensors.R
import es.unizar.gde.sensors.tools.FileWriter
import es.unizar.gde.sensors.tools.MODE
import es.unizar.gde.sensors.tools.RangeSerie
import es.unizar.gde.sensors.tools.hasRecordPermission
import es.unizar.gde.sensors.utilities.Fragment
import kotlin.math.abs

const val HZ = 40000//8000

/**
 * Shows input recorded by the microphone
 */
class Microphone(
        val cntx: MainActivity,
) : Fragment {
    private val views = cntx.views

    // utils
    val sm = SoundMeter()
    val micSerie = RangeSerie().apply { hz = HZ.toDouble() }.apply { SMOOTHNESS = 0.75 }
    private val file = FileWriter(cntx, "mic")

    /**
     * Initialize
     */
    override fun onCreate() {
        handler = Handler(Looper.getMainLooper())

        // graph microphone
        views.micGraph.addSeries(micSerie)
        micSerie.initializeGraph(views.micGraph)

        // press rec button to start/stop recording
        views.micRec.onCheckedChange {
            if (it) {
                file.openNew("rec")
                file.writeLine("amp")
            } else {
                file.close()
            }
        }
        views.micSnap.setOnClickListener {
            snapshot()
        }

        // set mode
        views.micMode.setOnModeChanged { micSerie.mode = it }

        // clear button
        views.micClr.setOnClickListener { micSerie.clear() }
    }

    fun snapshot() {
        file.openNew("snap", false)
        file.writeLine(when (micSerie.mode) {
            MODE.RAW -> "amp_raw"
            MODE.BASE -> "amp_base"
            MODE.AVERAGE -> "amp_avg"
            MODE.RANGE -> "amp_rang"
            MODE.BUFFER -> "buff_x buff_y"
            MODE.FFT -> "hz buff_val"
        })

        micSerie.getValues(micSerie.lowestValueX, micSerie.highestValueX).forEach {
            var line = ""
            if (micSerie.mode.isBuff()) {
                line += it.x.toString() + " "
            }
            line += it.y.toString()
            file.writeLine(line)
        }
        file.close()
    }

    /**
     * Gets the new data and plots it
     */
    fun updateSound() {
        val amp: Double
        if (micSerie.mode.isBuff()) {
            // plot buffer
            micSerie.replaceData(sm.getFullBuffer())
            amp = micSerie.average
        } else {
            // add amplitude
            amp = sm.getAmplitude()
            micSerie.addData(amp)
        }

        // update graph
        views.micGraph.onDataChanged(false, false)
        views.micGraph.viewport.setMaxY(micSerie.maxY + 1.0)
        views.micGraph.viewport.setMinY(micSerie.minY - 1.0)
        micSerie.labelVerticalWidth = views.micGraph.gridLabelRenderer.labelVerticalWidth * 2
        views.micTxt.text = cntx.getString(R.string.amp_desc, amp)
        file.writeLine(amp)
    }

    /**
     * Gets the current average value
     */
    val average: Double
        get() {
            return micSerie.average
        }

    // ---- updater ---------

    // thread objects
    lateinit var handler: Handler
    val updateTask = object : Runnable {
        override fun run() {
            updateSound()
            handler.postDelayed(this, 0)
        }
    }

    /**
     * When resumed, start recording
     */
    override fun onResume() {
        sm.start()
        handler.post(updateTask)
    }

    /**
     * When paused, stop recording
     */
    override fun onPause() {
        handler.removeCallbacks(updateTask)
        sm.stop()
    }
}

/**
 * Record audio and shows its properties
 */
class SoundMeter {

    private val FORMAT = AudioFormat.CHANNEL_IN_MONO

    private val ENCODING = AudioFormat.ENCODING_PCM_16BIT

    // properties
    var minSize = AudioRecord.getMinBufferSize(HZ, FORMAT, ENCODING).apply { Log.d("MINSIZE", this.toString()) }

    // utils
    val ar: AudioRecord by lazy { AudioRecord(MediaRecorder.AudioSource.MIC, HZ, FORMAT, ENCODING, minSize) }

    /**
     * Starts recording
     */
    fun start() {
        if (!hasRecordPermission) return

        ar.startRecording()
    }

    /**
     * Stops recording
     */
    fun stop() {
        if (!hasRecordPermission) return

        ar.stop()
    }

    /**
     * Returns the amplitude
     */
    fun getAmplitude() = getFullBuffer().map { abs(it) }.maxOrNull() ?: 0.0

    /**
     * Returns the full buffer
     */
    fun getFullBuffer(): DoubleArray {
        if (!hasRecordPermission) return DoubleArray(0)

        val buffer = ShortArray(minSize)
        ar.read(buffer, 0, minSize)
        return buffer.map { it.toDouble() }.toDoubleArray()
    }
}