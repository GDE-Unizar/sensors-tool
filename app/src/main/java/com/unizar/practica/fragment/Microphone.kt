package com.unizar.practica.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

const val HZ = 40000//8000

/**
 * Shows input recorded by the microphone
 */
class Microphone(
        val cntx: MainActivity,
) : Fragment {

    // utils
    val sm = SoundMeter()
    val micSerie = RangeSerie()
    private val file = FileWriter(cntx, "mic")

    /**
     * Initialize
     */
    override fun onCreate() {
        handler = Handler(Looper.getMainLooper())

        // graph microphone
        cntx.mic_graph.addSeries(micSerie)
        cntx.mic_graph.viewport.isXAxisBoundsManual = true
        cntx.mic_graph.gridLabelRenderer.isHorizontalLabelsVisible = false

        // press rec button to start/stop recording
        cntx.mic_rec.onCheckedChange {
            if (it) {
                file.openNew()
            } else {
                file.close()
            }
        }

        // set mode
        cntx.mic_mode.setOnModeChanged { micSerie.mode = it }

        // clear button
        cntx.mic_clr.setOnClickListener { micSerie.clear() }
    }

    /**
     * Gets the new data and plots it
     */
    fun updateSound() {
        val amp: Double
        if (micSerie.mode == MODE.BUFFER || micSerie.mode == MODE.FFT) {
            // plot buffer
            micSerie.replaceData(sm.getFullBuffer())
            amp = micSerie.average
            cntx.mic_graph.gridLabelRenderer.isHorizontalLabelsVisible = true
        } else {
            // add amplitude
            amp = sm.getAmplitude()
            micSerie.addData(amp)
            cntx.mic_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        }

        // update graph
        cntx.mic_graph.viewport.setMaxX(micSerie.maxX)
        cntx.mic_graph.viewport.setMinX(micSerie.minX)
        cntx.mic_graph.viewport.setMaxY(micSerie.maxY)
        cntx.mic_graph.viewport.setMinY(micSerie.minY)
        micSerie.labelVerticalWidth = cntx.mic_graph.gridLabelRenderer.labelVerticalWidth * 2
        cntx.mic_txt.text = "Amplitude of $amp"
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
    var ar: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, HZ, FORMAT, ENCODING, minSize)

    /**
     * Starts recording
     */
    fun start() = ar.startRecording()

    /**
     * Stops recording
     */
    fun stop() = ar.stop()

    /**
     * Returns the amplitude
     */
    fun getAmplitude() = getFullBuffer().map { abs(it) }.maxOrNull() ?: 0.0

    /**
     * Returns the full buffer
     */
    fun getFullBuffer(): DoubleArray {
        val buffer = ShortArray(minSize)
        ar.read(buffer, 0, minSize)
        return buffer.map { it.toDouble() }.toDoubleArray()
    }
}