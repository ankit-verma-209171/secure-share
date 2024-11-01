package com.codeitsolo.secureshare.core.common.permission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build

/**
 * Check if media permissions are granted.
 *
 * @return True if media permissions are granted, false otherwise.
 */
fun AppPermissionState.checkMediaPermissionsGranted(): Boolean = when {
    isPermissionGranted -> true

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        checkPermissionsGranted(READ_MEDIA_IMAGES) -> true

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
        checkPermissionsGranted(READ_MEDIA_VISUAL_USER_SELECTED) -> true

    checkPermissionsGranted(READ_EXTERNAL_STORAGE) -> true

    else -> false
}
