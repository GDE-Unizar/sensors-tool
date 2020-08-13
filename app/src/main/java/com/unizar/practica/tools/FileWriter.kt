package com.unizar.practica.tools

import android.app.Activity
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FileWriter(
        val cntx: Activity,
) {

    var stream: FileOutputStream? = null

    fun openNew() {
        close()

        with(File(Environment.getExternalStorageDirectory(), "PracFis")) {
            if (!exists() && !mkdirs()) {
                // folder error
                Toast.makeText(cntx, "Can't create folder", Toast.LENGTH_SHORT).show()
                stream = null
            } else {
                // folder ready
                stream = FileOutputStream(File(this, SimpleDateFormat("yyyy_MM_dd_HH_mm_ss'.txt'").format(Date())))
            }
        }
    }

    fun writeLine(line: String) {
        stream?.apply {
            write(line.toByteArray())
            write("\n".toByteArray())
        }
    }

    fun close() {
        stream?.run {
            close()
            Toast.makeText(cntx, "File saved", Toast.LENGTH_SHORT).show()
        }
        stream = null
    }

}