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
    init {
        adapter = ArrayAdapter(cntx, android.R.layout.simple_list_item_1, MODE.values())
    }

    fun setOnModeChanged(l: (MODE) -> Unit) {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                l(MODE.values()[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                l(MODE.RAW)
            }
        }
    }
}