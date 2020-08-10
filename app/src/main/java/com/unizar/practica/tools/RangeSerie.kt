package com.unizar.practica.tools

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

const val SAMPLES = 100

class RangeSerie : LineGraphSeries<DataPoint>() {

    val rawData = LinkedList<DataPoint>()
    val rangeData = LinkedList<DataPoint>()

    var base = 0.0
    var baseTicks = -1
    var clear = false

    var plotRange = false
        set(value) {
            field = value
            update()
        }

    fun addData(dataPoint: DataPoint?) {
        if(clear){
            rawData.clear()
            rangeData.clear()
            clear = false
        }

        dataPoint?.let { rawData.add(it) }
        while (rawData.size > SAMPLES) rawData.removeFirst()

        val range = rawData.map { it.y }.run { (maxOrNull() ?: 0.0) - (minOrNull() ?: 0.0) }

        rangeData.add(DataPoint(dataPoint?.x ?: 0.0, range))
        while (rangeData.size > SAMPLES) rangeData.removeFirst()

        if (baseTicks == 0) base = rawData.last.y
        baseTicks--

        update()
    }

    fun markBase() {
        baseTicks = 10
    }

    fun clear(){
        clear = true
    }

    fun update() = super.resetData((if (plotRange) rangeData else rawData.map { DataPoint(it.x, it.y - base) }).toTypedArray())

    val rangeY
        get() = rangeData.last.y
}