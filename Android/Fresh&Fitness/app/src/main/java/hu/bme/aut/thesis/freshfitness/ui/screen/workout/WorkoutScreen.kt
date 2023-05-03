package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

@Composable
fun WorkoutScreen(
    onNavigateRunning: () -> Unit,
    onNavigateNearbyGyms: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ImageWithTextOverlay(
            modifier = Modifier.fillMaxHeight(0.5f),
            painter = painterResource(R.drawable.workout_running),
            contentDescription = "Go running",
            text = "Track running",
            onClick = {
                Log.i("fresh_fitness_workout", "Clicked Start running")
                onNavigateRunning()
            }
        )
        ImageWithTextOverlay(
            modifier = Modifier.fillMaxHeight(1f),
            painter = painterResource(R.drawable.workout_places),
            contentDescription = "Go running",
            text = "Gyms nearby",
            onClick = {
                Log.i("fresh_fitness_workout", "Clicked gyms near you")
                onNavigateNearbyGyms()
            }
        )
    }
}

@Composable
fun ImageWithTextOverlay(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(20.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() },
        )
        Text(
            text = text,
            textAlign = TextAlign.Start,
            lineHeight = 48.sp,
            fontSize = 48.sp,
            fontFamily = FontFamily.Monospace,
            color = White.copy(alpha = 0.85f)
        )
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

@Preview
@Composable
fun WorkoutScreenPreview() {
    FreshFitnessTheme() {
        WorkoutScreen(
            onNavigateNearbyGyms = {},
            onNavigateRunning = {}
        )
    }
}