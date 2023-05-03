package hu.bme.aut.thesis.freshfitness.ui.util

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequireLocationPermissions(
    onGranted: @Composable () -> Unit
) {
    val permissionStates =
        rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionStates.launchMultiplePermissionRequest()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    permissionStates.permissions.forEach {
        when (it.permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                when {
                    it.status.isGranted -> {
                        onGranted()
                    }
                    it.status.shouldShowRationale -> {
                        /* Happens if a user denies the permission two times */
                    }
                    !it.status.isGranted && !it.status.shouldShowRationale -> {
                        /* If the permission is denied and the should not show rationale */
                    }
                }
            }
        }
    }
}