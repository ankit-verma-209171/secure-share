@file:OptIn(ExperimentalPermissionsApi::class)

package com.codeitsolo.secureshare.core.common.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Get the app's permission state.
 *
 * @param permissions The list of permissions to consider.
 *
 * @return The app's permission state for the given permissions.
 */
@Composable
fun rememberAppPermissionState(
    permissions: List<String>
): AppPermissionState {
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberMultiplePermissionsState(permissions)
    var appPermissionState by remember {
        mutableStateOf(
            AppPermissionState(
                isPermissionGranted = permissionState.allPermissionsGranted,
                shouldShowRational = permissionState.shouldShowRationale,
                permissionState = permissionState
            )
        )
    }

    LaunchedEffect(permissionState) {
        appPermissionState = appPermissionState.copy(
            isPermissionGranted = permissionState.allPermissionsGranted,
            shouldShowRational = permissionState.shouldShowRationale,
            shouldShowSettings = shouldShowSettings(
                permissionState = permissionState,
                appPermissionState = appPermissionState
            ),
            permissionState = permissionState
        )
    }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    appPermissionState = appPermissionState.copy(
                        isPermissionGranted = permissionState.allPermissionsGranted,
                        shouldShowRational = permissionState.shouldShowRationale,
                        shouldShowSettings = shouldShowSettings(
                            permissionState = permissionState,
                            appPermissionState = appPermissionState
                        )
                    )
                }

                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return appPermissionState
}

/**
 * Whether the app should show the settings.
 *
 * @param permissionState The permission state.
 * @param appPermissionState The app permission state.
 *
 * @return Whether the app should show the settings.
 */
private fun shouldShowSettings(
    permissionState: MultiplePermissionsState,
    appPermissionState: AppPermissionState
): Boolean = !permissionState.allPermissionsGranted &&
    !permissionState.shouldShowRationale &&
    appPermissionState.isPermissionRequested
