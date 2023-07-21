package es.unizar.gde.sensors.tools

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.widget.Toast
import es.unizar.gde.sensors.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

val SAVE_FOLDER = File(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    } else {
        Environment.getExternalStorageDirectory()
    }, "PracFis"
)

/**
 * Manages writting to a file
 */
class FileWriter(
    val cntx: Activity,
    val suffix: String = "",
) {

    // data
    var stream: FileOutputStream? = null
    var filename: String? = null

    /**
     * Opens a new file to start recording
     */
    fun openNew(subsuffix: String = "", toast: Boolean = true) {
        if (!hasWritePermission) {
            Toast.makeText(cntx, R.string.toast_nopermission, Toast.LENGTH_SHORT).show()
            return
        }

        // close previous if any
        close()
        // opens the file by creating the parent folder (if required)
        with(SAVE_FOLDER) {
            if (!exists() && !mkdirs()) {
                // folder error
                Toast.makeText(cntx, R.string.toast_nofolder, Toast.LENGTH_SHORT).show()
            } else {
                // folder ready. open file
                filename = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date()) + ((if (suffix.isEmpty()) "" else "_$suffix") + (if (subsuffix.isEmpty()) "" else "_$subsuffix") + ".txt")
                stream = FileOutputStream(File(this, filename))

                if (toast)
                    Toast.makeText(cntx, "Writing file...", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(cntx, cntx.getString(R.string.toast_saved, filename), Toast.LENGTH_LONG).show()
        }
        stream = null
        filename = null
    }

}