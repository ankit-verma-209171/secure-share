package com.codeitsolo.secureshare.feature.home

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeitsolo.secureshare.core.common.extensions.openSettings
import com.codeitsolo.secureshare.core.common.permission.AppPermissionState
import com.codeitsolo.secureshare.core.common.permission.rememberAppPermissionState

/**
 * Home route to show home screen.
 *
 * @param modifier The Modifier to be applied to this composable
 */
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier
) {
    val appPermissionState =
        rememberAppPermissionState(
            buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    add(READ_MEDIA_VISUAL_USER_SELECTED)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(READ_MEDIA_IMAGES)
                } else {
                    add(READ_EXTERNAL_STORAGE)
                }
            }
        )

    var mediaPermissionAccessLevel by rememberSaveable(Unit) {
        mutableStateOf(appPermissionState.getMediaPermissionLevel())
    }

    val showManageMediaReselection = remember(mediaPermissionAccessLevel) {
        mediaPermissionAccessLevel == MediaPermissionAccessLevel.Partial
    }

    val allPermissionsGranted: Boolean by remember(appPermissionState) {
        val currentMediaPermissionLevel = appPermissionState.getMediaPermissionLevel()
        mutableStateOf(currentMediaPermissionLevel != MediaPermissionAccessLevel.None)
    }

//    val images = viewModel.mediaStoreImagesState.collectAsLazyPagingItems() // TODO: Get images
    val refresh = remember(Unit) {
        {
//            images.refresh() // TODO: Update
        }
    }

    fun refreshImages() {
        val previousMediaPermissionAccessLevel = mediaPermissionAccessLevel
        val currentMediaPermissionAccessLevel = appPermissionState.getMediaPermissionLevel()
        mediaPermissionAccessLevel = currentMediaPermissionAccessLevel

        when {
            currentMediaPermissionAccessLevel == MediaPermissionAccessLevel.None -> Unit
            previousMediaPermissionAccessLevel != currentMediaPermissionAccessLevel -> refresh()
            previousMediaPermissionAccessLevel == MediaPermissionAccessLevel.Partial -> refresh()
        }
    }

    LifecycleResumeEffect(Unit) {
        refreshImages()
        onPauseOrDispose { }
    }

    val context = LocalContext.current

    if (allPermissionsGranted) {
        HomeScreen(
            modifier = modifier,
            showManageMediaReselection = showManageMediaReselection,
        )
    } else {
        MissingMediaPermissionsScreen(
            modifier = modifier,
            shouldShowOpenSettingsOption = appPermissionState.shouldShowSettings,
            requestPermissions = { appPermissionState.requestPermissions() },
            gotoSettings = { context.openSettings() }
        )
    }
}

/**
 * A standalone screen to show the home screen.
 *
 * @param modifier The modifier needed to be applied to the composable
 */
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    showManageMediaReselection: Boolean,
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Home ... Show manage option?? $showManageMediaReselection")
    }
}

/**
 * A standalone screen to show the missing permissions screen.
 *
 * @param modifier The modifier needed to be applied to the composable
 * @param shouldShowOpenSettingsOption Whether to show the open settings option
 * @param requestPermissions Callback for request permissions click
 * @param gotoSettings Callback for navigate to settings click
 */
@Composable
private fun MissingMediaPermissionsScreen(
    modifier: Modifier = Modifier,
    shouldShowOpenSettingsOption: Boolean,
    requestPermissions: () -> Unit,
    gotoSettings: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Missing Media Permissions")
        if (shouldShowOpenSettingsOption) {
            Button(onClick = gotoSettings) {
                Text(text = "Open Settings")
            }
        } else {
            Button(onClick = requestPermissions) {
                Text(text = "Allow")
            }
        }
    }
}

/**
 * Represents the media permission access levels.
 */
@Stable
@Immutable
private enum class MediaPermissionAccessLevel {
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
private fun AppPermissionState.getMediaPermissionLevel(): MediaPermissionAccessLevel = when {
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
