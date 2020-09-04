package es.unizar.gde.sensors.tools

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.view.MenuItem
import es.unizar.gde.sensors.R

// --------- permision ------------

var hasWritePermission = false
var hasRecordPermission = false

private val PERMISSIONS_CODE = 0


var permissionsMenu: MenuItem? = null

/**
 * Checks if the permission is granted, if it isn't asks for it
 */
fun Activity.testPermission(ask: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        hasWritePermission = true
        hasRecordPermission = true
        permissionsMenu?.isVisible = false
        return
    }

    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        hasWritePermission = true

    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        hasRecordPermission = true

    val needed = arrayOf(hasWritePermission to android.Manifest.permission.WRITE_EXTERNAL_STORAGE, hasRecordPermission to android.Manifest.permission.RECORD_AUDIO).filter { !it.first }.map { it.second }


    if (needed.isNotEmpty() && ask) {
        AlertDialog.Builder(this)
                .setTitle(R.string.title_permissions)
                .setMessage(getString(R.string.text_permissions).trimIndent())
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
                    requestPermissions(needed.toTypedArray(), PERMISSIONS_CODE)
                }).setNegativeButton(android.R.string.cancel, null)
                .show()

    }

    permissionsMenu?.isVisible = !hasWritePermission || !hasRecordPermission
}