package com.unizar.practica

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_help.*

private val PADDING = 15

class HelpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        initialize()
    }

    private fun initialize() {
        val tokenizer = Tokenizer(this)

        container.enableBetterTransitions()
        parseLine(tokenizer, container)
    }

    private fun parseLine(lines: Tokenizer, parent: ViewGroup) {
        lateinit var lastChild: TextView
        var lastPadding = -1
        while (true) {
            // exit if no more lines
            if (!lines.hasNext()) return

            when {
                lastPadding == -1 || lines.padding == lastPadding -> {
                    lastChild = newText(lines.text, lines.padding, parent)
                    lastPadding = lines.padding
                    lines.parseNext()
                }
                lines.padding > lastPadding -> parseLine(lines, newContainer(parent, lastChild))
                lines.padding < lastPadding -> return
            }
        }
    }

    private fun newText(label: String, padding: Int, parent: ViewGroup): TextView {
        return (layoutInflater.inflate(R.layout.label_help, parent, false) as TextView).apply {

            text = label
            setPadding(padding * PADDING, 0, padding * PADDING, 0)

            parent.addView(this)
        }
    }

    private fun newContainer(parent: ViewGroup, opener: TextView): ViewGroup {
        val ll = (layoutInflater.inflate(R.layout.box_help, parent, false) as LinearLayout).apply {


            enableBetterTransitions()
            parent.addView(this)
        }

        opener.setOnClickListener {
            ll.visibility = if (ll.visibility == View.VISIBLE) {
                opener.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_collapsed, 0, 0, 0)
                View.GONE
            } else {
                opener.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expanded, 0, 0, 0)
                View.VISIBLE
            }
        }
        opener.performClick()

        return ll
    }

}

private fun LinearLayout.enableBetterTransitions() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }
}

class Tokenizer(cntx: Context) {
    lateinit var text: String
    var padding: Int = 0

    private val lines = cntx.resources.openRawResource(R.raw.help).bufferedReader().readText().split(Regex("\\n\\s*\\n"))
    private var index = -1

    init {
        parseNext()
    }

    fun parseNext() {
        index++
        if (!hasNext()) return

        text = lines[index]
        padding = text.length
        text = text.trimStart()
        padding -= text.length

        Log.d("PARSE", "$padding - $text")
    }

    fun hasNext() = index < lines.size

}