/**
 * From https://github.com/karlotoy/perfectTune
 */

package com.unizar.practica.tools

import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.atan
import kotlin.math.sin

class Tune {

    var thread: TuneThread? = null

    var freq: Double = 440.0
        set(value) {
            field = value
            thread?.tuneFreq = field
        }

    fun start() {
        thread = (thread ?: TuneThread()).apply {
            tuneFreq = freq
            start()
        }
    }

    fun stop() {
        thread?.stopTune()
        thread = null
    }
}

class TuneThread : Thread() {

    var isRunning = false
    var tuneFreq = 440.0

    val sr = 44100

    override fun run() {
        super.run()
        isRunning = true
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