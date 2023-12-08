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
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.collectAsState
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
import com.google.android.gms.maps.CameraUpdateFactory
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
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import hu.bme.aut.thesis.freshfitness.service.TrackRunningService
import hu.bme.aut.thesis.freshfitness.ui.util.EmptyScreen
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.LocationDisabled
import hu.bme.aut.thesis.freshfitness.ui.util.OkCancelDialog
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.ui.util.UploadStateAlert
import hu.bme.aut.thesis.freshfitness.util.calculateDistanceInMeters
import hu.bme.aut.thesis.freshfitness.util.calculateElapsedTime
import hu.bme.aut.thesis.freshfitness.util.calculateMiddlePoint
import hu.bme.aut.thesis.freshfitness.util.setCustomMapIcon
import hu.bme.aut.thesis.freshfitness.viewmodel.TrackRunningViewModel
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.round

@SuppressLint("SimpleDateFormat")
@Composable
fun TrackRunningScreen(
    contentType: FreshFitnessContentType,
    viewModel: TrackRunningViewModel = viewModel(factory = TrackRunningViewModel.factory)
) {
    RequireLocationPermissions {
        val runs by viewModel.allRuns.observeAsState()
        val isServiceRunning by TrackRunningService.isRunning.collectAsState()
        var showRunOnMap by remember { mutableStateOf(false) }
        var shownRun: RunWithCheckpoints? by remember { mutableStateOf(null) }
        var showShareRunDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val screenshotState = rememberScreenshotState()
        val coroutineScope = rememberCoroutineScope()

        val onShare: () -> Unit = {
            coroutineScope.launch {
                screenshotState.capture()
                showShareRunDialog = true
            }
        }
        val onClickRun: (RunWithCheckpoints) -> Unit = {
            shownRun = it
            showRunOnMap = true
        }

        LaunchedEffect(key1 = false) {
            viewModel.getSession()
            viewModel.checkLocationState()
            viewModel.fetchRuns()
        }
        Box(modifier = Modifier.fillMaxSize()) {
            SharedRunNotification(visible = viewModel.runShared)
            Column {
                when (contentType) {
                    FreshFitnessContentType.LIST_ONLY -> {
                        TrackRunningScreenListOnly(
                            viewModel = viewModel,
                            showRunOnMap = showRunOnMap,
                            shownRun = shownRun,
                            screenshotState = screenshotState,
                            onShare = onShare,
                            onDismissShowRun = { showRunOnMap = false },
                            isServiceRunning = isServiceRunning,
                            runs = runs,
                            onClickRun = onClickRun
                        )
                    }
                    FreshFitnessContentType.LIST_AND_DETAIL -> {
                        TrackRunningScreenListAndDetail(
                            viewModel = viewModel,
                            showRunOnMap = showRunOnMap,
                            shownRun = shownRun,
                            screenshotState = screenshotState,
                            onShare = onShare,
                            isServiceRunning = isServiceRunning,
                            runs = runs,
                            onClickRun = onClickRun
                        )
                    }
                }
            }
        }

        AnimatedVisibility(showShareRunDialog) {
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
        AnimatedVisibility(viewModel.showUploadState) {
            UploadStateAlert(text = stringResource(R.string.uploading_file), fractionCompleted = viewModel.uploadState)
        }
    }
}

@Composable
fun TrackRunningScreenListOnly(
    viewModel: TrackRunningViewModel,
    showRunOnMap: Boolean,
    shownRun: RunWithCheckpoints?,
    screenshotState: ScreenshotState,
    onShare: () -> Unit,
    onDismissShowRun: () -> Unit,
    isServiceRunning: Boolean,
    runs: List<RunWithCheckpoints>?,
    onClickRun: (RunWithCheckpoints) -> Unit,
) {
    val context = LocalContext.current
    if (showRunOnMap) {
        RunOnMap(
            isLoggedIn = viewModel.isLoggedIn,
            shownRun = shownRun!!,
            screenshotState = screenshotState,
            onShare = onShare,
            onDismiss = onDismissShowRun
        )
    }
    else {
        TrackRunningList(
            locationSettingState = viewModel.locationSettingState,
            isServiceRunning = isServiceRunning,
            context = context,
            onStart = viewModel::startLocationTrackingService,
            onStop = viewModel::stopLocationTrackingService,
            runs = runs,
            onClickRun = onClickRun,
            onDeleteRun = viewModel::deleteRun,
            onTryAgain = viewModel::checkLocationState
        )
    }
}

@Composable
fun TrackRunningScreenListAndDetail(
    modifier: Modifier = Modifier,
    viewModel: TrackRunningViewModel,
    showRunOnMap: Boolean,
    shownRun: RunWithCheckpoints?,
    screenshotState: ScreenshotState,
    onShare: () -> Unit,
    isServiceRunning: Boolean,
    runs: List<RunWithCheckpoints>?,
    onClickRun: (RunWithCheckpoints) -> Unit,
) {
    val context = LocalContext.current
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = modifier.weight(1f)) {
            TrackRunningList(
                locationSettingState = viewModel.locationSettingState,
                isServiceRunning = isServiceRunning,
                context = context,
                onStart = viewModel::startLocationTrackingService,
                onStop = viewModel::stopLocationTrackingService,
                runs = runs,
                onClickRun = onClickRun,
                onDeleteRun = viewModel::deleteRun,
                onTryAgain = viewModel::checkLocationState
            )
        }
        Column(modifier = modifier.weight(1f)) {
            if (showRunOnMap) {
                RunOnMap(
                    isLoggedIn = viewModel.isLoggedIn,
                    shownRun = shownRun!!,
                    screenshotState = screenshotState,
                    onShare = onShare
                )
            } else {
                EmptyScreen("No run selected.", "Click on a run to show here")
            }
        }
    }
}

