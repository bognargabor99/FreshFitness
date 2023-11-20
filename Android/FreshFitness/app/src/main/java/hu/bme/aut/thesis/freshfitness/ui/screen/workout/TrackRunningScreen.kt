package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import hu.bme.aut.thesis.freshfitness.service.TrackRunningService
import hu.bme.aut.thesis.freshfitness.setCustomMapIcon
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.viewmodel.TrackRunningViewModel
import java.util.Date

@Composable
fun TrackRunningScreen(
    viewModel: TrackRunningViewModel = viewModel(factory = TrackRunningViewModel.factory)
) {
    RequireLocationPermissions {
        val runs = viewModel.allRuns.observeAsState()
        var showRunOnMap by remember { mutableStateOf(false) }
        var shownCheckPoints by remember { mutableStateOf(listOf<RunCheckpointEntity>()) }

        LaunchedEffect(key1 = true) {
            viewModel.checkLocationState()
            viewModel.fetchRuns()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            Button(
                enabled = viewModel.locationSettingState,
                onClick = {
                    if (!TrackRunningService.isRunning) {
                        if (permissions.all { ContextCompat.checkSelfPermission(viewModel.context, it) == PackageManager.PERMISSION_GRANTED })
                            viewModel.startLocationTrackingService()
                    } else {
                        viewModel.stopLocationTrackingService()
                    }
                }) {
                Text(text = stringResource(if (!TrackRunningService.isRunning) R.string.start else R.string.stop))
            }

            if (runs.value?.isNotEmpty() == true) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.previous_runs),
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    }
                    items(viewModel.allRuns.value?.size!!) {index ->
                        viewModel.allRuns.value?.get(index)!!.run {
                            RunListItem(
                                run = this,
                                onClick = {
                                    shownCheckPoints = this.checkpoints
                                    showRunOnMap = true
                                          },
                                onDelete = { viewModel.deleteRun(this.run.id) }
                            )
                        }
                    }
                }
            }
        }
        if (showRunOnMap)
            TrackedRunBottomSheet(
                checkpoints = shownCheckPoints,
                onDismiss = { showRunOnMap = false }
            )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RunListItem(
    run: RunWithCheckpoints,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(6.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(modifier = Modifier.fillMaxHeight(), imageVector = Icons.Default.DirectionsRun, contentDescription = null)
            Text(
                text = SimpleDateFormat("yyy.MM.dd HH").format(Date(run.run.startTime))+"h"
            )
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete_run))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackedRunBottomSheet(
    checkpoints: List<RunCheckpointEntity>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.6f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
    ) {
        TrackedRunMap(checkpoints = checkpoints)
    }
}

@Composable
fun TrackedRunMap(checkpoints: List<RunCheckpointEntity>) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(checkpoints.first().run { LatLng(this.latitude, this.longitude) }, 15f)
    }
    Box(
        modifier = Modifier
            .requiredHeight(300.dp)
            .clip(RoundedCornerShape(14.dp))
            .padding(6.dp)
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp))
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(compassEnabled = true, zoomControlsEnabled = false, mapToolbarEnabled = false)
        ) {
            Marker(
                state = MarkerState(checkpoints.map { LatLng(it.latitude, it.longitude) }.first()),
                icon = setCustomMapIcon(stringResource(R.string.start)),
                zIndex = 50f
            )
            Polyline(
                points = checkpoints.map { LatLng(it.latitude, it.longitude) },
                color = Color(0, 150, 255),
                jointType = JointType.DEFAULT,
                pattern = listOf(Dash(20f), Gap(8f)),
                startCap = RoundCap(),
                endCap = RoundCap(),
                width = 6f
            )
            Marker(
                state = MarkerState(checkpoints.map { LatLng(it.latitude, it.longitude) }.last()),
                icon = setCustomMapIcon(stringResource(R.string.end)),
                zIndex = 49f
            )
        }
    }
}