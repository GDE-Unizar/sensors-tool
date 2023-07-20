package es.unizar.gde.sensors.fragment

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import es.unizar.gde.sensors.MainActivity
import es.unizar.gde.sensors.R
import es.unizar.gde.sensors.tools.FileWriter
import es.unizar.gde.sensors.tools.MODE
import es.unizar.gde.sensors.tools.RangeSerie
import es.unizar.gde.sensors.utilities.Fragment
import kotlinx.android.synthetic.main.activity_main.acc_clr
import kotlinx.android.synthetic.main.activity_main.acc_graph
import kotlinx.android.synthetic.main.activity_main.acc_mode
import kotlinx.android.synthetic.main.activity_main.acc_nocal
import kotlinx.android.synthetic.main.activity_main.acc_rec
import kotlinx.android.synthetic.main.activity_main.acc_snap
import kotlinx.android.synthetic.main.activity_main.acc_txt
import java.text.NumberFormat

private val DELAY = 10 // in milliseconds

/**
 * Shows the accelerometer values
 */
class Accelerometer(
    val cntx: MainActivity,
) : Fragment, SensorEventListener {

    // utils
    private lateinit var sensorManager: SensorManager
    private val file = FileWriter(cntx, "acc")

    // series
    val serieX = RangeSerie().apply { color = Color.GREEN }.apply { hz = 1000.0 / DELAY }
    val serieY = RangeSerie().apply { color = Color.RED }.apply { hz = 1000.0 / DELAY }
    val serieZ = RangeSerie().apply { color = Color.BLUE }.apply { hz = 1000.0 / DELAY }
    private val series = sequenceOf(serieX, serieY, serieZ)


    /**
     * Initialize elements
     */
    override fun onCreate() {
        // sensor
        sensorManager = cntx.getSystemService(Context.SENSOR_SERVICE) as SensorManager


        // listeners
        cntx.acc_clr.setOnClickListener { series.forEach(RangeSerie::clear) }
        cntx.acc_rec.onCheckedChange { record(it) }
        cntx.acc_snap.setOnClickListener { snapshot() }
        cntx.acc_mode.setOnModeChanged { mode -> series.forEach { it.mode = mode } }
        cntx.acc_nocal.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setOnCheckedChangeListener { _, _ -> recreateAccelerometer() }
            else visibility = View.GONE
        }

        // other config
        cntx.acc_mode.disableMode(MODE.BUFFER)

        // graph acelerometer
        cntx.acc_graph.addSeries(serieX)
        cntx.acc_graph.addSeries(serieY)
        cntx.acc_graph.addSeries(serieZ)
        serieX.initializeGraph(cntx.acc_graph)
    }

    fun recreateAccelerometer() {
        onPause()
        onResume()
    }

    /**
     * starts (load=true) or stops (load=false) the record process on a file
     */
    fun record(load: Boolean) {
        if (load) {
            file.openNew("rec")
            file.writeLine("x_raw y_raw z_raw")
        } else {
            file.close()
        }
    }

    /**
     * Saves the currently displayed data to a file
     */
    fun snapshot() {
        file.openNew("snap", false)
        file.writeLine(
            when (serieX.mode) {
                MODE.RAW -> "x_raw y_raw z_raw"
                MODE.BASE -> "x_base y_base z_base"
                MODE.AVERAGE -> "x_avg y_avg z_avg"
                MODE.RANGE -> "x_rang y_rang z_rang"
                MODE.BUFFER -> "secret!" // this is impossible
                MODE.FFT -> "hz x y z"
            }
        )


        val itX = serieX.getValues(serieX.lowestValueX, serieX.highestValueX)
        val itY = serieY.getValues(serieY.lowestValueX, serieY.highestValueX)
        val itZ = serieZ.getValues(serieZ.lowestValueX, serieZ.highestValueX)

        while (itX.hasNext() && itY.hasNext() && itZ.hasNext()) {
            val nextX = itX.next()
            var line = ""
            if (serieX.mode.isBuff()) {
                line += nextX.x.toString() + " "
            }
            line += nextX.y.toString() + " "
            line += itY.next().y.toString() + " "
            line += itZ.next().y.toString()
            file.writeLine(line)
        }
        file.close()
    }

    /**
     * Returns the sensor type to use based on the device and checkbox
     */
    private fun getSensorType() = if (cntx.acc_nocal.isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Sensor.TYPE_ACCELEROMETER_UNCALIBRATED else Sensor.TYPE_ACCELEROMETER

    /**
     * When resuming, register the listener
     */
    override fun onResume() {
        val sensor = sensorManager.getDefaultSensor(getSensorType()) ?: run {
            Toast.makeText(cntx, R.string.no_sensor, Toast.LENGTH_SHORT).show()
            return
        }
        sensorManager.registerListener(this, sensor, DELAY * 1000)//SensorManager.SENSOR_DELAY_GAME)
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

        // add value to the series
        for ((i, serie) in sequenceOf(0 to serieX, 1 to serieY, 2 to serieZ)) {
            serie.addData(event.values[i].toDouble()) // millis.toDouble()))
        }

        // update graph
        cntx.acc_graph.onDataChanged(false, false)
        cntx.acc_graph.viewport.setMaxY((series.map { it.maxY }.maxOrNull() ?: 0.0) + 1.0)
        cntx.acc_graph.viewport.setMinY((series.map { it.minY }.minOrNull() ?: 0.0) - 1.0)
        series.forEach { it.labelVerticalWidth = cntx.acc_graph.gridLabelRenderer.labelVerticalWidth * 2 }

        // update text info
        SpannableStringBuilder().apply {
            for ((label, serie) in sequenceOf(X_pref to serieX, Y_pref to serieY, Z_pref to serieZ)) {
                val range = serie.range.format()
                val max = serie.highestValueY.format()
                val min = serie.lowestValueY.format()
                append(label)
                append("={$range} [$min,$max]")
            }
        }.let { cntx.acc_txt.setText(it, TextView.BufferType.SPANNABLE) }

        // save to file (if recording)
        file.writeLine(event.values.joinToString(" "))
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

}

/**
 * Number formatting object
 */
val num2str = NumberFormat.getInstance().apply { maximumFractionDigits = 3 }

/**
 * Number formatting extension
 */
private fun Double.format(): String {
    return num2str.format(this)
}

// colored strings
val X_pref = SpannableString("X").apply { setSpan(ForegroundColorSpan(Color.RED), 0, 1, 0) }
val Y_pref = SpannableString(" Y").apply { setSpan(ForegroundColorSpan(Color.GREEN), 1, 2, 0) }
val Z_pref = SpannableString(" Z").apply { setSpan(ForegroundColorSpan(Color.BLUE), 1, 2, 0) }