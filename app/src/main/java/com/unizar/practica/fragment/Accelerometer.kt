package com.unizar.practica.fragment

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.jjoe64.graphview.series.DataPoint
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.RangeSerie
import com.unizar.practica.tools.onCheckedChange
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class Accelerometer(
        val cntx: MainActivity
) : Fragment, SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private val file = FileWriter(cntx, "acc")


    private var nextX: Double = 0.0
        get() = field++

    private val serieX = RangeSerie().apply { color = Color.GREEN }
    private val serieY = RangeSerie().apply { color = Color.RED }
    private val serieZ = RangeSerie().apply { color = Color.BLUE }
    private val series = sequenceOf(serieX, serieY, serieZ)

    override fun onCreate() {
        // sensor
        sensorManager = cntx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // listeners
        cntx.acc_toggle.onCheckedChange { toggleRangePlot(it) }
        cntx.acc_base.setOnClickListener { series.forEach(RangeSerie::markBase) }
        cntx.acc_clr.setOnClickListener { series.forEach(RangeSerie::clear) }
        cntx.acc_rec.onCheckedChange { record(it) }

        // graph acelerometer
        cntx.acc_graph.addSeries(serieX)
        cntx.acc_graph.addSeries(serieY)
        cntx.acc_graph.addSeries(serieZ)
        cntx.acc_graph.viewport.isXAxisBoundsManual = true
        cntx.acc_graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        //cntx.graph.viewport.isScalable = true // setScalableX
        cntx.acc_graph.viewport.setScalableY(true)
    }

    fun record(load: Boolean) {
        if (load) {
            file.openNew()
        } else {
            file.close()
        }
    }

    override fun onResume() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        cntx.acc_rec.isChecked = false
    }

    override fun onSensorChanged(event: SensorEvent) {

        val x = nextX
        for ((i, serie) in sequenceOf(Pair(0, serieX), Pair(1, serieY), Pair(2, serieZ))) {
            serie.addData(DataPoint(x, event.values[i].toDouble()))
        }
        cntx.acc_graph.onDataChanged(false, false)
        cntx.acc_graph.viewport.setMaxX(serieX.highestValueX)
        cntx.acc_graph.viewport.setMinX(serieX.highestValueX - SAMPLES)

        cntx.acc_txt.text = StringBuilder().apply {
            for ((label, serie) in sequenceOf(Pair("X", serieX), Pair("\nY", serieY), Pair("\nZ", serieZ))) {
                val range = serie.rangeY.format()
                val max = serie.highestValueY.format()
                val min = serie.lowestValueY.format()
                append("$label={$range} [$min,$max]")
            }
        }

        file.writeLine(event.values.joinToString(" "))
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    fun toggleRangePlot(state: Boolean) = series.forEach { it.plotRange = state }
}

val num2str = NumberFormat.getInstance().apply { maximumFractionDigits = 4 }

private fun Double.format(): String {
    return num2str.format(this)
}