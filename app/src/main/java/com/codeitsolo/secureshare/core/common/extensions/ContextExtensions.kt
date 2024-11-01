package com.codeitsolo.secureshare.core.common.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Open the app's settings.
 */
fun Context.openSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName")
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}