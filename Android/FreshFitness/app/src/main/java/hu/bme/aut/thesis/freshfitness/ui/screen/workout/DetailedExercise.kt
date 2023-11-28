package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.ui.util.FilterTitle
import hu.bme.aut.thesis.freshfitness.ui.util.media.ExerciseMedia
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedExerciseBottomSheet(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(1f)
            .nestedScroll(rememberNestedScrollInteropConnection()),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        dragHandle = { }
    ) {
        DetailedExercise(exercise = exercise)
    }
}

@Composable
fun DetailedExercise(
    exercise: Exercise
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        ExerciseMedia(exercise.media)
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            DetailedExerciseTitle(title = exercise.name)
            DetailedExerciseBadges(exercise)
            DetailedExerciseDetails(details = exercise.details)
            exercise.equipment?.let {
                if (it.imgKey.isNotBlank()) {
                    FilterTitle(title = stringResource(id = R.string.equipments))
                    DetailedExerciseEquipment(it)
                }
            }
            exercise.alternateEquipment?.let {
                if (it.imgKey.isNotBlank()) {
                    FilterTitle(title = stringResource(id = R.string.alternative_equipment))
                    DetailedExerciseEquipment(it)
                }
            }
        }
    }
}

@Composable
fun DetailedExerciseTitle(title: String) {
    Text(
        text = title,
        fontSize = 26.sp,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailedExerciseBadges(exercise: Exercise) {
    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExerciseBadge(exercise.difficulty)
        exercise.muscleGroup?.name?.let { ExerciseBadge(it) }
    }
}

@Composable
fun DetailedExerciseDetails(details: String) {
    if (details.isNotEmpty())
        details.trim().replace(". ", ".\n").split("\n").forEachIndexed { index, s ->
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "${index + 1}. $s",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
        }
}

@Composable
fun DetailedExerciseEquipment(equipment: Equipment) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .widthIn(min = 90.dp, max = 140.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        S3Image(
            modifier = Modifier.fillMaxWidth(),
            imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${equipment.imgKey}"
        )
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp), text = equipment.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp), text = equipment.type, textAlign = TextAlign.Center, fontStyle = FontStyle.Italic, color = Color.Gray)
    }
}

@Composable
fun ExerciseBadge(text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.inversePrimary, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            text = text.uppercase(Locale.ROOT),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun LoadingDetailedExercise(alpha: Float) {
    Column {
        LoadingExerciseMedia(alpha)
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LoadingExerciseTitle(alpha)
            LoadingExerciseBadges(alpha)
            LoadingExerciseDetails(alpha)
            LoadingExerciseEquipments(alpha)
        }
    }
}

@Composable
fun LoadingExerciseMedia(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Gray.copy(alpha = alpha))
    )
}

@Composable
fun LoadingExerciseTitle(alpha: Float) {
    val titleLength by remember { mutableStateOf(Random.nextFloat() * 0.5f + 0.5f) }
    Box(
        modifier = Modifier
            .fillMaxWidth(titleLength)
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = alpha))
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoadingExerciseBadges(alpha: Float) {
    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(2) { LoadingExerciseBadge(alpha = alpha) }
    }
}

@Composable
fun LoadingExerciseBadge(alpha: Float) {
    val badgeLength by remember { mutableStateOf(Random.nextInt(80, 120)) }
    Box(
        modifier = Modifier
            .widthIn(min = badgeLength.dp, max = badgeLength.dp)
            .heightIn(min = 22.dp, max = 22.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = alpha), shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun LoadingExerciseDetails(alpha: Float) {
    val rows by remember { mutableStateOf(List(8) { Random.nextFloat() * 0.5f + 0.5f }) }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { length ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(length)
                    .heightIn(18.dp, 18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun LoadingExerciseEquipments(alpha: Float) {
    Box(modifier = Modifier
        .padding(vertical = 4.dp)
        .fillMaxWidth(0.3f)
        .heightIn(14.dp)
        .background(Color.Gray.copy(alpha = alpha))
    )
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .widthIn(min = 140.dp, max = 140.dp)
            .border(
                width = 2.dp,
                color = Color.Gray.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .background(Color.Gray.copy(alpha = alpha))
        )
        listOf(0.75f, 0.4f).forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth(it)
                    .heightIn(16.dp, 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .padding(vertical = 4.dp)
                    .background(Color.Gray.copy(alpha = alpha))
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseBadgePreview() {
    ExerciseBadge(text = "demo")
}

@Preview(showBackground = true)
@Composable
fun LoadingDetailedExercisePreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    LoadingDetailedExercise(alpha)
}