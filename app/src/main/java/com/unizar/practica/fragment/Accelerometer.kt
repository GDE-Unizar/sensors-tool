package com.unizar.practica.fragment

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.jjoe64.graphview.series.DataPoint
import com.unizar.practica.MainActivity
import com.unizar.practica.SAMPLES
import com.unizar.practica.tools.FixSerie
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class Accelerometer(
        val cntx: MainActivity
) : SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor


    private var nextX: Double = 0.0
        get() = field++

    private val serieX = FixSerie().apply { color = Color.GREEN }
    private val serieY = FixSerie().apply { color = Color.RED }
    private val serieZ = FixSerie().apply { color = Color.BLUE }

    fun onCreate() {
        // sensor
        sensorManager = cntx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // listeners
        cntx.acc_tog.setOnCheckedChangeListener { _, c -> toggleRangePlot(c) }

        // graph acelerometer
        cntx.graph_acc.addSeries(serieX)
        cntx.graph_acc.addSeries(serieY)
        cntx.graph_acc.addSeries(serieZ)
        cntx.graph_acc.viewport.isXAxisBoundsManual = true
        cntx.graph_acc.gridLabelRenderer.isHorizontalLabelsVisible = false
        //cntx.graph.viewport.isScalable = true // setScalableX
        cntx.graph_acc.viewport.setScalableY(true)
    }

    fun onResume() = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)


    fun onPause() = sensorManager.unregisterListener(this)

    override fun onSensorChanged(event: SensorEvent) {

        val x = nextX
        for ((i, serie) in mapOf(Pair(0, serieX), Pair(1, serieY), Pair(2, serieZ))) {
            serie.appendData(DataPoint(x, event.values[i].toDouble()), true, SAMPLES)
        }
        cntx.graph_acc.onDataChanged(false, false)
        cntx.graph_acc.viewport.setMaxX(serieX.highestValueX)
        cntx.graph_acc.viewport.setMinX(serieX.lowestValueX)

        cntx.txt_acc.text = StringBuilder().apply {
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

    fun toggleRangePlot(state: Boolean) = listOf(serieX, serieY, serieZ).forEach { it.plotRange = state }
}

val num2str = NumberFormat.getInstance().apply { maximumFractionDigits = 5 }

private fun Double.format(): String {
    return num2str.format(this)
}