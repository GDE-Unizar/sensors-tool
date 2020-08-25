package com.unizar.practica.tools

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Number of samples to keep
 */
var SAMPLES = 200

/**
 * A LineGraphSeries but with more functionality
 */
class RangeSerie : LineGraphSeries<DataPoint>() {

    // config
    var mode = MODE.RAW
        set(value) {
            if (value == MODE.BASE) markBase()
            clear()
            field = value
        }
    var labelVerticalWidth = 20
    var hz = 1.0

    // data containers
    private val rawData = LinkedList<Double>()
    private val rangeData = LinkedList<Double>()
    private val avgData = LinkedList<Double>()

    // values
    private var base = 0.0
    private var flag_clear = false
    private var offsetX = 0.0
    private var graph: GraphView? = null

    fun initializeGraph(graph: GraphView) {
        this.graph = graph

        // initial configuration
        with(graph) {
            viewport.isXAxisBoundsManual = true
            viewport.isYAxisBoundsManual = true
            gridLabelRenderer.isHorizontalLabelsVisible = false
//            viewport.isScalable = true // setScalableX
//            viewport.setScalableY(true)
        }
    }

    /**
     * Replaces all the data with the new array
     */
    fun replaceData(input: DoubleArray) {
        _clear(flag_clear)
        rawData.addAll(input.asIterable())
        rangeData.add((input.maxOrNull() ?: 0.0) - (input.minOrNull() ?: 0.0))
        avgData.add(input.average())

        update()
    }

    /**
     * Adds a single data to existing ones
     */
    fun addData(value: Double) {
        // clear if required
        if (flag_clear) {
            _clear(true)
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

        // update internal data
        offsetX++
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
    private fun _clear(all: Boolean) {
        rawData.clear()
        rangeData.clear()
        avgData.clear()
        offsetX = 0.0
        flag_clear = false
        if (all) {
            maxY = Double.MIN_VALUE
            minY = Double.MAX_VALUE
        }
    }

    /**
     * Sets the internal data for the serie
     */
    fun update() {
        var xArray: List<Double>? = null

        val list = when (mode) {
            MODE.RAW -> rawData
            MODE.BASE -> rawData.map { it - base }
            MODE.AVERAGE -> avgData
            MODE.RANGE -> rangeData
            MODE.BUFFER -> rawData
            MODE.FFT -> {
                val result = rawData.FFT(hz)
                xArray = result.second
                result.first
            }
        }

        val data: List<DataPoint>
        if (xArray == null) {
            var x = offsetX - list.size
            data = list.map { DataPoint(x++, it) }
        } else {
            data = list.mapIndexed { i, it -> DataPoint(xArray[i], it) }
        }

        val _maxY = list.maxOrNull() ?: 0.0
        maxY = if (_maxY > maxY) _maxY else maxY * 0.99 + _maxY * 0.01
        val _minY = list.minOrNull() ?: 0.0
        minY = if (_minY < minY) _minY else minY * 0.99 + _minY * 0.01
        super.resetData(data.toTypedArray())

        // update graph if present
        graph?.run {
            viewport.setMaxX(maxX)
            viewport.setMinX(minX)
//            viewport.setMaxY(series.map { it.maxY }.maxOrNull() ?: 0.0)
//            viewport.setMinY(series.map { it.minY }.minOrNull() ?: 0.0)
            gridLabelRenderer.isHorizontalLabelsVisible = mode == MODE.FFT
        }
    }

    /**
     * The current range of the data
     */
    val range
        get() = rangeData.lastOrNull() ?: 0.0

    /**
     * The current average of the data
     */
    val average
        get() = avgData.lastOrNull() ?: 0.0

    /**
     * The current max X value of the data
     */
    val maxX
        get() = super.getHighestValueX()

    /**
     * The current min X value of the data
     */
    val minX
        get() = if (mode.isBuff()) super.getLowestValueX() else min(super.getLowestValueX(), maxX - SAMPLES)

    /**
     * The smoother max Y value of the data
     */
    var maxY = 0.0
        private set

    /**
     * The smoother min Y value of the data
     */
    var minY = 0.0
        private set

}
