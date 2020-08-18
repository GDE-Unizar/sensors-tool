package com.unizar.practica.tools

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
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
    val rawData = LinkedList<DataPoint>()
    val rangeData = LinkedList<DataPoint>()
    val avgData = LinkedList<DataPoint>()

    // values
    var base = 0.0
    var baseTicks = -1
    var clear = false

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
        rawData.clear()
        rangeData.clear()

        rawData.addAll(data.mapIndexed { i, x -> DataPoint(i.toDouble(), x.toDouble()) })
        rangeData.add(DataPoint(0.0,
                (data.maxOrNull()?.toDouble() ?: 0.0) - (data.minOrNull()?.toDouble() ?: 0.0)
        ))
        avgData.add(DataPoint(0.0, data.average()))

        update()
    }

    fun addData(dataPoint: DataPoint?) {
        // clear if required
        if (clear) {
            rawData.clear()
            rangeData.clear()
            avgData.clear()
            clear = false
        }

        // add to raw
        dataPoint?.let { rawData.add(it) }
        while (rawData.size > SAMPLES) rawData.removeFirst()

        // update range
        val range = rawData.map { it.y }.run { (maxOrNull() ?: 0.0) - (minOrNull() ?: 0.0) }
        rangeData.add(DataPoint(dataPoint?.x ?: 0.0, range))
        while (rangeData.size > SAMPLES) rangeData.removeFirst()

        // update average
        val avg = rawData.listIterator(max(0, rawData.size - 10)).asSequence().map { it.y }.average()
        avgData.add(DataPoint(dataPoint?.x ?: 0.0, avg))
        while (avgData.size > SAMPLES) avgData.removeFirst()

        // update base
        if (baseTicks == 0) base = rawData.last.y
        baseTicks--

        update()
    }

    fun markBase() {
        baseTicks = 10
    }

    fun clear() {
        clear = true
    }

    fun update() = super.resetData((if (plotRange) rangeData else rawData.map { DataPoint(it.x, it.y - base) }).toTypedArray())
    //TODO: save only the y points, autoupdate the x values

    val lastRange
        get() = rangeData.last.y

    fun getAverage(): Double {
        return avgData.last.y
    }

    val maxX: Double
        get() = super.getHighestValueX()
    val minX: Double
        get() = min(super.getLowestValueX(), maxX - SAMPLES)

}