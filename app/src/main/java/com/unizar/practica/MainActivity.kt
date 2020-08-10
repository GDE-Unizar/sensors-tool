package com.unizar.practica

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.function.Consumer
import kotlin.text.StringBuilder

const val SAMPLES = 100

class MainActivity : Activity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var vibrator: Vibrator

    private val vibconfig = longArrayOf(50, 50)

    private var nextX: Double = 0.0
        get() = field++

    private val serieX = FixSerie().apply { color = Color.GREEN }
    private val serieY = FixSerie().apply { color = Color.RED }
    private val serieZ = FixSerie().apply { color = Color.BLUE }

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
        swt_vib.setOnCheckedChangeListener { _, c -> toggleVibration(c) }
        skb_on.setOnProgressChangedListener { value -> updateProgressBar(0, value) }
        skb_off.setOnProgressChangedListener { value -> updateProgressBar(1, value) }
        acc_tog.setOnCheckedChangeListener { _, c -> toggleRangePlot(c) }
        updateVibrationUI()

        // graph
        graph.addSeries(serieX)
        graph.addSeries(serieY)
        graph.addSeries(serieZ)
        graph.viewport.isXAxisBoundsManual = true
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        //graph.viewport.isScalable = true // setScalableX
        graph.viewport.setScalableY(true)
    }

    // ----------------------- sensor -------------------

    override fun onSensorChanged(event: SensorEvent) {

        val x = nextX
        for ((i, serie) in mapOf(Pair(0, serieX), Pair(1, serieY), Pair(2, serieZ))) {
            serie.appendData(DataPoint(x, event.values[i].toDouble()), true, SAMPLES)
        }
        graph.onDataChanged(false, false)
        graph.viewport.setMaxX(serieX.highestValueX)
        graph.viewport.setMinX(serieX.lowestValueX)

        txt_acc.text = StringBuilder().apply {
            for ((label, serie) in mapOf(Pair("X", serieX), Pair("Y", serieY), Pair("Z", serieZ))) {
                append(label)
                append("=")
                append("{")
                append(serie.rangeY.format())
                append("}")
                append(" ")
                append("[")
                append(serie.highestValueY.format())
                append(",")
                append(serie.lowestValueY.format())
                append("]")
                append("\n")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    // ------------------------ switch ------------------------

    fun toggleVibration(state: Boolean) = if (state) vibrator.vibrate(vibconfig, 0) else vibrator.cancel()

    fun toggleRangePlot(state: Boolean) = listOf(serieX, serieY, serieZ).forEach { it.plotRange = state }

    // ------------------------- seekbar ---------------------

    fun updateProgressBar(id: Int, progress: Int) {
        vibconfig[id] = (progress + 1).toLong()
        updateVibrationUI()
    }


    private fun updateVibrationUI() {
        swt_vib.text = "on=${vibconfig[0]} - off=${vibconfig[1]}"
    }

}

//----------------- utilities ---------------------

inline fun SeekBar.setOnProgressChangedListener(crossinline listener: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            listener(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    })
}


val num2str = NumberFormat.getInstance().apply { maximumFractionDigits = 5 }

private fun Double.format(): String {
    return num2str.format(this)
}