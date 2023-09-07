package es.unizar.gde.sensors.tools

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.widget.Toast
import es.unizar.gde.sensors.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages writing to a file
 */
class FileWriter(
    private val cntx: Activity,
    private val suffix: String = "",
) {

    // data
    private var stream: OutputStream? = null
    private var currentFilename: String? = null

    /**
     * Opens a new file to start recording
     */
    fun openNew(subsuffix: String = "", toast: Boolean = true) {
        // close previous if any
        close()

        // check permissions
        if (!hasWritePermission) {
            Toast.makeText(cntx, R.string.toast_filepermissions, Toast.LENGTH_SHORT).show()
            return
        }

        // open file for writing
        runCatching {
            val fileName = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(Date()) + suffix.toSuffix + subsuffix.toSuffix + ".txt"
            stream = FileOutputStream(getOutputFolder(cntx).apply { mkdirs() } / fileName)
            currentFilename = fileName
        }.onFailure {
            Toast.makeText(cntx, R.string.toast_fileerror, Toast.LENGTH_SHORT).show()
            return
        }

        if (toast)
            Toast.makeText(cntx, R.string.toast_writingfile, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(cntx, cntx.getString(R.string.toast_saved, currentFilename), Toast.LENGTH_LONG).show()
        }
        stream = null
        currentFilename = null
    }

}

/**
 * Get the output folder to save files
 */
private fun getOutputFolder(cntx: Context) = File(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val storageManager = cntx.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        storageManager.primaryStorageVolume.directory
    } else {
        Environment.getExternalStorageDirectory()
    }?.absolutePath ?: "/"
) / Environment.DIRECTORY_DOWNLOADS / "PracFis"

/**
 * (Folder / filename) looks better
 */
private operator fun File.div(child: String) = File(this, child)

/**
 * Prepend a '_' to the string, unless blank
 */
private val String.toSuffix
    get() = if (isBlank()) "" else "_$this"