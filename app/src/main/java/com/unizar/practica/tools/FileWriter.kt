package com.unizar.practica.tools

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.unizar.practica.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manages writting to a file
 */
class FileWriter(
        val cntx: Activity,
        val suffix: String = "",
) {

    // utils
    var stream: FileOutputStream? = null

    /**
     * Opens a new file to start recording
     */
    fun openNew(subsuffix: String = "") {
        if (!hasPermission) {
            Toast.makeText(cntx, R.string.no_permission, Toast.LENGTH_SHORT).show()
            return
        }

        // close previous if any
        close()
        // opens the file by creating the parent folder (if required)
        with(File(Environment.getExternalStorageDirectory(), "PracFis")) {
            if (!exists() && !mkdirs()) {
                // folder error
                Toast.makeText(cntx, R.string.toast_nofolder, Toast.LENGTH_SHORT).show()
            } else {
                // folder ready. open file
                var filesuffix = (if (suffix.isEmpty()) "" else "_$suffix") + (if (subsuffix.isEmpty()) "" else "_$subsuffix") + ".txt"
                stream = FileOutputStream(File(this, SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date()) + filesuffix))
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
            Toast.makeText(cntx, R.string.toast_saved, Toast.LENGTH_SHORT).show()
        }
        stream = null
    }

}

// --------- permision ------------

var hasPermission = false

/**
 * Checks if the permission is granted, if it isn't asks for it
 */
fun testPermission(cntx: Activity) {
    hasPermission =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true // old android version
            else if (cntx.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) true // already granted
            else false.also { cntx.requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0) } // not granted, ask

}