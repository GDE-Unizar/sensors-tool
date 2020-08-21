package com.unizar.practica.tools

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

enum class MODE(val label: String) {
    RAW("raw"),
    BASE("base"),
    AVERAGE("avg"),
    RANGE("range"),
    BUFFER("buff"),
    FFT("fft"),
    ;

    override fun toString(): String {
        return label
    }
}

class ModeSpinner(cntx: Context, attr: AttributeSet) : Spinner(cntx, attr) {
    private val customAdapter = ModeAdapter(cntx)

    init {
        adapter = customAdapter
    }

    fun disableMode(mode: MODE) = customAdapter.removeMode(mode)


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

class ModeAdapter(cntx: Context) : ArrayAdapter<MODE>(cntx, android.R.layout.simple_list_item_1, emptyArray()) {

    val modes = MODE.values().toMutableList()

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