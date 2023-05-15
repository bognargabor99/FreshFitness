package hu.bme.aut.thesis.freshfitness.ui.util

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

@Composable
fun InfiniteCircularProgressBar() {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
            },
            repeatMode = RepeatMode.Restart
        )
    )
    CircularProgressIndicator(
        modifier = Modifier.rotate(rotationDegrees),
        progress = progress
    )
}

@Preview(
    name = "ProgressBarDay",
    heightDp = 300,
    widthDp = 300,
    showBackground = true
)
@Composable
fun CircularProgressbarPreview() {
    FreshFitnessTheme {
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

@Preview(
    name = "ProgressBarNight",
    heightDp = 300,
    widthDp = 300,
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun CircularProgressbarPreviewDark() {
    FreshFitnessTheme {
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
