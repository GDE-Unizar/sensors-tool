package com.gde.sensors.tools

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.gde.sensors.R

/**
 * Mode of a graph
 */
enum class MODE(val id: Int) {
    RAW(R.string.mode_raw),
    BASE(R.string.mode_base),
    AVERAGE(R.string.mode_avg),
    RANGE(R.string.mode_range),
    BUFFER(R.string.mode_buff),
    FFT(R.string.mode_fft),
    ;

    /**
     * @return true iff it is FFT or BUFFER
     */
    fun isBuff(): Boolean {
        return this == FFT || this == BUFFER
    }

    var label: String = "?"

    /**
     * Call this before toString to get a localizated string
     */
    fun localizate(cntx: Context) {
        label = cntx.getString(id)
    }

    override fun toString(): String {
        return label
    }
}

/**
 * Spinner to choose a mode
 */
class ModeSpinner(cntx: Context, attr: AttributeSet) : Spinner(cntx, attr) {
    private val customAdapter = ModeAdapter(cntx)

    init {
        adapter = customAdapter
    }

    /**
     * Removes a mode from the list of available modes
     */
    fun disableMode(mode: MODE) = customAdapter.removeMode(mode)

    /**
     * listener for when the mode changes
     */
    fun setOnModeChanged(l: (MODE) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                l(customAdapter.getItem(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                l(MODE.RAW)
            }
        }
    }
}

/**
 * for ModeSpinner, adapter for the items
 */
class ModeAdapter(cntx: Context) : ArrayAdapter<MODE>(cntx, android.R.layout.simple_list_item_1, emptyArray()) {

    // perform localization here
    val modes = MODE.values().toMutableList().apply { forEach { it.localizate(cntx) } }

    override fun getCount(): Int {
        return modes.size
    }

    override fun getItem(position: Int): MODE {
        return modes.get(position)
    }

    fun removeMode(mode: MODE) {
        modes.remove(mode)
    }
}