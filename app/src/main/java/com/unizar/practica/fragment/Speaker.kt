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
            tune.run { if (it) play() else stop() }
        }

        cntx.spk_hz.onProgressChange {
            tune.freq = it.toDouble()
            cntx.spk_toggle.text = "$it Hz"
        }
        cntx.spk_hz.progress = 440

        cntx.spk_inc.setOnClickListener { cntx.spk_hz.progress += 10 }
        cntx.spk_dec.setOnClickListener { cntx.spk_hz.progress -= 10 }
    }

    override fun onPause() {
        cntx.spk_toggle.isChecked = false
    }

}

