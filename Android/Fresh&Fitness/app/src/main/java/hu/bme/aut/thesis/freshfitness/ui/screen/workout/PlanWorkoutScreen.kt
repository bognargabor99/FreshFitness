package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.ui.util.DifficultyFilter
import hu.bme.aut.thesis.freshfitness.ui.util.MuscleFilter
import hu.bme.aut.thesis.freshfitness.ui.util.NumberPicker

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlanWorkoutScreen(
    exercises: List<Exercise>,
    allMuscles: List<MuscleGroup>,
    onCreateWorkout: (Workout) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler {
        onDismiss()
    }
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")
    var difficulty by remember { mutableStateOf("") }
    var setCount by remember { mutableStateOf(3) }
    var createWarmup by remember { mutableStateOf(true) }
    var targetMuscle by remember { mutableStateOf("") }
    FlowRow(horizontalArrangement = Arrangement.SpaceAround) {
        DifficultyFilter(currentDifficulty = difficulty, difficulties = difficulties, onDifficultyFilterChange = { difficulty = it })
        NumberPicker(title = "Sets", currentNumber = setCount, numbers = 1..6, onNumberChange = { setCount = it })
        WarmupSwitch(checked = createWarmup, onCheckedChange = { createWarmup = it })
        MuscleFilter(
            muscleFilter = targetMuscle,
            onMuscleFilterChange = { targetMuscle = it },
            allMuscles = allMuscles)
    }
}

@Composable
fun WarmupSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Plan warmup?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PlanWorkoutScreenPreview() {
    PlanWorkoutScreen(
        exercises = listOf(),
        onCreateWorkout = { },
        allMuscles = listOf(
            MuscleGroup(id = 1, name = "Whole body", imgKey = ""),
            MuscleGroup(id = 1, name = "Chest & Triceps", imgKey = ""),
            MuscleGroup(id = 1, name = "Back & biceps", imgKey = ""),
            MuscleGroup(id = 1, name = "Shoulders", imgKey = ""),
            MuscleGroup(id = 1, name = "Abs", imgKey = ""),
            MuscleGroup(id = 1, name = "Legs", imgKey = "")
        )
    ) { }
}