package com.unizar.practica.fragment

import android.media.AudioManager
import android.media.AudioTrack
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.onCheckedChange
import com.unizar.practica.tools.onProgressChange
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.atan
import kotlin.math.sin

/**
 * Allows to play a tune of any frequency from the speaker
 */
class Speaker(
        val cntx: MainActivity
) : Fragment {

    // util
    private var thread: TuneThread? = null

    /**
     * Initialize
     */
    override fun onCreate() {

        // toggle to play/stop
        cntx.spk_toggle.onCheckedChange {
            if (it) play() else stop()
        }

        // change hz bar
        cntx.spk_hz.onProgressChange {
            thread?.tuneFreq = it.toDouble() // update if playing
            cntx.spk_toggle.text = "$it Hz"
        }
        cntx.spk_hz.progress = 440 // initial value

        // increase/decrease buttons
        cntx.spk_inc.setOnClickListener { cntx.spk_hz.progress += 10 }
        cntx.spk_dec.setOnClickListener { cntx.spk_hz.progress -= 10 }
    }

    /**
     * On pause, stop
     */
    override fun onPause() {
        cntx.spk_toggle.isChecked = false
    }

    /**
     * Plays the tune (non-stop)
     */
    fun play() {
        if (thread != null) {
            // already playing, update
            thread?.tuneFreq = cntx.spk_hz.progress.toDouble()
        } else {
            // not playing, start
            thread = TuneThread().apply {
                tuneFreq = cntx.spk_hz.progress.toDouble()
                start()
            }
        }
    }

    /**
     * Stops the tune
     */
    fun stop() {
        thread?.stopTune()
        thread = null
    }
}

/**
 * The thread that plays the tune
 */
class TuneThread : Thread() {

    var tuneFreq = 440.0

    private var isRunning = false

    /**
     * When the thread runs, plays the frequency
     */
    override fun run() {
        // init
        isRunning = true
        val sr = 44100
        val buffsize = AudioTrack.getMinBufferSize(sr, 4, 2)
        val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sr, 4, 2, buffsize, 1)
        val samples = ShortArray(buffsize)
        val amp = 10000.0
        val twopi = 8.0 * atan(1.0)
        var ph = 0.0
        audioTrack.play()
        while (isRunning) {
            val fr = tuneFreq
            for (i in 0 until buffsize) {
                samples[i] = (amp * sin(ph)).toInt().toShort()
                ph += twopi * fr / sr.toDouble()
            }
            audioTrack.write(samples, 0, buffsize)
        }
        audioTrack.stop()
        audioTrack.release()
    }

    /**
     * Stops the thread
     */
    fun stopTune() {
        isRunning = false
        try {
            join()
            interrupt()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
