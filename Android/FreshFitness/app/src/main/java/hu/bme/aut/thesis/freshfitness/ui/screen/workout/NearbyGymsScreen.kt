package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.LocationEnabledState
import hu.bme.aut.thesis.freshfitness.model.NearByGymShowLocationState
import hu.bme.aut.thesis.freshfitness.ui.screen.nocontent.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.DistanceFilter
import hu.bme.aut.thesis.freshfitness.ui.util.EmptyScreen
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.MapMarker
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.viewmodel.NearbyGymsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NearbyGymsScreen(
    networkAvailable: Boolean,
    contentType: FreshFitnessContentType,
    viewModel: NearbyGymsViewModel = viewModel()
) {
    val context = LocalContext.current
    RequireLocationPermissions(onDenied = { DeniedLocationPermission() }) {
        LaunchedEffect(networkAvailable) { if (networkAvailable && viewModel.gyms.isEmpty()) viewModel.startLocationFlow(context) }

        Column {
            NearbyGymsTopAppBar(
                onRefresh = { viewModel.startLocationFlow(context) },
                onLoadList = { viewModel.setShowSavedList(context) }
            )
            Divider()
            if (!viewModel.showSavedList) {
                if (!networkAvailable)
                    NetworkUnavailable()
                else
                    GymSearchScreen(
                        contentType = contentType,
                        gyms = viewModel.gyms,
                        radius = viewModel.radius,
                        onSaveItem = viewModel::savePlace,
                        onCheckSaved = { place -> viewModel.favouritePlaces.any { it.id == place.placeId }},
                        onGoToPlace = viewModel::showPlaceOnMap,
                        onHidePlace = viewModel::hideMap,
                        locationState = viewModel.showLocationState,
                        userLocation =  viewModel.currentLocation,
                        changeRadius = viewModel::changeRadius,
                        locationEnabledState = viewModel.locationEnabledState,
                        startLocationFlow = { viewModel.startLocationFlow(context) }
                    )
            } else {
                GymListScreen(
                    contentType = contentType,
                    useDistanceFilter = false,
                    radius = 0,
                    onRadiusChange = { },
                    gyms = viewModel.gyms,
                    onSaveItem = viewModel::savePlace,
                    onCheckSaved = { place -> viewModel.favouritePlaces.any { it.id == place.placeId }},
                    onGoToPlace = viewModel::showPlaceOnMap,
                    onHidePlace = viewModel::hideMap,
                    locationState = viewModel.showLocationState,
                    userLocation =  viewModel.currentLocation
                )
            }
        }
    }
}

