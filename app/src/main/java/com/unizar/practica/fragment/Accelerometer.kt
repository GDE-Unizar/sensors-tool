package com.unizar.practica.fragment

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.unizar.practica.MainActivity
import com.unizar.practica.tools.FileWriter
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.RangeSerie
import com.unizar.practica.tools.onCheckedChange
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

/**
 * Shows the accelerometer values
 */
class Accelerometer(
        val cntx: MainActivity
) : Fragment, SensorEventListener {

    // utils
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private val file = FileWriter(cntx, "acc")

    // series
    val serieX = RangeSerie().apply { color = Color.GREEN }
    val serieY = RangeSerie().apply { color = Color.RED }
    val serieZ = RangeSerie().apply { color = Color.BLUE }
    private val series = sequenceOf(serieX, serieY, serieZ)

    // to measuer update
    private var millisec: Long = System.currentTimeMillis()
        get() {
            val prev = field
            field = System.currentTimeMillis()
            return field - prev
        }

    /**
     * Initialize elements
     */
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

    /**
     * starts (load=true) or stops (load=false) the record process on a file
     */
    fun record(load: Boolean) {
        if (load) {
            file.openNew()
        } else {
            file.close()
        }
    }

    /**
     * When resuming, register the listener
     */
    override fun onResume() {
        sensorManager.registerListener(this, accelerometer, 10 * 1000)//SensorManager.SENSOR_DELAY_GAME)
    }

    /**
     * When pausing, unregister
     */
    override fun onPause() {
        sensorManager.unregisterListener(this)
        cntx.acc_rec.isChecked = false
    }

    /**
     * A new value! lets use it
     */
    override fun onSensorChanged(event: SensorEvent) {
        val millis = millisec

        // add value to the series
        for ((i, serie) in sequenceOf(Pair(0, serieX), Pair(1, serieY), Pair(2, serieZ))) {
            serie.addData(event.values[i].toDouble()) // millis.toDouble()))
        }

        // update graph
        cntx.acc_graph.onDataChanged(false, false)
        cntx.acc_graph.viewport.setMaxX(serieX.maxX)
        cntx.acc_graph.viewport.setMinX(serieX.minX)

        // update text info
        cntx.acc_txt.text = StringBuilder().apply {
            for ((label, serie) in sequenceOf(Pair("X", serieX), Pair("\nY", serieY), Pair("\nZ", serieZ))) {
                val range = serie.range.format()
                val max = serie.highestValueY.format()
                val min = serie.lowestValueY.format()
                append("$label={$range} [$min,$max]")
            }
            append("\n$millis")
        }

        // save to file (if recording)
        file.writeLine(event.values.joinToString(" "))
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    /**
     * toggles the range/raw data plot
     */
    fun toggleRangePlot(state: Boolean) = series.forEach { it.plotRange = state }
}

/**
 * Number formatting object
 */
val num2str = NumberFormat.getInstance().apply { maximumFractionDigits = 4 }

/**
 * Number formatting extension
 */
private fun Double.format(): String {
    return num2str.format(this)
}