@Composable
fun TrackRunningList(
    locationSettingState: Boolean,
    isServiceRunning: Boolean,
    context: Context,
    onStart: () -> Unit,
    onStop: () -> Unit,
    runs: List<RunWithCheckpoints>?,
    onClickRun: (RunWithCheckpoints) -> Unit,
    onDeleteRun: (Long) -> Unit,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isServiceRunning || locationSettingState) {
            StartOrStopTrackButton(isServiceRunning = isServiceRunning, context, onStart, onStop)
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)) {
                LocationDisabled(onTryAgain = onTryAgain)
            }
        }
        PreviousRunsList(
            runs = runs,
            onClickRun = onClickRun,
            onDeleteRun = onDeleteRun
        )
    }
}

@Composable
fun RunOnMap(
    isLoggedIn: Boolean,
    shownRun: RunWithCheckpoints,
    screenshotState: ScreenshotState,
    onShare: () -> Unit,
    onDismiss: () -> Unit = { }
) {
    TrackedRun(
        run = shownRun,
        screenshotState = screenshotState,
        isLoggedIn = isLoggedIn,
        onShare = onShare,
        onDismiss = onDismiss
    )
}

@Composable
fun StartOrStopTrackButton(
    isServiceRunning: Boolean,
    context: Context,
    onStart: () -> Unit,
    onStop: () -> Unit

) {
    val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    Button(
        enabled = true,
        onClick = {
            if (!isServiceRunning) {
                if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED })
                    onStart()
            } else {
                onStop()
            }
        }) {
        Text(text = stringResource(if (!isServiceRunning) R.string.start else R.string.stop))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviousRunsList(
    runs: List<RunWithCheckpoints>?,
    onClickRun: (RunWithCheckpoints) -> Unit,
    onDeleteRun: (Long) -> Unit
) {
    if (runs?.isNotEmpty() == true) {
        LazyColumn(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stickyHeader {
                Text(
                    text = stringResource(R.string.previous_runs),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
            if (runs.isEmpty()) {
                item { EmptyScreen("There are no previously tracked runs.") }
            }
            else {
                items(runs.size) {index ->
                    runs[index].run {
                        RunListItem(run = this, onClick = onClickRun, onDelete = onDeleteRun)
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun getAdditionalShareText(run: RunWithCheckpoints, context: Context): String =
    "I ran ${context.resources.getString(R.string.meters, round(run.checkpoints.calculateDistanceInMeters() * 100) / 100)} " +
    "in ${calculateElapsedTime(run.run.startTime, run.run.endTime).run { "${this / 60}:${this % 60}" }} minutes " +
    "on ${SimpleDateFormat("MM.dd").format(Date(run.run.startTime))}\n" +
    "Can you beat me?"

@SuppressLint("SimpleDateFormat")
@Composable
fun RunListItem(
    run: RunWithCheckpoints,
    onClick: (RunWithCheckpoints) -> Unit,
    onDelete: (Long) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(6.dp)
            .clickable(onClick = { onClick(run) }),
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
            IconButton(onClick = { onDelete(run.run.id) }) {
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
        TrackedRunStatistics(
            checkpoints = run.checkpoints,
            startTime = run.run.startTime,
            endTime = run.run.endTime
        )
        TrackedRunMap(checkpoints = run.checkpoints, screenshotState = screenshotState)
        if (isLoggedIn) {
            Button(onClick = onShare) {
                Text(text = stringResource(R.string.share))
            }
        }
    }
}

@Composable
fun TrackedRunStatistics(
    checkpoints: List<RunCheckpointEntity>,
    startTime: Long,
    endTime: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.meters, round(checkpoints.calculateDistanceInMeters() * 100) / 100),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
        Text(
            text = calculateElapsedTime(startTime, endTime).run { "${this / 60}:${this % 60}" },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )
    }
}

@Composable
fun TrackedRunMap(
    checkpoints: List<RunCheckpointEntity>,
    screenshotState: ScreenshotState,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calculateMiddlePoint(checkpoints.first(), checkpoints.last()), 15f)
    }
    LaunchedEffect(key1 = checkpoints) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(calculateMiddlePoint(checkpoints.first(), checkpoints.last()), 15f, 0f, 0f)
            ),
            durationMs = 1000
        )
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
                StartMarker(checkpoints.first().let { LatLng(it.latitude, it.longitude) })
                PolylineRun(checkpoints.map { LatLng(it.latitude, it.longitude) })
                FinishMarker(checkpoints.last().let { LatLng(it.latitude, it.longitude) })
            }
        }
    }
}

@Composable
fun StartMarker(
    latLng: LatLng
) {
    Marker(
        state = MarkerState(latLng),
        icon = setCustomMapIcon(stringResource(R.string.start)),
        zIndex = 50f
    )
}

@Composable
fun PolylineRun(
    points: List<LatLng>
) {
    Polyline(
        points = points,
        color = Color(0, 150, 255),
        jointType = JointType.DEFAULT,
        pattern = listOf(Dash(20f), Gap(8f)),
        startCap = RoundCap(),
        endCap = RoundCap(),
        width = 6f
    )
}

@Composable
fun FinishMarker(latLng: LatLng) {
    Marker(
        state = MarkerState(latLng),
        icon = setCustomMapIcon(stringResource(R.string.finish)),
        zIndex = 49f
    )
}

@Composable
fun SharedRunNotification(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your run has been shared!\nCheck your social feed!",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
    }
}