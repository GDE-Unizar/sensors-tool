package com.unizar.practica

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.unizar.practica.fragment.*
import com.unizar.practica.tools.testPermission
import com.unizar.practica.utilities.initLongTap
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity
 */
class MainActivity : Activity() {

    // fragments
    val acc by lazy { Accelerometer(this) }
    val mic by lazy { Microphone(this) }
    val spk by lazy { Speaker(this) }
    val vib by lazy { Vibrator(this) }

    val fragments by lazy {
        sequenceOf(
                Triple(acc_head, acc_box, acc),
                Triple(mic_head, mic_box, mic),
                Triple(spk_head, spk_box, spk),
                Triple(vib_head, vib_box, vib)
        )
    }

    val exp by lazy { Experiments(acc, mic, spk, vib, this) }

    /**
     * When the activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for ((_, _, fragment) in fragments) fragment.onCreate()
        initHideable()

        exp.onCreate()

        s_info.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        initLongTap(acc_clr, acc_rec, acc_snap, mic_clr, mic_rec, mic_snap)
        testPermission(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.add("Help")?.setIcon(android.R.drawable.ic_menu_help)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        startActivity(Intent(this, HelpActivity::class.java))
        return false
    }

    /**
     * The activity is resumed
     */
    override fun onResume() {
        super.onResume()

        // resume visible fragments
        for ((_, box, fragment) in fragments)
            if (box.visibility == View.VISIBLE)
                fragment.onResume()
    }

    /**
     * The activity is paused
     */
    override fun onPause() {
        super.onPause()

        // pause all fragments
        for ((_, _, fragment) in fragments) fragment.onPause()
    }

    /**
     * Request for permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.any { it != PackageManager.PERMISSION_DENIED })
            testPermission(this)
    }


    /**
     * A key was pressed:
     * - Pressing the back button will close all fragment modules and cancel close
     * - Pressing the back button will stop running experiments and cancel close
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // back key
            var done = false

            // when at least a fragment is shown, close and don't exit
            if (fragments.any { it.second.visibility == View.VISIBLE }) {
                hideAll()
                done = true
            }

            // if an experiment is running, stop and don't close
            if (exp.isRunning()) {
                exp.stop()
                done = true
            }

            if (done) return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Initialize the hideable parts
     */
    private fun initHideable() {
        for ((head, box, fragment) in fragments) {

            // click header to toggle
            head.setOnClickListener {
                box.visibility = if (box.visibility == View.VISIBLE) {
                    // hide
                    fragment.onPause()
                    View.GONE
                } else {
                    // show
                    fragment.onResume()
                    View.VISIBLE
                }
            }

            // start hided
            box.visibility = View.GONE
        }
    }

    fun showAll() = fragments.forEach { (head, box, _) -> if (box.visibility != View.VISIBLE) head.performClick() }
    fun hideAll() = fragments.forEach { (head, box, _) -> if (box.visibility == View.VISIBLE) head.performClick() }
}


