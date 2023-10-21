package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.ui.screen.todo.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilter
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.viewmodel.ExerciseBankViewModel
import java.util.Locale

@Composable
fun ExerciseBankScreen(viewModel: ExerciseBankViewModel = viewModel()) {
    ConnectivityStatus(
        availableContent = {
            viewModel.onNetworkAvailable()
            if (viewModel.exercises.isEmpty())
                LaunchedEffect(key1 = false) {
                    viewModel.fetchData()
                }
        },
        unAvailableContent = { viewModel.onNetworkUnavailable() }
    )

    Column {
        AnimatedVisibility(
            visible = !viewModel.networkAvailable && viewModel.hasDataToShow,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            NoConnectionNotification()
        }

        AnimatedVisibility(
            visible = viewModel.showBackOnline,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            BackOnlineNotification()
        }

        if (!viewModel.networkAvailable) {
            if (!viewModel.hasDataToShow)
                NetworkUnavailable()
            else {
                ExerciseListLoaded(
                    exercises = viewModel.filteredExercises,
                    nameFilter = viewModel.nameFilter,
                    muscleFilter = viewModel.muscleFilter,
                    equipmentFilter = viewModel.equipmentFilter,
                    allMuscles = viewModel.muscleGroups,
                    allEquipments = viewModel.equipments,
                    onNameFilter = { viewModel.saveNameFilter(it) },
                    clearNameFilter = { viewModel.clearNameFilter() },
                    onApplyNewFilters = { muscle, equipment -> viewModel.saveOtherFilters(muscle, equipment) }
                )
            }
        } else {
            if (viewModel.isLoading)
                ExerciseListLoading()
            else
                ExerciseListLoaded(
                    exercises = viewModel.filteredExercises,
                    nameFilter = viewModel.nameFilter,
                    muscleFilter = viewModel.muscleFilter,
                    equipmentFilter = viewModel.equipmentFilter,
                    allMuscles = viewModel.muscleGroups,
                    allEquipments = viewModel.equipments,
                    onNameFilter = { viewModel.saveNameFilter(it) },
                    clearNameFilter = { viewModel.clearNameFilter() },
                    onApplyNewFilters = { muscle, equipment -> viewModel.saveOtherFilters(muscle, equipment) }
                )
        }
    }
}

@Composable
fun ExerciseListLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfiniteCircularProgressBar()
            Text(
                text = stringResource(R.string.fetching_exercises),
                fontStyle = FontStyle.Italic,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ExerciseListLoaded(
    exercises: List<Exercise>,
    nameFilter: String,
    muscleFilter: String,
    equipmentFilter: String,
    allMuscles: List<MuscleGroup>,
    allEquipments: List<Equipment>,
    onNameFilter: (String) -> Unit,
    clearNameFilter: () -> Unit,
    onApplyNewFilters: (muscle: String, equipment: String) -> Unit
) {
    var showDetailsOfExercise by remember { mutableStateOf(false) }
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    var detailedExercise: Exercise? by remember { mutableStateOf(null) }
    ExerciseListHeader(onFilterIconClick = { showFilterBottomSheet = true })
    ExerciseNameFilter(nameFilter, onNameFilter, clearNameFilter)
    ExerciseList(
        exercises = exercises,
        onChooseExercise = {
            detailedExercise = it
            showDetailsOfExercise = true
        }
    )
    if (showDetailsOfExercise)
        detailedExercise?.let { DetailedExercise(exercise = it, onDismiss = { showDetailsOfExercise = false }) }
    if (showFilterBottomSheet) {
        ExerciseFilter(
            muscleFilter = muscleFilter,
            equipmentFilter = equipmentFilter,
            allMuscles = allMuscles,
            allEquipments = allEquipments,
            onApplyFilters = { muscle, equipment ->
                onApplyNewFilters(muscle, equipment)
                showFilterBottomSheet = false },
            onDismiss = { showFilterBottomSheet = false }
        )
    }
}

@Composable
fun ExerciseListHeader(
    onFilterIconClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.exercises), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        IconButton(onClick = onFilterIconClick) {
            Icon(imageVector = Icons.Filled.FilterAlt, contentDescription = null)
        }
    }
}

@Composable
fun ExerciseNameFilter(nameFilter: String, onNameFilter: (String) -> Unit, clearNameFilter: () -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        value = nameFilter,
        onValueChange = onNameFilter,
        singleLine = true,
        placeholder = { Text("Name...") },
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable { clearNameFilter() },
                imageVector = Icons.Filled.Cancel,
                contentDescription = null
            )
        }
    )
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    onChooseExercise: (Exercise) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
    ) {
        itemsIndexed(exercises) {index, it ->
            Exercise(exercise = it, onClick = onChooseExercise)
            if (exercises.size != index + 1)
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
        }
    }
}

@Composable
fun Exercise(
    exercise: Exercise,
    onClick: (Exercise) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(exercise) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = exercise.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NoConnectionNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(red = 0.5f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_connection),
            color = Color.White
        )
    }
}

@Composable
fun BackOnlineNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green.copy(green = 0.5f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.back_online),
            color = Color.White
        )
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