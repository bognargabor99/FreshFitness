package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.LocationEnabledState
import hu.bme.aut.thesis.freshfitness.model.NearByGymShowLocationState
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.DistanceFilter
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.ui.util.MapMarker
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.viewmodel.NearbyGymsViewModel
import kotlinx.coroutines.launch

@Composable
fun NearbyGymsScreen(
    viewModel: NearbyGymsViewModel = viewModel(factory = NearbyGymsViewModel.factory)
) {
    RequireLocationPermissions {
        LaunchedEffect(true) { viewModel.startLocationFlow() }

        Column {
            NearbyGymsTopAppBar(
                onRefresh = { viewModel.startLocationFlow() },
                onLoadList = { viewModel.setShowSavedList() }
            )
            Divider()
            if (!viewModel.showSavedList) {
                when (viewModel.locationEnabled) {
                    LocationEnabledState.UNKNOWN -> {
                        LocationEnabledUnknown(
                            radius = viewModel.radius,
                            onValueChange = { viewModel.changeRadius(it.toInt()) }
                        )
                    }

                    LocationEnabledState.DISABLED -> {
                        LocationDisabled {
                            viewModel.startLocationFlow()
                        }
                    }

                    LocationEnabledState.ENABLED_SEARCHING -> {
                        LocationEnabledSearching(
                            radius = viewModel.radius,
                            onValueChange = { viewModel.changeRadius(it.toInt()) },
                            onQuery = { viewModel.startLocationFlow() }
                        )
                    }

                    LocationEnabledState.ENABLED_SEARCHING_FINISHED -> {
                        GymListScreen(
                            useDistanceFilter = true,
                            radius = viewModel.radius,
                            onRadiusChange = { viewModel.changeRadius(it.toInt()) },
                            gyms = viewModel.gyms,
                            onSaveItem = { viewModel.savePlace(it) },
                            onCheckSaved = { place -> viewModel.favouritePlaces.any { it.id == place.placeId }},
                            onGoToPlace = { viewModel.showPlaceOnMap(it) },
                            onHidePlace = { viewModel.hideMap() },
                            locationState = viewModel.showLocationState,
                            shownLocation = viewModel.shownLocation,
                            userLocation =  viewModel.currentLocation
                        )
                    }
                }
            } else {
                GymListScreen(
                    useDistanceFilter = false,
                    radius = 0,
                    onRadiusChange = { },
                    gyms = viewModel.gyms,
                    onSaveItem = { viewModel.savePlace(it) },
                    onCheckSaved = { place -> viewModel.favouritePlaces.any { it.id == place.placeId }},
                    onGoToPlace = { viewModel.showPlaceOnMap(it) },
                    onHidePlace = { viewModel.hideMap() },
                    locationState = viewModel.showLocationState,
                    shownLocation = viewModel.shownLocation,
                    userLocation =  viewModel.currentLocation
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyGymsTopAppBar(
    onRefresh: () -> Unit,
    onLoadList: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.gyms_nearby)) },
        actions = {
            TopAppBarActionButton(
                imageVector = Icons.Default.Refresh,
                description = stringResource(id = R.string.refresh),
                onClick = onRefresh
            )
            TopAppBarActionButton(
                imageVector = Icons.Default.Grade,
                description = stringResource(id = R.string.view_saved),
                onClick = onLoadList
            )
        },
    )
}

@Composable
fun LocationDisabled(
    onTryAgain: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "To continue, please turn on device location and then try again")
            Button(onClick = { onTryAgain() }) {
                Text(text = "Try again")
            }
        }
    }
}

