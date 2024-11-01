package com.codeitsolo.secureshare.feature.home.permissions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.codeitsolo.secureshare.core.common.permission.AppPermissionState


/**
 * Represents the media permission access levels.
 */
@Stable
@Immutable
enum class MediaPermissionAccessLevel {
    /**
     * No media permission given
     */
    None,

    /**
     * Partial Media Access permission given (applicable for Android 13+)
     */
    Partial,

    /**
     * Full Media Access permission given
     */
    Full,
}

/**
 * Get the media permission level.
 *
 * @return The media permission level
 */
fun AppPermissionState.getMediaPermissionLevel(): MediaPermissionAccessLevel = when {
    isPermissionGranted -> {
        MediaPermissionAccessLevel.Full
    }

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkPermissionsGranted(READ_MEDIA_IMAGES) -> {
        MediaPermissionAccessLevel.Full
    }

    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            checkPermissionsGranted(READ_MEDIA_VISUAL_USER_SELECTED) -> {
        MediaPermissionAccessLevel.Partial
    }

    checkPermissionsGranted(READ_EXTERNAL_STORAGE) -> {
        MediaPermissionAccessLevel.Full
    }

    else -> {
        MediaPermissionAccessLevel.None
    }
}
