@file:OptIn(ExperimentalPermissionsApi::class)

package com.codeitsolo.secureshare.core.common.permission

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted

/**
 * Represents the state of an app permissions.
 *
 * @param isPermissionGranted Whether the permission is granted.
 * @param shouldShowRational Whether the permission should show rational.
 * @param shouldShowSettings Whether the permission should show settings.
 */
class AppPermissionState(
    val isPermissionGranted: Boolean = false,
    val shouldShowRational: Boolean = false,
    val shouldShowSettings: Boolean = false,
    internal val permissionState: MultiplePermissionsState? = null,
) {

    var isPermissionRequested = false
        private set

    fun copy(
        isPermissionGranted: Boolean = this.isPermissionGranted,
        shouldShowRational: Boolean = this.shouldShowRational,
        shouldShowSettings: Boolean = this.shouldShowSettings,
        permissionState: MultiplePermissionsState? = this.permissionState,
    ): AppPermissionState = AppPermissionState(
        isPermissionGranted = isPermissionGranted,
        shouldShowRational = shouldShowRational,
        shouldShowSettings = shouldShowSettings,
        permissionState = permissionState
    ).apply {
        isPermissionRequested = this@AppPermissionState.isPermissionRequested
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppPermissionState) return false
        if (isPermissionGranted != other.isPermissionGranted) return false
        if (shouldShowRational != other.shouldShowRational) return false
        if (shouldShowSettings != other.shouldShowSettings) return false
        if (permissionState != other.permissionState) return false
        if (isPermissionRequested != other.isPermissionRequested) return false
        return true
    }

    override fun hashCode(): Int {
        var result = isPermissionGranted.hashCode()
        result = 31 * result + shouldShowRational.hashCode()
        result = 31 * result + shouldShowSettings.hashCode()
        result = 31 * result + permissionState.hashCode()
        result = 31 * result + isPermissionRequested.hashCode()
        return result
    }

    override fun toString(): String {
        return "AppPermissionState(" +
            "isPermissionGranted=$isPermissionGranted, " +
            "shouldShowRational=$shouldShowRational, " +
            "shouldShowSettings=$shouldShowSettings, " +
            "permissionState=$permissionState, " +
            "isPermissionRequested=$isPermissionRequested, " +
            ")"
    }

    fun checkPermissionsGranted(vararg permissions: String): Boolean {
        permissionState ?: return false
        val permissionsSet = permissions.toSet()
        val permissionsToCheck = permissionState
            .permissions
            .filter { it.permission in permissionsSet }

        if (permissionsToCheck.isEmpty()) return false
        return permissionsToCheck.all { it.status.isGranted }
    }

    fun requestPermissions() {
        isPermissionRequested = true
        permissionState?.launchMultiplePermissionRequest()
    }
}
