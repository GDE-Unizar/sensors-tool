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
    val rawData = LinkedList<Double>()
    val rangeData = LinkedList<Double>()
    val avgData = LinkedList<Double>()

    // values
    private var base = 0.0
    private var baseTicks = -1
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

        rawData.addAll(data.map { it.toDouble() })
        rangeData.add((data.maxOrNull()?.toDouble() ?: 0.0) - (data.minOrNull()?.toDouble() ?: 0.0))
        avgData.add(data.average())

        update()
    }

    fun addData(value: Double) {
        // clear if required
        if (flag_clear) {
            _clear()
        }

        // update base
        if (baseTicks == 0) base = value
        baseTicks--

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

        offsetX = 20 + (offsetX + 1) % 20

        update()
    }

    fun markBase() {
        baseTicks = 10
    }

    fun clear() {
        flag_clear = true
    }

    fun _clear() {
        rawData.clear()
        rangeData.clear()
        avgData.clear()
        offsetX = 0.0
        flag_clear = false
    }

    fun update() {

        val list =
                if (plotRange) rangeData
                else rawData.map { it - base }

        var x = if (list.size < SAMPLES) 0.0 else offsetX
        super.resetData(list.map { DataPoint(x++, it) }.toTypedArray())
    }

    val lastRange
        get() = rangeData.last

    fun getAverage(): Double {
        return avgData.last
    }

    val maxX: Double
        get() = super.getHighestValueX()
    val minX: Double
        get() = min(super.getLowestValueX(), maxX - SAMPLES)

}