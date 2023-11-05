package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.state.WorkoutPlanState
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.ui.util.DifficultyFilter
import hu.bme.aut.thesis.freshfitness.ui.util.MuscleFilter
import hu.bme.aut.thesis.freshfitness.ui.util.NumberPicker
import hu.bme.aut.thesis.freshfitness.ui.util.TargetDatePicker
import hu.bme.aut.thesis.freshfitness.ui.util.ToggleFilter

@SuppressLint("SimpleDateFormat")
@Composable
fun PlanWorkoutScreen(
    workoutPlanState: WorkoutPlanState,
    allMuscles: List<MuscleGroup>,
    allDifficulties: List<String>,
    allEquipmentTypes: List<String>,
    onSetCountChange: (Int) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onEquipmentTypeChange: (String) -> Unit,
    onMuscleChange: (String) -> Unit,
    onCreateWarmupChange: (Boolean) -> Unit,
    onTargetDateChange: (String) -> Unit,
    isCreationEnabled: Boolean,
    onCreateWorkout: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        var showTargetDateDialog by remember { mutableStateOf(false) }
        BackHandler {
            onDismiss()
        }
        WorkoutPlanningHeader()
        WorkoutPlanningBody(
            allMuscles = allMuscles,
            allDifficulties = allDifficulties,
            allEquipmentTypes = allEquipmentTypes,
            workoutPlanState = workoutPlanState,
            onCreateWarmupChange = onCreateWarmupChange,
            onDifficultyChange = onDifficultyChange,
            onEquipmentTypeChange = onEquipmentTypeChange,
            onMuscleChange = onMuscleChange,
            onSetCountChange = onSetCountChange,
            onShowTargetDateDialog = { showTargetDateDialog = true }
        )
        WorkoutPlanningSubmit(enabled = isCreationEnabled, onSubmit = onCreateWorkout)
        if (showTargetDateDialog) {
            TargetDatePicker(
                selectedDate = workoutPlanState.targetDate,
                onSelectDate = {
                    onTargetDateChange(it)
                    showTargetDateDialog = false
                },
                onDismiss = { showTargetDateDialog = false }
            )
        }
    }
}

@Composable
fun WorkoutPlanningHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.plan_a_workout), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutPlanningBody(
    allDifficulties: List<String>,
    allEquipmentTypes: List<String>,
    allMuscles: List<MuscleGroup>,
    workoutPlanState: WorkoutPlanState,
    onSetCountChange: (Int) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onEquipmentTypeChange: (String) -> Unit,
    onMuscleChange: (String) -> Unit,
    onCreateWarmupChange: (Boolean) -> Unit,
    onShowTargetDateDialog: () -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.SpaceAround) {
        DifficultyFilter(
            currentDifficulty = workoutPlanState.difficulty,
            difficulties = allDifficulties,
            onDifficultyFilterChange = onDifficultyChange)
        EquipmentTypeFilter(
            currentEquipment = workoutPlanState.equipmentType,
            equipmentTypes = allEquipmentTypes,
            onEquipmentTypeFilterChange = onEquipmentTypeChange)
        NumberPicker(
            title = "Sets",
            currentNumber = workoutPlanState.setCount,
            numbers = 1..6,
            onNumberChange = onSetCountChange)
        WarmupSwitch(checked = workoutPlanState.createWarmup, onCheckedChange = onCreateWarmupChange)
        MuscleFilter(
            muscleFilter = workoutPlanState.muscleGroup,
            onMuscleFilterChange = onMuscleChange,
            allMuscles = allMuscles
        )
        TextButton(onClick = onShowTargetDateDialog) {
            Text(text = "Choose date")
        }
    }
}

@Composable
fun EquipmentTypeFilter(currentEquipment: String, equipmentTypes: List<String>, onEquipmentTypeFilterChange: (String) -> Unit) {
    ToggleFilter(title = stringResource(id = R.string.equipment_types), selected = currentEquipment, allElements = equipmentTypes, onFilterChange = onEquipmentTypeFilterChange)
}

@Composable
fun WorkoutPlanningSubmit(
    enabled: Boolean,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight()
    ) {
        Button(modifier = Modifier.fillMaxWidth(), onClick = onSubmit, enabled = enabled) {
            Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.create), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
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