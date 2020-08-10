package com.unizar.practica.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.unizar.practica.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

const val HZ = 8000

const val SAMPLES = 100

class Microphone(
        val cntx: MainActivity
) {

    val sm = SoundMeter()
    val micSerie = LineGraphSeries<DataPoint>()
    var micx = 0.0

    fun onCreate() {
        handler = Handler(Looper.getMainLooper())

        // graph microphone
        cntx.graph_mic.addSeries(micSerie)
        cntx.graph_mic.viewport.isXAxisBoundsManual = true
        cntx.graph_mic.gridLabelRenderer.isHorizontalLabelsVisible = false
    }

    fun updateSound() {
        micSerie.appendData(DataPoint(micx++, sm.amplitude), true, SAMPLES)
        cntx.graph_mic.onDataChanged(false, false)
        cntx.graph_mic.viewport.setMaxX(micSerie.highestValueX)
        cntx.graph_mic.viewport.setMinX(micSerie.highestValueX - SAMPLES)
    }

    // ---- updater ---------

    lateinit var handler: Handler

    val updateTask = object : Runnable {
        override fun run() {
            updateSound()
            handler.postDelayed(this, 100)
        }
    }

    fun onResume() {
        sm.start()
        handler.post(updateTask)
    }

    fun onPause() {
        handler.removeCallbacks(updateTask)
        sm.stop()
    }
}


class SoundMeter {

    var minSize = AudioRecord.getMinBufferSize(HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    var ar: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize)

    fun start() = ar.startRecording()

    fun stop() = ar.stop()

    val amplitude: Double
        get() {
            val buffer = ShortArray(minSize)
            ar.read(buffer, 0, minSize)

            return buffer.map { abs(it.toDouble()) }.maxOrNull() ?: 0.0
        }
}