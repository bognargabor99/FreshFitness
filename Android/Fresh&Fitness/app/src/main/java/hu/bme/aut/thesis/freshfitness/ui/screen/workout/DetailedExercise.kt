package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
            .fillMaxHeight()
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
        //Spacer(modifier = Modifier.weight(1f))
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

@Preview(showBackground = true)
@Composable
fun ExerciseBadgePreview() {
    ExerciseBadge(text = "demo")
}