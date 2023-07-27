package es.unizar.gde.sensors.tools

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.v4.provider.DocumentFile
import android.widget.Toast
import es.unizar.gde.sensors.R
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

private const val FOLDER_KEY = "saveFolder"
private fun Context.getSharedPrefs() = getSharedPreferences("FileWriter", Context.MODE_PRIVATE)

fun getSaveFolder(cntx: Context) = DocumentFile.fromTreeUri(cntx, Uri.parse(cntx.getSharedPrefs().getString(FOLDER_KEY, "PracFis")!!))
fun setSaveFolder(folder: String, cntx: Context) = cntx.getSharedPrefs().edit().putString(FOLDER_KEY, folder).commit()
fun canCreateFiles(cntx: Context) = getSaveFolder(cntx).canWrite()


/**
 * Manages writting to a file
 */
class FileWriter(
    val cntx: Activity,
    val suffix: String = "",
) {

    // data
    var stream: OutputStream? = null
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

        // creates and opens a new file stream
        filename = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date()) + ((if (suffix.isEmpty()) "" else "_$suffix") + (if (subsuffix.isEmpty()) "" else "_$subsuffix") + ".txt")

        stream = cntx.contentResolver.openOutputStream(getSaveFolder(cntx).createFile("text/*", filename).uri, "w")

        if (toast)
            Toast.makeText(cntx, "Writing file...", Toast.LENGTH_SHORT).show()
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