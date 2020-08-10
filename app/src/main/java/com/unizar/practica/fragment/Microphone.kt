package com.unizar.practica.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.unizar.practica.MainActivity
import com.unizar.practica.SAMPLES
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

private val HZ = 8000

class Microphone(
        val cntx: MainActivity
) {

    fun onCreate() {
        // graph microphone
        val sm = SoundMeter()
        sm.start()
        val micSerie = LineGraphSeries<DataPoint>()
        var micx = 0.0
        cntx.graph_mic.addSeries(micSerie)
        cntx.graph_mic.viewport.isXAxisBoundsManual = true
        cntx.graph_mic.gridLabelRenderer.isHorizontalLabelsVisible = false

        fixedRateTimer("mic", false, 0, 100) {
            micSerie.appendData(DataPoint(micx++, sm.amplitude), true, SAMPLES)
            cntx.graph_mic.onDataChanged(false, false)
            cntx.graph_mic.viewport.setMaxX(micSerie.highestValueX)
            cntx.graph_mic.viewport.setMinX(micSerie.lowestValueX)
        }
    }
}


class SoundMeter {

    private var minSize = AudioRecord.getMinBufferSize(HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    private var ar: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize)

    fun start() = ar.startRecording()

    fun stop() = ar.stop()

    val amplitude: Double
        get() {
            val buffer = ShortArray(minSize)
            ar.read(buffer, 0, minSize)

            return buffer.map { abs(it.toDouble()) }.maxOrNull() ?: 0.0
        }
}