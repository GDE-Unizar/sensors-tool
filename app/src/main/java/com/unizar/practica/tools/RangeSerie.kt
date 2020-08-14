package com.unizar.practica.tools

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.math.max

const val SAMPLES = 100

class RangeSerie : LineGraphSeries<DataPoint>() {

    val rawData = LinkedList<DataPoint>()
    val rangeData = LinkedList<DataPoint>()
    val avgData = LinkedList<DataPoint>()

    var base = 0.0
    var baseTicks = -1
    var clear = false

    var plotRange = false
        set(value) {
            field = value
            update()
        }

    fun addData(dataPoint: DataPoint?) {
        if (clear) {
            rawData.clear()
            rangeData.clear()
            clear = false
        }

        dataPoint?.let { rawData.add(it) }
        while (rawData.size > SAMPLES) rawData.removeFirst()

        val range = rawData.map { it.y }.run { (maxOrNull() ?: 0.0) - (minOrNull() ?: 0.0) }
        rangeData.add(DataPoint(dataPoint?.x ?: 0.0, range))
        while (rangeData.size > SAMPLES) rangeData.removeFirst()

        val avg = rawData.listIterator(max(0, rawData.size - 10)).asSequence().map { it.y }.average()
        avgData.add(DataPoint(dataPoint?.x ?: 0.0, avg))
        while (avgData.size > SAMPLES) avgData.removeFirst()

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

    val lastRange
        get() = rangeData.last.y

    fun getAverage(): Double {
        return avgData.last.y
    }
}