@Composable
fun GymSearchScreen(
    contentType: FreshFitnessContentType,
    locationEnabledState: LocationEnabledState,
    radius: Int,
    changeRadius: (Float) -> Unit,
    startLocationFlow: () -> Unit,
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    onHidePlace: () -> Unit,
    locationState: NearByGymShowLocationState,
    userLocation: LatLng
) {
    when (locationEnabledState) {
        LocationEnabledState.UNKNOWN ->
            LocationEnabledUnknown(radius, changeRadius)

        LocationEnabledState.DISABLED ->
            LocationDisabled(startLocationFlow)

        LocationEnabledState.ENABLED_SEARCHING ->
            LocationEnabledSearching(radius, changeRadius)

        LocationEnabledState.ENABLED_SEARCHING_FINISHED -> {
            GymListScreen(
                contentType = contentType,
                useDistanceFilter = true,
                radius = radius,
                onRadiusChange = changeRadius,
                gyms = gyms,
                onSaveItem = onSaveItem,
                onCheckSaved = onCheckSaved,
                onGoToPlace = onGoToPlace,
                onHidePlace = onHidePlace,
                locationState = locationState,
                userLocation =  userLocation
            )
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

@Preview
@Composable
fun DeniedLocationPermission() {
    EmptyScreen(
        "You did not grant access to your location and without that we cannot show gyms nearby",
        "Please enable the access in your settings"
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
            Text(modifier = Modifier.fillMaxWidth(0.8f), text = "To continue, please turn on device location and then try again", color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
            Button(onClick = onTryAgain) {
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
    IconButton(onClick = onClick) {
        Icon(imageVector = imageVector, contentDescription = description)
    }
}

@Composable
fun GymListScreen(
    contentType: FreshFitnessContentType,
    useDistanceFilter: Boolean,
    radius: Int,
    onRadiusChange: (Float) -> Unit,
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    onHidePlace: () -> Unit,
    locationState: NearByGymShowLocationState,
    userLocation: LatLng
) {

    Column {
        if (useDistanceFilter) {
            DistanceFilter(
                radius = radius,
                onValueChange = onRadiusChange
            )
            Divider()
        }
        if (gyms.isEmpty())
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "No gyms found",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        else {
            GymList(
                contentType = contentType,
                gyms = gyms,
                onSaveItem = onSaveItem,
                onCheckSaved = onCheckSaved,
                onGoToPlace = onGoToPlace,
                onHidePlace = onHidePlace,
                userLocation = userLocation,
                showLocationState = locationState
            )
        }
    }
}

@Composable
fun GymList(
    contentType: FreshFitnessContentType,
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    onHidePlace: () -> Unit,
    userLocation: LatLng,
    showLocationState: NearByGymShowLocationState
) {
    val scope = rememberCoroutineScope()

    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            GymListListOnly(
                gyms = gyms,
                onSaveItem = onSaveItem,
                onCheckSaved = onCheckSaved,
                onGoToPlace = onGoToPlace,
                showLocationState = showLocationState,
                onHidePlace = onHidePlace,
                userLocation = userLocation,
                scope = scope
            )
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            GymListListAndDetail(
                gyms = gyms,
                onSaveItem = onSaveItem,
                onCheckSaved = onCheckSaved,
                onGoToPlace = onGoToPlace,
                showLocationState = showLocationState,
                userLocation = userLocation,
                scope = scope,
            )
        }
    }
}

@Composable
fun GymListListOnly(
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    showLocationState: NearByGymShowLocationState,
    onHidePlace: () -> Unit,
    userLocation: LatLng,
    scope: CoroutineScope
) {
    GymListOnlyList(
        gyms = gyms,
        onSaveItem = onSaveItem,
        onCheckSaved = onCheckSaved,
        onGoToPlace = onGoToPlace,
        openableItems = true,
        scope = scope
    )
    if (showLocationState is NearByGymShowLocationState.Show) {
        GoogleMapModalBottomSheet(
            onHidePlace = onHidePlace,
            place = showLocationState.place,
            userLocation = userLocation
        )
    }
}

@Composable
fun GymListListAndDetail(
    modifier: Modifier = Modifier,
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    showLocationState: NearByGymShowLocationState,
    userLocation: LatLng,
    scope: CoroutineScope
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = modifier.weight(1f)) {
            GymListOnlyList(
                gyms = gyms,
                onSaveItem = onSaveItem,
                onCheckSaved = onCheckSaved,
                onGoToPlace = onGoToPlace,
                openableItems = false,
                scope = scope
            )
        }
        Column(modifier = modifier.weight(1f)) {
            if (showLocationState is NearByGymShowLocationState.Show) {
                DetailedPlace(place = showLocationState.place, showFooter = true, userLocation = userLocation)
            }
            else {
                EmptyScreen("No place chosen", "Please click on one to view it")
            }
        }
    }
}

@Composable
fun GymListOnlyList(
    gyms: List<PlacesSearchResult>,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onCheckSaved: (PlacesSearchResult) -> Boolean,
    onGoToPlace: (PlacesSearchResult) -> Unit,
    openableItems: Boolean,
    scope: CoroutineScope
) {
    LazyColumn(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(gyms.size) { placeIndex ->
            NearByGymItem(
                place = gyms[placeIndex],
                onSaveItem = onSaveItem,
                saved = onCheckSaved(gyms[placeIndex]),
                onGo = {
                    scope.launch {
                        onGoToPlace(gyms[placeIndex])
                    }
                },
                openable = openableItems
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapModalBottomSheet(
    place: PlacesSearchResult,
    onHidePlace: () -> Unit,
    userLocation: LatLng
) {
    ModalBottomSheet(
        modifier = Modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onHidePlace,
    ) {
        DetailedPlace(
            place = place,
            userLocation = userLocation,
            showFooter = false
        )
    }
}

@Composable
fun LocationEnabledUnknown(radius: Int, onValueChange: (Float) -> Unit) {
    LocationLoading(
        text = "Checking settings...",
        radius = radius,
        onValueChange = onValueChange
    )
}

@Composable
fun LocationEnabledSearching(radius: Int, onValueChange: (Float) -> Unit) {
    LocationLoading(
        text = "Retrieving location...",
        radius = radius,
        onValueChange = onValueChange
    )
}

@Composable
fun LocationLoading(text: String, radius: Int, onValueChange: (Float) -> Unit) {
    Column {
        DistanceFilter(
            radius = radius,
            onValueChange = onValueChange
        )
        Divider()
        ScreenLoading(loadingText = text)
    }
}

@Composable
fun DetailedPlace(
    place: PlacesSearchResult,
    showFooter: Boolean,
    userLocation: LatLng
) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            place.geometry.location.run { LatLng(lat, lng) }, 13.5f
        )
    }
    LaunchedEffect(key1 = place) {
        cameraState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(place.geometry.location.run { LatLng(lat, lng) }, 13.5f, 0f, 0f)
            ),
            durationMs = 1000
        )
    }
    DetailedPlaceContent(
        cameraState = cameraState,
        place = place,
        userLocation = userLocation,
        showFooter = showFooter
    )
}

@Composable
fun DetailedPlaceContent(
    cameraState: CameraPositionState,
    place: PlacesSearchResult,
    userLocation: LatLng,
    showFooter: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailedPlaceHeader(place = place)
        DetailedPlaceMap(
            cameraState = cameraState,
            place = place,
            userLocation = userLocation
        )
        if (showFooter)
            DetailedPlaceFooter(place = place)
    }
}

