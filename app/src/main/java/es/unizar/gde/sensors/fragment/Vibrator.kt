package es.unizar.gde.sensors.fragment

import android.content.Context
import android.os.Vibrator
import es.unizar.gde.sensors.MainActivity
import es.unizar.gde.sensors.utilities.Fragment
import es.unizar.gde.sensors.utilities.onCheckedChange

/**
 * Makes the device vibrate
 */
class Vibrator(
        val cntx: MainActivity,
) : Fragment {
    private val views = cntx.views

    // variables
    private lateinit var vibrator: Vibrator

    private val vibconfig = longArrayOf(0, Integer.MAX_VALUE.toLong())

    /**
     * Initialization
     */
    override fun onCreate() {
        // service
        vibrator = cntx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // toggle to start/stop
        views.vibTog.onCheckedChange { setState(it) }
    }

    /**
     * Update ui (text)
     */
    private fun setState(state: Boolean) {

        // start/stop
        if (state) vibrator.vibrate(vibconfig, 0)
        else vibrator.cancel()
    }


    /**
     * On pause, stop
     */
    override fun onPause() {
        views.vibTog.isChecked = false
    }
}

