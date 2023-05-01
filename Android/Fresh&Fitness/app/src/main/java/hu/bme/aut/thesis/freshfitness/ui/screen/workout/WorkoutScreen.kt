package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun WorkoutScreen() {
    MultiplePermissions()
}

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissions() {
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

    Column {
        permissionStates.permissions.forEach {
            when (it.permission) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    when {
                        it.status.isGranted -> {
                            /* Permission has been granted by the user.
                               You can use this permission to now acquire the location of the device.
                               You can perform some other tasks here.
                            */
                            Text(text = "Coarse location permission has been granted")
                        }

                        it.status.shouldShowRationale -> {
                            /* Happens if a user denies the permission two times */
                            Text(text = "Coarse location permission is needed")
                        }

                        !it.status.isGranted && !it.status.shouldShowRationale -> {
                            /* If the permission is denied and the should not show rationale
                                You can only allow the permission manually through app settings
                             */
                            Text(text = "Navigate to settings and enable the Coarse location permission")

                        }
                    }
                }

                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    when {
                        it.status.isGranted -> {
                            /* Permission has been granted by the user.
                               You can use this permission to now acquire the location of the device.
                               You can perform some other tasks here.
                            */
                            Text(text = "Fine location permission has been granted")
                        }

                        it.status.shouldShowRationale -> {
                            /*Happens if a user denies the permission two times

                             */
                            Text(text = "Fine location permission is needed")

                        }

                        !it.status.isGranted && !it.status.shouldShowRationale -> {
                            /* If the permission is denied and the should not show rationale
                                You can only allow the permission manually through app settings
                             */
                            Text(text = "Navigate to settings and enable the Fine location permission")

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowVac() {
    val vac = LatLng(47.78703,19.1207415)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vac, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,

        ) {
        Marker(
            state = MarkerState(position = vac),
            title = "Singapore",
            snippet = "Marker in Vac"
        )
    }
}