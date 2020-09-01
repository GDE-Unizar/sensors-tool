package com.gde.sensors.tools

import android.content.Context
import android.media.AudioManager

private val STREAM_TYPE = AudioManager.STREAM_MUSIC

class Volume(cntx: Context) {
    val am = cntx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun setVolume(vol: Int) {
        am.setStreamVolume(STREAM_TYPE, vol, AudioManager.FLAG_SHOW_UI)
    }

    val maxVolume = am.getStreamMaxVolume(STREAM_TYPE)
}