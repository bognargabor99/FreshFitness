package hu.bme.aut.thesis.freshfitness.ui.util.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
    saveEnabled: Boolean,
    deleteEnabled: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
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
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { setToScale(0.8f,0.8f,0.8f,1f) }),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            0F to Color.Transparent,
                            0.3F to Color.Black.copy(alpha = 0.5F),
                            1F to Color.Black.copy(alpha = 1F)
                        )
                    )
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background,
                        style = TextStyle.Default.copy(
                            drawStyle = Fill,
                            shadow = Shadow(Color.Black, Offset(4f, 4f), blurRadius = 3f)
                        )
                    )
                    content()
                }
                if (!isSaved && saveEnabled)
                    IconButton(
                        onClick = onSave,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background)
                    ) {
                        Icon(imageVector = Icons.Filled.SaveAlt, contentDescription = null)
                    }
                if (isSaved && deleteEnabled)
                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.background)
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoverPreview() {
    WorkoutCover(name = "Chest & Triceps", imageRes = R.drawable.calisthenics, saveEnabled = true, deleteEnabled = true, isSaved = false, onSave = { }, onDelete = { }) {
        WorkoutBadge(
            text = "3 sets",
            backGroundColor = MaterialTheme.colorScheme.background,
            fontColor = MaterialTheme.colorScheme.onBackground
        )
    }
}