@Composable
fun TopAppBarActionButton(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(imageVector = imageVector, contentDescription = description)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymListScreen(
    useDistanceFilter: Boolean,
    radius: Int,
    onRadiusChange: (Float) -> Unit,
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    onHidePlace: () -> Unit,
    locationState: NearByGymShowLocationState,
    shownLocation: LatLng,
    userLocation: LatLng
) {
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false, confirmValueChange = {
        if (it == SheetValue.Hidden) {
            onHidePlace()
        }
        true
    })
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (locationState is NearByGymShowLocationState.Show)
                GoogleMapSheetContent(
                    shownLocation = shownLocation,
                    userLocation = userLocation
                )
        }) {
        Column {
            if (useDistanceFilter) {
                DistanceFilter(
                    radius = radius,
                    onValueChange = onRadiusChange,
                    onQuery = { }
                )
                Divider()
            }
            if (gyms.isEmpty())
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "No gyms found",
                    textAlign = TextAlign.Center
                )
            else
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(gyms.size) { placeIndex ->
                        NearByGymItem(
                            place = gyms[placeIndex],
                            onSaveItem = onSaveItem,
                            saved = onCheckSaved(gyms[placeIndex]),
                            onGo = {
                                scope.launch {
                                    onGoToPlace(gyms[placeIndex])
                                    sheetState.expand()
                                }
                            }
                        )
                    }
                }
        }
    }
}

@Composable
fun LocationEnabledUnknown(radius: Int, onValueChange: (Float) -> Unit) {
    Column {
        DistanceFilter(
            radius = radius,
            onValueChange = onValueChange,
            onQuery = { }
        )

        Divider()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfiniteCircularProgressBar()
                Text(
                    text = "Checking settings...",
                    fontStyle = FontStyle.Italic,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun LocationEnabledSearching(radius: Int, onValueChange: (Float) -> Unit, onQuery: () -> Unit) {
    Column {
        DistanceFilter(
            radius = radius,
            onValueChange = onValueChange,
            onQuery = onQuery
        )

        Divider()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfiniteCircularProgressBar()
                Text(
                    text = "Retrieving location...",
                    fontStyle = FontStyle.Italic,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun GoogleMapSheetContent(
    shownLocation: LatLng,
    userLocation: LatLng
) {
    val cameraState = rememberCameraPositionState() {
        position = CameraPosition.fromLatLngZoom(shownLocation, 13.5f)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState
        ) {
            Marker(
                state = MarkerState(position = shownLocation),
                title = "Here"
            )
            MapMarker(
                context = LocalContext.current,
                position = userLocation,
                title = "You are here",
                iconResourceId = R.drawable.ic_map_marker)
        }
    }
}

@Composable
fun NearByGymItem(
    place: PlacesSearchResult,
    saved: Boolean = false,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onGo: () -> Unit
) {
    NearbyGymItem(
        name = place.name ?: "Default name",
        address = place.vicinity ?: "Default address",
        rating = place.rating,
        totalRatings = place.userRatingsTotal,
        saved = saved,
        onSaveToFavourites = { onSaveItem(place) },
        onGo = onGo
    )
}

@Composable
fun NearbyGymItem(
    name: String,
    address: String,
    rating: Float,
    totalRatings: Int,
    saved: Boolean = false,
    onSaveToFavourites: () -> Unit,
    onGo: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = Color.DarkGray.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(6.dp)
            .clickable { expanded = !expanded },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(.8f),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = name, modifier = Modifier.padding(bottom = 6.dp), style = MaterialTheme.typography.titleLarge.copy(color = Color.Black.copy(alpha = 0.6f)), fontWeight = FontWeight.Bold)
                Text(text = address, style = MaterialTheme.typography.labelLarge.copy(color = Color.Black.copy(alpha = 0.6f)))
            }
            IconButton(
                modifier = Modifier.weight(.2f),
                onClick = onSaveToFavourites,
            ) {
                Icon(
                    imageVector = if (saved) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    tint = if (saved) Color(0xffFFD700) else MaterialTheme.colorScheme.onBackground,
                    contentDescription = if (saved) "Delete from favourites" else "Save to favourites"
                )
            }
        }
        if (expanded)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$totalRatings users rated this place $rating/5")
                Button(onClick = { onGo() }) {
                    Text(text = "Show")
                }
            }
    }
}

@Preview
@Composable
fun NearbyGymItemPreview() {
    FreshFitnessTheme {
        NearbyGymItem(
            name = "Muscle Beach",
            address = "Siófok, Petőfi stny. 3-5, 8600",
            rating = 5.0f,
            totalRatings = 1,
            onGo = { },
            saved = false,
            onSaveToFavourites = { }
        )
    }
}