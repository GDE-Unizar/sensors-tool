package com.unizar.practica

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

class FixSerie : LineGraphSeries<DataPoint>() {

    private val rawData = LinkedList<DataPoint>()
    private val rangeData = LinkedList<DataPoint>()

    var plotRange = false
        set(value) {
            field = value
            update()
        }

    override fun appendData(dataPoint: DataPoint?, scrollToEnd: Boolean, maxDataPoints: Int) {
        dataPoint?.let { rawData.add(it) }
        while (rawData.size > maxDataPoints) rawData.removeFirst()

        val range = rawData.map { it.y }.run { (maxOrNull() ?: 0.0) - (minOrNull() ?: 0.0) }

        rangeData.add(DataPoint(dataPoint?.x ?: 0.0, range))
        while (rangeData.size > maxDataPoints) rangeData.removeFirst()

        update()
    }

    fun update() = super.resetData((if (plotRange) rangeData else rawData).toTypedArray())

    var rangeY = 0.0
        get() = rangeData.last.y
}