@Composable
fun DetailedPlaceHeader(place: PlacesSearchResult) {
    Text(
        text = place.name,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DetailedPlaceMap(
    cameraState: CameraPositionState,
    place: PlacesSearchResult,
    userLocation: LatLng
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentAlignment = Alignment.Center
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
            cameraPositionState = cameraState,
        ) {
            Marker(
                state = MarkerState(position = place.geometry.location.run { LatLng(lat, lng) }),
                title = stringResource(R.string.gym)
            )
            MapMarker(
                context = LocalContext.current,
                position = userLocation,
                title = stringResource(R.string.you_are_here),
                iconResourceId = R.drawable.ic_map_marker
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailedPlaceFooter(place: PlacesSearchResult) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = place.vicinity,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = "${place.userRatingsTotal} users rated this place ${place.rating}/5",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun NearByGymItem(
    place: PlacesSearchResult,
    saved: Boolean = false,
    onSaveItem: (PlacesSearchResult) -> Unit,
    onGo: () -> Unit,
    openable: Boolean = true
) {
    NearbyGymItem(
        name = place.name ?: "Default name",
        address = place.vicinity ?: "Default address",
        rating = place.rating,
        totalRatings = place.userRatingsTotal,
        saved = saved,
        onSaveToFavourites = { onSaveItem(place) },
        onGo = onGo,
        openable = openable
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
    onGo: () -> Unit,
    openable: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(6.dp)
            .clickable {
                if (openable) {
                    expanded = !expanded
                } else {
                    onGo()
                }
            },
    ) {
        NearbyGymItemMainContent(
            name = name,
            address = address,
            saved = saved,
            onSaveToFavourites = onSaveToFavourites
        )
        AnimatedVisibility(visible = openable && expanded) {
            NearbyGymItemSubContent(
                totalRatings = totalRatings,
                rating = rating,
                onGo = onGo
            )
        }
    }
}

@Composable
fun NearbyGymItemMainContent(
    name: String,
    address: String,
    saved: Boolean,
    onSaveToFavourites: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NearbyGymBasicInfo(
            modifier = Modifier
                .fillMaxHeight()
                .weight(.8f),
            name = name,
            address = address
        )
        SavePlaceButton(
            modifier = Modifier.weight(.2f),
            saved = saved,
            onSaveToFavourites = onSaveToFavourites
        )
    }
}

@Composable
fun NearbyGymItemSubContent(
    totalRatings: Int,
    rating: Float,
    onGo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$totalRatings users rated this place $rating/5", color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = onGo) {
            Text(text = "Show")
        }
    }
}

@Composable
fun NearbyGymBasicInfo(
    modifier: Modifier = Modifier,
    name: String,
    address: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = name, modifier = Modifier.padding(bottom = 6.dp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        Text(text = address, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Composable
fun SavePlaceButton(
    modifier: Modifier = Modifier,
    saved: Boolean,
    onSaveToFavourites: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onSaveToFavourites,
    ) {
        Icon(
            imageVector = if (saved) Icons.Filled.Star else Icons.Outlined.StarBorder,
            tint = if (saved) Color(0xffFFD700) else MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(if (saved) R.string.delete_from_favourites else R.string.save_to_favourites)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NearbyGymItemPreview() {
    FreshFitnessTheme {
        NearbyGymItem(
            name = "Muscle Beach",
            address = "Siófok, Petőfi stny. 3-5, 8600",
            rating = 4.7f,
            totalRatings = 173,
            onGo = { },
            saved = false,
            onSaveToFavourites = { },
            openable = true
        )
    }
}