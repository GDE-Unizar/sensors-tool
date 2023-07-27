package es.unizar.gde.sensors.tools

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.MenuItem
import es.unizar.gde.sensors.R

// --------- permision ------------

var hasWritePermission = false
var hasRecordPermission = false

const val REQUEST_CODE = 0


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

    if (canCreateFiles(this))
        hasWritePermission = true

    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        hasRecordPermission = true


    if ((!hasWritePermission || !hasRecordPermission) && ask) {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_permissions)
            .setMessage(getString(R.string.text_permissions).trimIndent())
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (!hasRecordPermission) requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)

                if (!hasWritePermission) startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_CODE)

            }.setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    permissionsMenu?.isVisible = !hasWritePermission || !hasRecordPermission
}