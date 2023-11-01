package hu.bme.aut.thesis.freshfitness.ui.util.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutBadge

@OptIn(ExperimentalTextApi::class)
@Composable
fun WorkoutCover(
    name: String,
    @DrawableRes imageRes: Int,
    content: @Composable () -> Unit
) {
    Card {
        Box {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "image: $name",
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(4f / 3f),
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { setToScale(0.8f,0.8f,0.8f,1f) }),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0F to Color.Transparent,
                            0.3F to Color.Black.copy(alpha = 0.5F),
                            1F to Color.Black.copy(alpha = 1F)))
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.background,
                    style = TextStyle.Default.copy(
                        drawStyle = Fill,
                        shadow = Shadow(Color.Black, Offset(4f, 4f), blurRadius = 3f)
                    )
                )
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoverPreview() {
    WorkoutCover(name = "Chest & Triceps", imageRes = R.drawable.calisthenics) {
        WorkoutBadge(
            text = "3 sets",
            backGroundColor = MaterialTheme.colorScheme.background,
            fontColor = MaterialTheme.colorScheme.onBackground
        )
    }
}