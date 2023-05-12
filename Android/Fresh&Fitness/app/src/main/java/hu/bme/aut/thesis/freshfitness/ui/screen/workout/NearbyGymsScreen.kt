package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.LocationEnabledState
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.ui.util.RequireLocationPermissions
import hu.bme.aut.thesis.freshfitness.viewmodel.NearbyGymsViewModel

@Composable
fun NearbyGymsScreen(
    viewModel: NearbyGymsViewModel = viewModel(factory = NearbyGymsViewModel.factory)
) {
    RequireLocationPermissions {
        LaunchedEffect(true) { viewModel.startLocationFlow() }

        when (viewModel.locationEnabled) {
            LocationEnabledState.UNKNOWN -> {
                LocationEnabledUnknown(
                    radius = viewModel.radius,
                    onValueChange = { viewModel.changeRadius(it.toInt()) },
                    onQuery = { viewModel.startLocationFlow() }
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
                LocationSearchFinished(
                    radius = viewModel.radius,
                    onValueChange = { viewModel.changeRadius(it.toInt()) },
                    gyms = viewModel.gyms,
                    onQuery = { viewModel.startLocationFlow() }
                )
            }
        }
    }
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
fun LocationSearchFinished(radius: Int, onValueChange: (Float) -> Unit, gyms: List<PlacesSearchResult>, onQuery: () -> Unit) {
    Column {
        DistanceFilter(
            radius = radius,
            onValueChange = onValueChange,
            onQuery = onQuery
        )

        Divider()

        if (gyms.isEmpty())
            Text(text = "No gyms found")
        else
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(gyms.size) { placeIndex ->
                    NearByGymItem(place = gyms[placeIndex])
                }
            }
    }
}

@Composable
fun LocationEnabledUnknown(radius: Int, onValueChange: (Float) -> Unit, onQuery: () -> Unit) {
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
fun NearByGymItem(
    place: PlacesSearchResult
) {
    NearbyGymItem(
        rating = place.rating,
        name = place.name ?: "Default name",
        address = place.vicinity ?: "Default address"
    )
}

@Composable
fun NearbyGymItem(
    rating: Float,
    name: String,
    address: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(
                    width = 1.dp,
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    RoundedCornerShape(30.dp)
                )
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = name, modifier = Modifier.padding(bottom = 12.dp), style = MaterialTheme.typography.titleLarge.copy(color = Color.Black.copy(alpha = 0.6f)), fontWeight = FontWeight.Bold)
                Text(text = address, style = MaterialTheme.typography.labelLarge.copy(color = Color.Black.copy(alpha = 0.6f)))
            }
            Text(
                text = "$rating",

                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
                    .size(24.dp),
            )

        }
    }
}

@Composable
fun DistanceFilter(
    radius: Int,
    onValueChange: (Float) -> Unit,
    onQuery: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 12.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Column(
        modifier = Modifier
            .padding(bottom = extraPadding.coerceAtLeast(0.dp)).background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Range: $radius m",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }

        if (expanded) {
            Row(
                modifier = Modifier.height(44.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(R.string.distance)
                )
                Slider(
                    value = radius.toFloat(),
                    onValueChange = onValueChange,
                    steps = (10000 - 1000) / 500 - 1,
                    valueRange = 1000f..10000f,
                    onValueChangeFinished = onQuery
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistanceFilterPreview() {
    DistanceFilter(radius = 2500, onValueChange = {}) {

    }
}