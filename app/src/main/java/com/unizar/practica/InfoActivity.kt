package com.unizar.practica

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_info.*

private val PADDING = 25

class InfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        for (sensor in sm.getSensorList(Sensor.TYPE_ALL)) {

            val header = TextView(this).apply {

                // set properties
                text = sensor.toString()
                setPadding(0, PADDING, 0, 0)

                // add
                container.addView(this)
            }

            val info = TextView(this).apply {
                for (method in sensor::class.java.methods) {

                    // skip elements that...
                    if (
                            method.equals(sensor::class.java.getDeclaredMethod("toString")) // is toString (used in header)
                            || method.returnType.equals(Void.TYPE) // return void
                            || method.typeParameters.isNotEmpty() // has parameters
                            || method.genericParameterTypes.isNotEmpty() // has type parameters
                    ) continue

                    // append function and result
                    append("${method.name}() ")
                    try {
                        append("=> ${method.invoke(sensor)}")
                    } catch (a: Throwable) {
                        append("[$a]")
                    }
                    append("\n")
                }

                // set properties
                visibility = View.GONE
                setPadding(PADDING, 0, PADDING, 0)

                // add
                container.addView(this)
            }

            // click functionality
            val listener: (View) -> Unit = { info.run { visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE } }
            header.setOnClickListener(listener)
            info.setOnClickListener(listener)
        }
    }
}