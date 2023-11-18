package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.getEquipmentsOfWorkout
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import hu.bme.aut.thesis.freshfitness.ui.util.EmptyScreen
import hu.bme.aut.thesis.freshfitness.ui.util.NeededEquipmentsModal
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.ui.util.media.WorkoutCover
import java.util.Locale
import kotlin.random.Random

@Composable
fun DetailedWorkout(
    workout: Workout,
    saveEnabled: Boolean,
    isSaved: Boolean = false,
    onSave: () -> Unit = { },
    onDelete: () -> Unit = { },
    onDismiss: () -> Unit
) {
    var showEquipmentModal by remember { mutableStateOf(false) }
    var showWarmup by remember { mutableStateOf(true) }
    val equipments = getEquipmentsOfWorkout(workout)

    var showDetailsOfExercise by remember { mutableStateOf(false) }
    var detailedExercise: Exercise? by remember { mutableStateOf(null) }

    val onClickExercise: (Exercise) -> Unit = {
        detailedExercise = it
        showDetailsOfExercise = true
    }
    BackHandler { onDismiss() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        DetailedWorkoutHeader(
            workout = workout,
            saveEnabled = saveEnabled,
            isSaved = isSaved,
            onSave = onSave,
            onDelete = onDelete
        )
        DetailedWorkoutBody(
            workout = workout,
            showEquipmentsEnabled = equipments.any(),
            onSeeEquipments = { showEquipmentModal = true },
            showWarmup = showWarmup,
            onSwitchWarmup = { showWarmup = it },
            onClickExercise = onClickExercise
        )
    }
    if (showDetailsOfExercise)
        detailedExercise?.let { DetailedExerciseBottomSheet(exercise = it, onDismiss = { showDetailsOfExercise = false }) }
    if (equipments.any() && showEquipmentModal) {
        NeededEquipmentsModal(equipments = equipments, onDismiss = { showEquipmentModal = false })
    }
}

@Composable
fun DetailedWorkoutHeader(
    workout: Workout,
    saveEnabled: Boolean,
    isSaved: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) {
    WorkoutCover(
        name = workout.targetMuscle!!.name,
        saveEnabled = saveEnabled,
        imageRes = getWorkoutBackground(workout),
        isSaved = isSaved,
        onSave = onSave,
        onDelete = onDelete
    ) {
        WorkoutCoverBadges(workout)
    }
}

@Composable
fun DetailedWorkoutBody(
    workout: Workout,
    showEquipmentsEnabled: Boolean,
    onSeeEquipments: () -> Unit,
    showWarmup: Boolean,
    onSwitchWarmup: (Boolean) -> Unit,
    onClickExercise: (Exercise) -> Unit
) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WorkoutOverview(
            workout = workout,
            showEquipmentsEnabled = showEquipmentsEnabled,
            onSeeEquipments = onSeeEquipments,
            showWarmup = showWarmup,
            onSwitchWarmup = onSwitchWarmup
        )
        AnimatedVisibility(
            visible = showWarmup && workout.warmupExercises.isNotEmpty()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExerciseList(
                    title = "Warmup",
                    exercises = workout.warmupExercises.sortedBy { it.sequenceNum },
                    onClickExercise = onClickExercise
                )
                ExerciseGroupDivider(text = "Rest 1-2 minutes")
            }
        }
        if (workout.exercises.any()) {
            ExerciseList(
                title = "Workout (${workout.sets} rounds)",
                exercises = workout.exercises.sortedBy { it.sequenceNum },
                onClickExercise = onClickExercise
            )
        }
    }
}

@Composable
fun WorkoutCoverBadges(
    workout: Workout
) {
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

@Composable
fun WorkoutOverview(
    workout: Workout,
    showEquipmentsEnabled: Boolean,
    onSeeEquipments: () -> Unit,
    showWarmup: Boolean,
    onSwitchWarmup: (Boolean) -> Unit
) {
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
            ClickableText(
                text = if (showEquipmentsEnabled) AnnotatedString(stringResource(id = R.string.see_equipment)) else AnnotatedString(stringResource(id = R.string.no_equipment)),
                style = TextStyle(textDecoration = if (showEquipmentsEnabled) TextDecoration.Underline else TextDecoration.None),
                onClick = { if (showEquipmentsEnabled) onSeeEquipments() }
            )
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
    modifier: Modifier = Modifier,
    title: String,
    exercises: List<WorkoutExercise>,
    onClickExercise: (Exercise) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(modifier = Modifier.padding(8.dp), text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        exercises.forEach {
            WorkoutExerciseRow(workoutExercise = it, onClick = onClickExercise)
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

@Composable
fun DetailedWorkoutEmpty() {
    EmptyScreen("No workout selected", "Click on a workout to view")
}

@Composable
fun LoadingDetailedWorkout(alpha: Float) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LoadingDetailedWorkoutHeader(alpha = alpha)
        LoadingDetailedWorkoutBody(alpha = alpha)
    }
}

@Composable
fun LoadingDetailedWorkoutHeader(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray.copy(alpha = alpha)),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) {
                LoadingDetailedWorkoutHeaderBadge(alpha)
            }
        }
    }
}

