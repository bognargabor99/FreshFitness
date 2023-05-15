package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.model.LocationEnabledState
import hu.bme.aut.thesis.freshfitness.ui.util.DistanceFilter
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
                .heightIn(min = 70.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = name, modifier = Modifier.padding(bottom = 6.dp), style = MaterialTheme.typography.titleLarge.copy(color = Color.Black.copy(alpha = 0.6f)), fontWeight = FontWeight.Bold)
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