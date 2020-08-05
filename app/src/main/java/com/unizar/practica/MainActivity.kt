package com.unizar.practica

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), SensorEventListener, CompoundButton.OnCheckedChangeListener, OnSeekBarChangeListener {

    lateinit private var sensorManager: SensorManager
    lateinit private var accelerometer: Sensor
    lateinit private var vibrator: Vibrator

    private val vibconfig = longArrayOf(50, 50)

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // listeners
        swt_vib.setOnCheckedChangeListener(this)
        skb_on.setOnSeekBarChangeListener(this)
        skb_off.setOnSeekBarChangeListener(this)
        updateVibrationUI()
    }

    // ----------------------- sensor -------------------

    override fun onSensorChanged(event: SensorEvent) {
        txt_acc.text = "X=${event.values[0]}\nY=${event.values[1]}\nZ=${event.values[2]}"
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    // ------------------------ switch ------------------------

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            // vibrate
            vibrator.vibrate(vibconfig, 0)
        } else {
            vibrator.cancel()
        }
    }

    // ------------------------- seekbar ---------------------

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        vibconfig[when(seekBar.id) {
            R.id.skb_on -> 0
            R.id.skb_off -> 1
            else -> throw Throwable("Invalid id")
        }] = (progress + 1).toLong()
        updateVibrationUI()
    }

    private fun updateVibrationUI() {
        swt_vib.text = "on=${vibconfig[0]} - off=${vibconfig[1]}"
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}