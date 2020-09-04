package es.unizar.gde.sensors

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import es.unizar.gde.sensors.fragment.*
import es.unizar.gde.sensors.tools.permissionsMenu
import es.unizar.gde.sensors.tools.testPermission
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

//        initLongTap(acc_clr, acc_rec, acc_snap, mic_clr, mic_rec, mic_snap)
        testPermission(true)

        helpOnLongTap(acc_head, acc_txt, acc_mode, acc_clr, acc_rec, acc_snap, acc_graph, mic_head, mic_txt, mic_mode, mic_clr, mic_rec, mic_snap, mic_graph, spk_head, spk_toggle, vib_head, vib_tog, s_info)
    }

    // --- menu ------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        permissionsMenu = menu?.findItem(R.id.permissions)
        testPermission()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                // show help
                showMainActivityHelp()
                true
            }
            R.id.folder -> {
                // show folder info
                showFolder()
                true
            }
            R.id.permissions -> {
                // recheck permissions
                testPermission(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        testPermission()
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
                    head.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_collapsed, 0, R.drawable.ic_collapsed_r, 0)
                    View.GONE
                } else {
                    // show
                    fragment.onResume()
                    head.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expanded, 0, R.drawable.ic_expanded, 0)
                    View.VISIBLE
                }
            }

            // start hided
            head.performClick()
        }
    }

    fun showAll() = fragments.forEach { (head, box, _) -> if (box.visibility != View.VISIBLE) head.performClick() }
    fun hideAll() = fragments.forEach { (head, box, _) -> if (box.visibility == View.VISIBLE) head.performClick() }
}


