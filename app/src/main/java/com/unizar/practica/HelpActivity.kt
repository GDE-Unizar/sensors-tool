package com.unizar.practica

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View

/**
 * Displays a dialog with help for the long-tap feature
 */
fun Context.showGeneralHelp() {
    AlertDialog.Builder(this)
            .setTitle(R.string.menu_help)
            .setMessage(R.string.h_general)
            .show()
}

/**
 * Prepares the views for the show-help-on-long-tap feature
 */
fun Context.helpOnLongTap(vararg views: View) {
    val helplistener = View.OnLongClickListener { view ->

        val text = try {
            getString(resources.getIdentifier("h_" + view.getStringId(), "string", packageName))
        } catch (e: Resources.NotFoundException) {
            Log.d("NOHELP", view.toString())
            getString(R.string.h_nohelp)
        }.trimIndent()

        AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage(text)
                .show()

        true
    }

    views.forEach {
        it.setOnLongClickListener(helplistener)
    }
}

/**
 * Returns the string for the help identifier. The tag if present, or the view name.
 */
fun View.getStringId() =
        when {
            tag != null -> tag.toString()
            id == View.NO_ID -> throw Resources.NotFoundException()
            else -> resources.getResourceEntryName(id)
        }


//private val PADDING = 15
//
//class HelpActivity : Activity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_help)
//
//        initialize()
//    }
//
//    private fun initialize() {
//        val tokenizer = Tokenizer(this)
//
//        container.enableBetterTransitions()
//        parseLine(tokenizer, container)
//    }
//
//    private fun parseLine(lines: Tokenizer, parent: ViewGroup) {
//        lateinit var lastChild: TextView
//        var lastPadding = -1
//        while (true) {
//            // exit if no more lines
//            if (!lines.hasNext()) return
//
//            when {
//                lastPadding == -1 || lines.padding == lastPadding -> {
//                    lastChild = newText(lines.text, lines.padding, parent)
//                    lastPadding = lines.padding
//                    lines.parseNext()
//                }
//                lines.padding > lastPadding -> parseLine(lines, newContainer(parent, lastChild))
//                lines.padding < lastPadding -> return
//            }
//        }
//    }
//
//    private fun newText(label: String, padding: Int, parent: ViewGroup): TextView {
//        return (layoutInflater.inflate(R.layout.label_help, parent, false) as TextView).apply {
//
//            text = label
//            setPadding(padding * PADDING, 0, padding * PADDING, 0)
//
//            parent.addView(this)
//        }
//    }
//
//    private fun newContainer(parent: ViewGroup, opener: TextView): ViewGroup {
//        val ll = (layoutInflater.inflate(R.layout.box_help, parent, false) as LinearLayout).apply {
//
//
//            enableBetterTransitions()
//            parent.addView(this)
//        }
//
//        opener.setOnClickListener {
//            ll.visibility = if (ll.visibility == View.VISIBLE) {
//                opener.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_collapsed, 0, 0, 0)
//                View.GONE
//            } else {
//                opener.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expanded, 0, 0, 0)
//                View.VISIBLE
//            }
//        }
//        opener.performClick()
//
//        return ll
//    }
//
//}
//
//private fun LinearLayout.enableBetterTransitions() {
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//    }
//}
//
//class Tokenizer(cntx: Context) {
//    lateinit var text: String
//    var padding: Int = 0
//
//    private val lines = cntx.resources.openRawResource(R.raw.help).bufferedReader().readText().split(Regex("\\n\\s*\\n"))
//    private var index = -1
//
//    init {
//        parseNext()
//    }
//
//    fun parseNext() {
//        index++
//        if (!hasNext()) return
//
//        text = lines[index]
//        padding = text.length
//        text = text.trimStart()
//        padding -= text.length
//
//        Log.d("PARSE", "$padding - $text")
//    }
//
//    fun hasNext() = index < lines.size
//
//}