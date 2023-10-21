package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.ui.util.FilterTitle
import hu.bme.aut.thesis.freshfitness.ui.util.media.ExerciseMedia
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedExercise(
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ExerciseMedia(exercise.media)
            DetailedExerciseTitle(title = exercise.name)
            DetailedExerciseBadges(exercise)
            DetailedExerciseDetails(details = exercise.details)
            exercise.equipment?.let {
                FilterTitle(title = stringResource(id = R.string.equipments))
                if (it.imgKey.isNotBlank()) DetailedExerciseEquipment(it)
            }
            exercise.alternateEquipment?.let { if (it.imgKey.isNotBlank()) DetailedExerciseEquipment(it) }
        }
    }
}

@Composable
fun DetailedExerciseTitle(title: String) {
    Text(
        text = title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailedExerciseBadges(exercise: Exercise) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 3
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
        shape = RoundedCornerShape(12.dp),

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