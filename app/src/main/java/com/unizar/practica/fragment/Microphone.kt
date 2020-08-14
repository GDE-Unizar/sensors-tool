package com.unizar.practica.fragment

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import com.jjoe64.graphview.series.DataPoint
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.RangeSerie
import com.unizar.practica.tools.onCheckedChange
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

const val HZ = 8000

const val SAMPLES = 100

class Microphone(
        val cntx: MainActivity
) : Fragment {

    val sm = SoundMeter()
    val micSerie = RangeSerie()
    var micx = 0.0

    private val file = FileWriter(cntx, "mic")

    override fun onCreate() {
        handler = Handler(Looper.getMainLooper())

        // graph microphone
        cntx.mic_graph.addSeries(micSerie)
        cntx.mic_graph.viewport.isXAxisBoundsManual = true
        cntx.mic_graph.gridLabelRenderer.isHorizontalLabelsVisible = false

        cntx.mic_rec.onCheckedChange {
            if (it) {
                file.openNew()
            } else {
                file.close()
            }
        }
    }

    fun updateSound() {
        val amp = sm.getAmplitude()
        micSerie.addData(DataPoint(micx++, amp))
        cntx.mic_graph.onDataChanged(false, false)
        cntx.mic_graph.viewport.setMaxX(micSerie.highestValueX)
        cntx.mic_graph.viewport.setMinX(micSerie.highestValueX - SAMPLES)
        cntx.mic_txt.text = "Amplitude of $amp"
        file.writeLine(amp)
    }

    fun getAvgAmplitude(): Double {
        return micSerie.getAverage()
    }

    // ---- updater ---------

    lateinit var handler: Handler

    val updateTask = object : Runnable {
        override fun run() {
            updateSound()
            handler.postDelayed(this, 0)
        }
    }

    override fun onResume() {
        sm.start()
        handler.post(updateTask)
    }

    override fun onPause() {
        handler.removeCallbacks(updateTask)
        sm.stop()
    }
}


class SoundMeter {

    var minSize = AudioRecord.getMinBufferSize(HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    var ar: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize)

    fun start() = ar.startRecording()

    fun stop() = ar.stop()

    fun getAmplitude(): Double {
        val buffer = ShortArray(minSize)
        ar.read(buffer, 0, minSize)

        return buffer.map { abs(it.toDouble()) }.maxOrNull() ?: 0.0
    }
}