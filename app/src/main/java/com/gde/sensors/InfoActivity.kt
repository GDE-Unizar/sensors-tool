package com.gde.sensors

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_info.*

/**
 * Padding to apply to subelements
 */
private val PADDING = 25

/**
 * Activity that shows info about all available sensors
 */
class InfoActivity : Activity() {

    /**
     * When the activity starts
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // foreach sensor...
        for (sensor in sm.getSensorList(Sensor.TYPE_ALL)) {

            // add header with its toString description
            val header = TextView(this).apply {

                // set properties
                text = sensor.toString()
                setPadding(0, PADDING, 0, 0)

                // add
                s_info.addView(this)
            }

            // add body with the sensor data
            val info = TextView(this).apply {

                // for each method...
                for (method in sensor::class.java.methods) {

                    // skip those that...
                    if (
                            method.equals(sensor::class.java.getDeclaredMethod("toString")) // is toString (used in header)
                            || method.returnType.equals(Void.TYPE) // return void
                            || method.typeParameters.isNotEmpty() // has parameters
                            || method.genericParameterTypes.isNotEmpty() // has type parameters
                    ) continue

                    // append name and result
                    append(method.name.run { if (startsWith("get")) substring(3) else if (startsWith("is")) substring(2) else this })
                    try {
                        append(" = ${method.invoke(sensor)}")
                    } catch (a: Throwable) {
                        append(" [$a]")
                    }
                    append("\n")
                }

                // set properties
                visibility = View.GONE
                setPadding(PADDING, 0, PADDING, 0)

                // add
                s_info.addView(this)
            }

            // toggle functionality
            val listener: (View) -> Unit = { info.run { visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE } }
            header.setOnClickListener(listener)
            info.setOnClickListener(listener)
        }
    }

    //TODO: menu with the help dialog
}