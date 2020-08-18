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

class Microphone(
        val cntx: MainActivity
) : Fragment {

    val sm = SoundMeter()
    val micSerie = RangeSerie()
    var show_buffer = false

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

        cntx.mic_buff.onCheckedChange {
            show_buffer = it

            if (!it) {
                micSerie.clear()
            }
        }

        cntx.mic_clr.setOnClickListener { micSerie.clear() }
    }

    fun updateSound() {
        val amp: Double
        if (show_buffer) {
            micSerie.replaceData(sm.getFullBuffer())
            amp = micSerie.getAverage()
        } else {
            amp = sm.getAmplitude()
            micSerie.addData(amp)
        }
        cntx.mic_graph.onDataChanged(false, false)
        cntx.mic_graph.viewport.setMaxX(micSerie.maxX)
        cntx.mic_graph.viewport.setMinX(micSerie.minX)
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

    fun getFullBuffer(): ShortArray {
        val buffer = ShortArray(minSize)
        ar.read(buffer, 0, minSize)

        return buffer
    }
}