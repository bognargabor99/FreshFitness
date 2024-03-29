package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.state.WorkoutPlanState
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.ui.util.DifficultyFilter
import hu.bme.aut.thesis.freshfitness.ui.util.EmptyScreen
import hu.bme.aut.thesis.freshfitness.ui.util.MuscleFilter
import hu.bme.aut.thesis.freshfitness.ui.util.NumberPicker
import hu.bme.aut.thesis.freshfitness.ui.util.TargetDatePicker
import hu.bme.aut.thesis.freshfitness.ui.util.ToggleFilter

@SuppressLint("SimpleDateFormat")
@Composable
fun PlanWorkoutScreen(
    isAdmin: Boolean,
    workoutPlanState: WorkoutPlanState,
    allMuscles: List<MuscleGroup>,
    allDifficulties: List<String>,
    allEquipmentTypes: List<String>,
    onSetCountChange: (Int) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onEquipmentTypeChange: (String) -> Unit,
    onMuscleChange: (String) -> Unit,
    onCreateWarmupChange: (Boolean) -> Unit,
    onIsCommunityChange: (Boolean) -> Unit,
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
            isAdmin = isAdmin,
            allMuscles = allMuscles,
            allDifficulties = allDifficulties,
            allEquipmentTypes = allEquipmentTypes,
            workoutPlanState = workoutPlanState,
            onCreateWarmupChange = onCreateWarmupChange,
            onIsCommunityChange = onIsCommunityChange,
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
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.plan_a_workout),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutPlanningBody(
    isAdmin: Boolean,
    allDifficulties: List<String>,
    allEquipmentTypes: List<String>,
    allMuscles: List<MuscleGroup>,
    workoutPlanState: WorkoutPlanState,
    onSetCountChange: (Int) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onEquipmentTypeChange: (String) -> Unit,
    onMuscleChange: (String) -> Unit,
    onCreateWarmupChange: (Boolean) -> Unit,
    onIsCommunityChange: (Boolean) -> Unit,
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
            numbers = 2..6,
            onNumberChange = onSetCountChange)
        WarmupSwitch(checked = workoutPlanState.createWarmup, onCheckedChange = onCreateWarmupChange)
        MuscleFilter(
            muscleFilter = allMuscles.singleOrNull { it.id == workoutPlanState.muscleId }?.name ?: "",
            onMuscleFilterChange = onMuscleChange,
            allMuscles = allMuscles
        )
        DateChooser(selectedDate = workoutPlanState.targetDate, onClick = onShowTargetDateDialog)
        if (isAdmin)
            IsCommunityWorkoutSwitch(
                isCommunity = workoutPlanState.owner == "community",
                onChange = onIsCommunityChange
            )
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
        Text(text = "Plan warmup?", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun IsCommunityWorkoutSwitch(isCommunity: Boolean, onChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Workout plan for everyone?", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        Switch(
            checked = isCommunity,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun DateChooser(selectedDate: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null)
            Text(text = selectedDate, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlannedWorkoutEmpty() {
    EmptyScreen("No workout planned", "Create a plan to see here")
}

@Preview(showBackground = true)
@Composable
fun DateChooserPreview() {
    TextButton(onClick = { }) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null)
            Text(text = "2023-11-07", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}