package es.unizar.gde.sensors.fragment

import android.media.AudioManager
import android.media.AudioTrack
import es.unizar.gde.sensors.MainActivity
import es.unizar.gde.sensors.utilities.Fragment
import es.unizar.gde.sensors.utilities.onCheckedChange
import es.unizar.gde.sensors.utilities.onProgressChange
import kotlin.math.atan
import kotlin.math.sin

/**
 * Allows to play a tune of any frequency from the speaker
 */
class Speaker(
        val cntx: MainActivity,
) : Fragment {
    private val views = cntx.views

    // util
    private var thread: TuneThread? = null

    /**
     * Initialize
     */
    override fun onCreate() {

        // toggle to play/stop
        views.spkToggle.onCheckedChange {
            if (it) play() else stop()
        }

        // change hz bar
        views.spkHz.onProgressChange {
            thread?.tuneFreq = it.toDouble() // update if playing
            views.spkToggle.text = "$it Hz"
        }
        views.spkHz.progress = 440 // initial value

        // increase/decrease buttons
        views.spkInc.setOnClickListener { views.spkHz.progress += 1 }
        views.spkDec.setOnClickListener { views.spkHz.progress -= 1 }
    }

    /**
     * On pause, stop
     */
    override fun onPause() {
        views.spkToggle.isChecked = false
    }

    /**
     * Plays the tune (non-stop)
     */
    fun play() {
        if (thread != null) {
            // already playing, update
            thread?.tuneFreq = views.spkHz.progress.toDouble()
        } else {
            // not playing, start
            thread = TuneThread().apply {
                tuneFreq = views.spkHz.progress.toDouble()
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
