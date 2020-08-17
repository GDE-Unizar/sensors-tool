package com.unizar.practica.tools

import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.atan
import kotlin.math.sin

/**
 * Allows to play a specific frequency
 * Adapted from https://github.com/karlotoy/perfectTune
 */
class Tune {

    private var thread: TuneThread? = null

    /**
     * The configured frequency that will play (can be modified while it is playing)
     */
    var freq: Double = 440.0
        set(value) {
            field = value
            thread?.tuneFreq = field
        }

    /**
     * Plays the tune (non-stop)
     */
    fun play() {
        if (thread != null) {
            // already playing, update
            thread?.tuneFreq = freq
        } else {
            // not playing, start
            thread = TuneThread().apply {
                tuneFreq = freq
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
        val amp = 10000
        val twopi = 8.0 * atan(1.0)
        var ph = 0.0
        audioTrack.play()
        while (isRunning) {
            val fr = tuneFreq
            for (i in 0 until buffsize) {
                samples[i] = (amp.toDouble() * sin(ph)).toShort()
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