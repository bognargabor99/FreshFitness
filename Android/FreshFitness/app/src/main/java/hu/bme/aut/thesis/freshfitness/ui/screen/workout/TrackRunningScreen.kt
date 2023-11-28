package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.icu.text.SimpleDateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import com.smarttoolfactory.screenshot.rememberScreenshotState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.calculateDistanceInMeters
import hu.bme.aut.thesis.freshfitness.calculateElapsedTime
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import hu.bme.aut.thesis.freshfitness.service.TrackRunningService
import hu.bme.aut.thesis.freshfitness.setCustomMapIcon
import hu.bme.aut.thesis.freshfitness.ui.util.OkCancelDialog
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.ui.util.UploadStateAlert
import hu.bme.aut.thesis.freshfitness.viewmodel.TrackRunningViewModel
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.round

@SuppressLint("SimpleDateFormat")
@Composable
fun TrackRunningScreen(
    viewModel: TrackRunningViewModel = viewModel(factory = TrackRunningViewModel.factory)
) {
    RequireLocationPermissions {
        val runs by viewModel.allRuns.observeAsState()
        var showRunOnMap by remember { mutableStateOf(false) }
        var shownRun: RunWithCheckpoints? by remember { mutableStateOf(null) }
        var showShareRunDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = false) {
            viewModel.getSession()
            viewModel.checkLocationState()
            viewModel.fetchRuns()
        }

        val context = LocalContext.current
        val screenshotState = rememberScreenshotState()
        val coroutineScope = rememberCoroutineScope()
        if (showRunOnMap) {
            Column {
                AnimatedVisibility(
                    visible = viewModel.runShared,
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it })
                ) {
                    SharedRunNotification()
                }
                TrackedRun(
                    run = shownRun!!,
                    screenshotState = screenshotState,
                    isLoggedIn = viewModel.isLoggedIn,
                    onShare = {
                        coroutineScope.launch {
                            screenshotState.capture()
                            showShareRunDialog = true
                        }
                    },
                    onDismiss = { showRunOnMap = false }
                )
            }
            if (showShareRunDialog) {
                val additionalShareText = shownRun?.let { getAdditionalShareText(it, context) } ?: "Can you bet me?"
                OkCancelDialog(
                    title = stringResource(R.string.sharing_run),
                    subTitle = stringResource(R.string.are_you_sure_to_share_run),
                    onDismiss = { showShareRunDialog = false },
                    onOk = {
                        screenshotState.bitmap?.let {
                            viewModel.shareRun(context, it, additionalShareText)
                        }
                        showShareRunDialog = false
                    }
                )
            }
            if (viewModel.showUploadState) {
                UploadStateAlert(text = stringResource(R.string.uploading_file), fractionCompleted = viewModel.uploadState) { }
            }
        }
        else {
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

                if (runs?.isNotEmpty() == true) {
                    LazyColumn(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.previous_runs),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize
                            )
                        }
                        items(viewModel.allRuns.value?.size!!) {index ->
                            viewModel.allRuns.value?.get(index)!!.run {
                                RunListItem(
                                    run = this,
                                    onClick = {
                                        shownRun = this
                                        showRunOnMap = true
                                    },
                                    onDelete = { viewModel.deleteRun(this.run.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getAdditionalShareText(run: RunWithCheckpoints, context: Context): String {
    return "I ran ${context.resources.getString(R.string.meters, round(run.checkpoints.calculateDistanceInMeters() * 100) / 100)} " +
            "in ${calculateElapsedTime(run.run.startTime, run.run.endTime).run { "${this / 60}:${this % 60}" }} minutes " +
            "on ${SimpleDateFormat("MM.dd").format(Date(run.run.startTime))}\n" +
            "Can you beat me?"
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

@Composable
fun TrackedRun(
    run: RunWithCheckpoints,
    screenshotState: ScreenshotState,
    isLoggedIn: Boolean,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    Column (
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.meters, round(run.checkpoints.calculateDistanceInMeters() * 100) / 100),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
            Text(
                text = calculateElapsedTime(run.run.startTime, run.run.endTime).run { "${this / 60}:${this % 60}" },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
        TrackedRunMap(checkpoints = run.checkpoints, screenshotState = screenshotState)
        if (isLoggedIn) {
            Button(onClick = onShare) {
                Text(text = stringResource(R.string.share))
            }
        }
    }
}

@Composable
fun TrackedRunMap(
    checkpoints: List<RunCheckpointEntity>,
    screenshotState: ScreenshotState,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(checkpoints.first().run { LatLng(this.latitude, this.longitude) }, 15f)
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .border(width = 1.dp, color = Color.LightGray)
    ) {
        ScreenshotBox(screenshotState = screenshotState) {
            GoogleMap(
                modifier = Modifier
                    .matchParentSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false
                )
            ) {
                Marker(
                    state = MarkerState(checkpoints.map { LatLng(it.latitude, it.longitude) }
                        .first()),
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
                    state = MarkerState(checkpoints.map { LatLng(it.latitude, it.longitude) }
                        .last()),
                    icon = setCustomMapIcon(stringResource(R.string.end)),
                    zIndex = 49f
                )
            }
        }
    }
}

@Composable
fun SharedRunNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green.copy(green = 0.5f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your run has been shared!\nCheck your social feed!",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp
        )
    }
}