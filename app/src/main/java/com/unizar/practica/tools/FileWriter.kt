package com.unizar.practica.tools

import android.app.Activity
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manages writting to a file
 */
class FileWriter(
        val cntx: Activity,
        val suffix: String,
) {

    // utils
    var stream: FileOutputStream? = null

    /**
     * Opens a new file to start recording
     */
    fun openNew() {
        // close previous if any
        close()

        // opens the file by creating the parent folder (if required)
        with(File(Environment.getExternalStorageDirectory(), "PracFis")) {
            if (!exists() && !mkdirs()) {
                // folder error
                Toast.makeText(cntx, "Can't create folder", Toast.LENGTH_SHORT).show()
            } else {
                // folder ready. open file
                stream = FileOutputStream(File(this, SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_'$suffix.txt'").format(Date())))
            }
        }
    }

    /**
     * Writes the line to the file (does nothing if not opened)
     */
    fun writeLine(line: Any) {
        stream?.apply {
            write(line.toString().toByteArray())
            write("\n".toByteArray())
        }
    }

    /**
     * Closes the file
     */
    fun close() {
        stream?.run {
            close()
            Toast.makeText(cntx, "File saved", Toast.LENGTH_SHORT).show()
        }
        stream = null
    }

}