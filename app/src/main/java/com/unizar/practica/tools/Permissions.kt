package com.unizar.practica.tools

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

// --------- permision ------------

var hasWritePermission = false
var hasRecordPermission = false

/**
 * Checks if the permission is granted, if it isn't asks for it
 */
fun testPermission(cntx: Activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        hasWritePermission = true
        hasRecordPermission = true
        return
    }

    if (cntx.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        hasWritePermission = true

    if (cntx.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        hasRecordPermission = true

    val needed = arrayOf(hasWritePermission to android.Manifest.permission.WRITE_EXTERNAL_STORAGE, hasRecordPermission to android.Manifest.permission.RECORD_AUDIO).filter { !it.first }.map { it.second }


    if (!needed.isEmpty())
        cntx.requestPermissions(needed.toTypedArray(), 0)
}