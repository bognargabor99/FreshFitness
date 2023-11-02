package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.ui.util.media.WorkoutCover
import java.util.Locale

@Composable
fun DetailedWorkout(
    workout: Workout,
    onDisMiss: () -> Unit
) {
    var showEquipmentModal by remember { mutableStateOf(false) }
    var showWarmup by remember { mutableStateOf(true) }
    val equipments = workout.exercises.filter { it.exercise != null && it.exercise!!.equipment != null }.map { it.exercise!!.equipment!! }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            .clickable { onDisMiss() }
    ) {
        WorkoutCover(name = workout.targetMuscle!!.name, imageRes = getWorkoutBackground(workout)) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WorkoutBadge(modifier = Modifier.fillMaxHeight(), text = workout.difficulty, backGroundColor = MaterialTheme.colorScheme.background, fontColor = MaterialTheme.colorScheme.onBackground)
                WorkoutBadge(modifier = Modifier.fillMaxHeight(), text = "${workout.sets} sets", backGroundColor = MaterialTheme.colorScheme.background, fontColor = MaterialTheme.colorScheme.onBackground)
                WorkoutBadge(modifier = Modifier.fillMaxHeight(), text = "warmup", backGroundColor = MaterialTheme.colorScheme.background, fontColor = MaterialTheme.colorScheme.onBackground,
                    leadingIcon = { Icon(modifier = Modifier.heightIn(min = 14.dp, max = 14.dp), tint = MaterialTheme.colorScheme.primary, imageVector = if (workout.warmupExercises.any()) Icons.Filled.Check else Icons.Filled.Close, contentDescription = null) }
                )
            }
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorkoutOverview(
                workout = workout,
                onSeeEquipments = { showEquipmentModal = true },
                showWarmup = showWarmup,
                onSwitchWarmup = { showWarmup = it }
            )
            if (showWarmup && workout.warmupExercises.isNotEmpty()) {
                ExerciseList(
                    title = "Warmup",
                    exercises = workout.warmupExercises.sortedBy { it.sequenceNum }
                )
            }
            if (showWarmup && workout.warmupExercises.any() && workout.exercises.any())
                ExerciseGroupDivider(text = "Rest 2 minutes")
            if (workout.exercises.any()) {
                ExerciseList(
                    title = "Workout",
                    exercises = workout.exercises.sortedBy { it.sequenceNum }
                )
            }
        }
    }
}


@Composable
fun WorkoutOverview(
    workout: Workout,
    onSeeEquipments: (List<Equipment>) -> Unit,
    showWarmup: Boolean,
    onSwitchWarmup: (Boolean) -> Unit
) {
    val equipmentList = workout.exercises.filter { it.exercise != null && it.exercise!!.equipment != null }.map { it.exercise!!.equipment!! }
    Column(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.Speed, title = "Difficulty") {
            Text(text = workout.difficulty.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        }
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.FitnessCenter, title = "Equipments") {
            ClickableText(text = AnnotatedString(stringResource(id = R.string.see_equipment)), onClick = { onSeeEquipments(equipmentList) })
        }
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.Accessibility, title = "Warmup") {
            Switch(
                checked = showWarmup && workout.warmupExercises.any(),
                enabled = workout.warmupExercises.any(),
                onCheckedChange = { onSwitchWarmup(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun WorkoutOverviewRow(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    rightElement: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = imageVector, contentDescription = null)
            Text(text = title)
        }
        rightElement()
    }
}

@Composable
fun ExerciseList(
    title: String,
    exercises: List<WorkoutExercise>
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(modifier = Modifier.padding(8.dp), text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        exercises.forEach {
            WorkoutExerciseRow(workoutExercise = it, onClick = { })
        }
    }
}

@Composable
fun WorkoutExerciseRow(
    workoutExercise: WorkoutExercise,
    onClick: (Exercise) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(workoutExercise.exercise!!) },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (workoutExercise.exercise!!.muscleGroup != null) {
            S3Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .heightIn(min = 60.dp, max = 60.dp)
                    .widthIn(min = 60.dp, max = 60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(6.dp)),
                imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${workoutExercise.exercise!!.muscleGroup!!.imgKey}"
            )
        }
        Column {
            Text(
                text = workoutExercise.exercise!!.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkoutBadge(text = "${workoutExercise.amount} ${workoutExercise.exercise!!.unit!!.unit}")
                if (workoutExercise.exercise!!.unit!!.type == "weight")
                    WorkoutBadge(text = "8-12 reps")
            }
        }
    }
}

@Composable
fun ExerciseGroupDivider(
    text : String
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 4.dp)
                .clip(shape = RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.onBackground, thickness = 2.dp)
        Text(
            text = text.uppercase(),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 4.dp)
                .clip(shape = RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.onBackground, thickness = 2.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutOverviewPreview() {
    WorkoutOverview(
        workout = Workout(
            difficulty = "advanced",
            sets = 4,
            date = "2023-11-01T00:00:00",
            equipmentTypes = "gym",
            muscleId = 2,
            owner = "gaborbognar123",
            warmupExercises = mutableListOf(WorkoutExercise(exerciseId = 1, sequenceNum = 1, isWarmup = 0, amount = 40))
        ),
        onSeeEquipments = {},
        showWarmup = true,
        onSwitchWarmup = {})
}

@Preview(showBackground = true)
@Composable
fun ExerciseGroupDividerPreview() {
    ExerciseGroupDivider(text = "Rest 2 minutes")
}