@Composable
fun LoadingDetailedWorkoutHeaderBadge(alpha: Float) {
    val badgeLength by remember { mutableStateOf(Random.nextInt(60, 100)) }
    Box(
        modifier = Modifier
            .widthIn(min = badgeLength.dp, max = badgeLength.dp)
            .heightIn(min = 18.dp, max = 18.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Gray.copy(alpha = alpha), shape = RoundedCornerShape(6.dp))
    )
}

@Composable
fun LoadingDetailedWorkoutBody(alpha: Float) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LoadingWorkoutOverview(alpha = alpha)
        LoadingWorkoutExerciseList(alpha = alpha, count = 3)
        LoadingExerciseGroupDivider(alpha = alpha)
        LoadingWorkoutExerciseList(alpha = alpha, count = 6)
    }
}

@Composable
fun LoadingWorkoutOverview(alpha: Float) {
    Column(
        modifier = Modifier
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Color.Gray.copy(alpha = alpha), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        LoadingWorkoutOverViewRow(modifier = Modifier.weight(1f), alpha = alpha)
        LoadingWorkoutOverViewRow(modifier = Modifier.weight(1f), alpha = alpha)
        LoadingWorkoutOverViewRow(modifier = Modifier.weight(1f), alpha = alpha)
    }
}

@Composable
fun LoadingWorkoutOverViewRow(
    modifier: Modifier = Modifier,
    alpha: Float
) {
    val boxLengths by remember { mutableStateOf(listOf(Random.nextInt(100, 140), Random.nextInt(50, 80))) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        boxLengths.forEach {
            Box(
                modifier = Modifier
                    .widthIn(it.dp, it.dp)
                    .heightIn(18.dp, 18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Gray.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun LoadingExerciseGroupDivider(alpha: Float) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = 4.dp)
                .clip(shape = RoundedCornerShape(2.dp)),
            color = Color.Gray.copy(alpha = alpha), thickness = 2.dp)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(0.2f)
                .heightIn(16.dp, 16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = alpha)),
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 4.dp)
                .clip(shape = RoundedCornerShape(2.dp)),
            color = Color.Gray.copy(alpha = alpha), thickness = 2.dp)
    }
}

@Composable
fun LoadingWorkoutExerciseList(alpha: Float, count: Int = 3) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Color.Gray.copy(alpha = alpha), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier
            .padding(8.dp)
            .widthIn(70.dp, 70.dp)
            .heightIn(20.dp, 20.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Gray.copy(alpha = alpha))
            .padding(bottom = 6.dp))
        repeat(count) {
            LoadingWorkoutExerciseRow(alpha = alpha)
        }
    }
}

@Composable
fun LoadingWorkoutExerciseRow(alpha: Float) {
    val textLength by remember { mutableStateOf(Random.nextInt(120, 160)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
            //.background(Color.LightGray.copy(alpha = alpha))
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .heightIn(min = 60.dp, max = 60.dp)
                .widthIn(min = 60.dp, max = 60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Gray.copy(alpha = alpha), RoundedCornerShape(6.dp)),
        )

        Column {
            Box(
                modifier = Modifier
                    .widthIn(textLength.dp)
                    .heightIn(20.dp, 20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Gray.copy(alpha))
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) {
                    LoadingWorkoutBadge(alpha = alpha)
                }
            }
        }
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
        showEquipmentsEnabled = true,
        onSeeEquipments = {},
        showWarmup = true,
        onSwitchWarmup = {})
}

@Preview(showBackground = true)
@Composable
fun ExerciseGroupDividerPreview() {
    ExerciseGroupDivider(text = "Rest 2 minutes")
}