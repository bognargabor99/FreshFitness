package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.thesis.freshfitness.viewmodel.TrackRunningViewModel

@Composable
fun TrackRunningScreen(
    viewModel: TrackRunningViewModel = viewModel(factory = TrackRunningViewModel.factory)
) {
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            viewModel.startLocationUpdates()
            Toast.makeText(viewModel.context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(viewModel.context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        Button(
            enabled = !viewModel.isTracking,
            onClick = {
            if (permissions.all {
                    ContextCompat.checkSelfPermission(
                        viewModel.context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                // Get the location
                viewModel.startLocationUpdates()
            } else {
                launcherMultiplePermissions.launch(permissions)
            }
        }) {
            Text(text = "Start")
        }
        Button(
            enabled = viewModel.isTracking,
            onClick = { viewModel.stopLocationUpdates() }
        ) {
            Text(text = "Stop")
        }

        Text(text = "Latitude : " + viewModel.currentLocation.latitude)
        Text(text = "Longitude : " + viewModel.currentLocation.longitude)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(viewModel.currentLocation.latitude, viewModel.currentLocation.longitude), 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            Marker(
                state = MarkerState(position = LatLng(viewModel.currentLocation.latitude, viewModel.currentLocation.longitude)),
                title = "Your are here",
                snippet = "You are here",

            )
        }
    }
}