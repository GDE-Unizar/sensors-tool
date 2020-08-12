package com.unizar.practica.fragment

import com.unizar.practica.MainActivity
import com.unizar.practica.tools.Fragment
import com.unizar.practica.tools.Tune
import com.unizar.practica.tools.onCheckedChange
import com.unizar.practica.tools.onProgressChange
import kotlinx.android.synthetic.main.activity_main.*

class Speaker(
        val cntx: MainActivity
) : Fragment {

    val tune = Tune()

    override fun onCreate() {
        cntx.spk_toggle.onCheckedChange {
            tune.run { if (it) start() else stop() }
        }

        cntx.spk_progress.onProgressChange {
            tune.freq = it.toDouble()
            cntx.spk_toggle.text = "$it Hz"
        }
        cntx.spk_progress.progress = 440
    }

    override fun onResume() {}

    override fun onPause() {
        cntx.spk_toggle.isChecked = false
    }

}

