package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

@Composable
fun WorkoutScreen(
    onNavigateRunning: () -> Unit = {},
    onNavigateNearbyGyms: () -> Unit = {},
    onNavigateWorkoutPlanning: () -> Unit = {},
    onNavigateExerciseBank: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ImageWithTextOverlay(
                painter = painterResource(R.drawable.workout_running),
                contentDescription = "Go running",
                text = stringResource(R.string.go_for_a_run),
                onClick = {
                    Log.i("fresh_fitness_workout", "Clicked Start running")
                    onNavigateRunning()
                }
            )
        }
        item {
            ImageWithTextOverlay(
                painter = painterResource(R.drawable.workout_places),
                contentDescription = "Find nearby gyms",
                text = stringResource(R.string.gyms_nearby),
                onClick = {
                    Log.i("fresh_fitness_workout", "Clicked gyms near you")
                    onNavigateNearbyGyms()
                }
            )
        }
        item {
            ImageWithTextOverlay(
                painter = painterResource(R.drawable.workout_planning),
                contentDescription = "Planning a workout",
                text = stringResource(R.string.workout_planning),
                onClick = onNavigateWorkoutPlanning
            )
        }

        item {
            ImageWithTextOverlay(
                painter = painterResource(R.drawable.workout_exercise_bank),
                contentDescription = "View exercise bank",
                text = stringResource(R.string.exercise_bank),
                onClick = onNavigateExerciseBank
            )
        }
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
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Image(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() },
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillWidth,
        )
        Text(
            modifier = Modifier.padding(6.dp).fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            color = White.copy(alpha = 0.9f)
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