package com.unizar.practica.tools

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.unizar.practica.fragment.HZ
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Number of samples to keep
 */
const val SAMPLES = 100

/**
 * A LineGraphSeries but with more functionality
 */
class RangeSerie : LineGraphSeries<DataPoint>() {

    // data containers
    val rawData = LinkedList<Double>()
    val rangeData = LinkedList<Double>()
    val avgData = LinkedList<Double>()

    // values
    private var base = 0.0
    private var flag_clear = false
    private var offsetX = 0.0

    /**
     * Whether to plot range (true) or just data (false)
     */
    var plotRange = false
        set(value) {
            field = value
            update()
        }


    /**
     * Replaces all the data with the new array
     */
    fun replaceData(data: ShortArray) {
        _clear()

        val (fftX, fftY) = data.FFT(HZ)

        rawData.addAll(fftX.asIterable()/*.map { it.toDouble() }*/)
        rangeData.add((data.maxOrNull()?.toDouble() ?: 0.0) - (data.minOrNull()?.toDouble() ?: 0.0))
        avgData.add(data.average())

        update(fftY)
    }

    /**
     * Adds a single data to existing ones
     */
    fun addData(value: Double) {
        // clear if required
        if (flag_clear) {
            _clear()
        }

        // add to raw
        rawData.add(value)
        while (rawData.size > SAMPLES) rawData.removeFirst()

        // update range
        val range = rawData.run { (maxOrNull() ?: 0.0) - (minOrNull() ?: 0.0) }
        rangeData.add(range)
        while (rangeData.size > SAMPLES) rangeData.removeFirst()

        // update average
        val avg = rawData.listIterator(max(0, rawData.size - 10)).asSequence().average()
        avgData.add(avg)
        while (avgData.size > SAMPLES) avgData.removeFirst()

        // increase offset
        offsetX = 20 + (offsetX + 1) % 20 // 20 is the distance between vertical bars

        // update internal data
        update()
    }

    /**
     * Keeps current average value as the base (substracted from the raw data)
     */
    fun markBase() {
        base = rawData.average()
    }

    /**
     * Clears all current data
     */
    fun clear() {
        // to run in the addData thread, otherwise a concurrent modification exception is thrown
        flag_clear = true
    }

    /**
     * Performs the clear operations
     */
    private fun _clear() {
        rawData.clear()
        rangeData.clear()
        avgData.clear()
        offsetX = 0.0
        flag_clear = false
    }

    /**
     * Sets the internal data for the serie
     */
    fun update(xArray: DoubleArray? = null) {
        val list =
                if (plotRange) rangeData
                else rawData.map { it - base }

        val data: List<DataPoint>
        if (xArray == null) {
            var x = if (list.size < SAMPLES) 0.0 else offsetX
            data = list.map { DataPoint(x++, it) }
        } else {
            data = list.mapIndexed { i, l -> DataPoint(xArray[i], l) }
        }

        super.resetData(data.toTypedArray())
    }

    /**
     * The current range of the data
     */
    val range
        get() = rangeData.last

    /**
     * The current average of the data
     */
    val average: Double
        get() {
            return avgData.last
        }

    /**
     * The current max X value of the data
     */
    val maxX: Double
        get() = super.getHighestValueX()

    /**
     * The current min X value of the data
     */
    val minX: Double
        get() = min(super.getLowestValueX(), maxX - SAMPLES)

}