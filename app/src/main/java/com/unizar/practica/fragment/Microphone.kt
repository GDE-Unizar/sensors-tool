package com.unizar.practica.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.RangeSerie
import com.unizar.practica.tools.onCheckedChange
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

const val HZ = 40000//8000

/**
 * Shows input recorded by the microphone
 */
class Microphone(
        val cntx: MainActivity
) : Fragment {

    // utils
    val sm = SoundMeter()
    val micSerie = RangeSerie()
    private val file = FileWriter(cntx, "mic")

    // if true, show buffer, if false show amplitude
    //TODO: replace with an enum
    var show_buffer = false

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

        // toggle show_buffer
        cntx.mic_buff.onCheckedChange {
            show_buffer = it
            cntx.mic_graph.gridLabelRenderer.isHorizontalLabelsVisible = it
            if (!it) {
                micSerie.clear()
            }
        }

        // clear button
        cntx.mic_clr.setOnClickListener { micSerie.clear() }
    }

    /**
     * Gets the new data and plots it
     */
    fun updateSound() {
        val amp: Double
        if (show_buffer) {
            // plot buffer
            micSerie.replaceData(sm.getFullBuffer())
            amp = micSerie.average
        } else {
            // add amplitude
            amp = sm.getAmplitude()
            micSerie.addData(amp)
        }

        // update graph
        cntx.mic_graph.onDataChanged(false, false)
        cntx.mic_graph.viewport.setMaxX(micSerie.maxX)
        cntx.mic_graph.viewport.setMinX(micSerie.minX)
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

    // properties
    var minSize = AudioRecord.getMinBufferSize(HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT).apply { println(this) }

    // utils
    var ar: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize)

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
    fun getAmplitude() = getFullBuffer().map { abs(it.toDouble()) }.maxOrNull() ?: 0.0

    /**
     * Returns the full buffer
     */
    fun getFullBuffer(): ShortArray {
        val buffer = ShortArray(minSize)
        ar.read(buffer, 0, minSize)
        return buffer
    }
}