package hu.bme.aut.thesis.freshfitness.ui.screen.progress

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.getEquipmentsOfWorkout
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.DetailedExerciseEquipment
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.DetailedWorkout
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutOverviewRow
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.getWorkoutBackground
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.Day
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.getWeekPageTitle
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.rememberFirstVisibleWeekAfterScroll
import hu.bme.aut.thesis.freshfitness.viewmodel.ProgressViewModel
import java.time.LocalDate
import java.util.Locale

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel()) {
    LaunchedEffect(key1 = false) {
        if (viewModel.exercises.isEmpty())
            viewModel.initScreen()
    }

    var showDetailsOfWorkout by rememberSaveable { mutableStateOf(false) }
    var detailedWorkout: Workout? by rememberSaveable { mutableStateOf(null) }

    if (!viewModel.savedWorkoutsFetched) {
        ProgressScreenLoading()
    }
    else {
        if (showDetailsOfWorkout) {
            DetailedWorkout(
                workout = detailedWorkout!!,
                saveEnabled = false,
                onDismiss = {
                    showDetailsOfWorkout = false
                    detailedWorkout = null
                })
        }
        else {
            ProgressScreenLoaded(
                savedWorkouts = viewModel.savedWorkouts,
                viewEnabled = viewModel.hasDataToShow,
                onRefresh = { viewModel.getSavedWorkouts() },
                onClickWorkout = {
                    showDetailsOfWorkout = true
                    detailedWorkout = it
                }
            )
        }
    }
}

@Composable
fun ProgressScreenLoading() {
    ScreenLoading(loadingText = stringResource(R.string.fetching_data))
}

@Composable
fun ProgressScreenLoaded(
    savedWorkouts: List<Workout>,
    viewEnabled: Boolean,
    onRefresh: () -> Unit,
    onClickWorkout: (Workout) -> Unit,
) {
    val currentDate = remember { LocalDate.now() }
    val startDate = remember { currentDate.minusDays(365) }
    val endDate = remember { currentDate.plusDays(35) }
    var selection by remember { mutableStateOf(currentDate) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        UpperWeekCalendar(
            currentDate = currentDate,
            startDate = startDate,
            endDate = endDate,
            selection = selection,
            onSelectionChange = { clicked -> if (selection != clicked) { selection = clicked } },
            onRefresh = onRefresh
        )
        if (!viewEnabled) {
            LoadingDayContent()
        }
        else {
            val w = savedWorkouts.singleOrNull { it.date.take(10) == selection.toString() }
            if (w != null) {
                DayContent(workout = w, onClick = { onClickWorkout(w) })
            } else {
                NoWorkoutsForTheDay()
            }
        }
    }
}

@Composable
fun LoadingDayContent() {
    ScreenLoading(backgroundColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f), loadingText = stringResource(R.string.loading_workout))
}

@Composable
fun NoWorkoutsForTheDay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.25f))
            .wrapContentSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.SentimentVeryDissatisfied,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = stringResource(R.string.no_workouts_scheduled))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpperWeekCalendar(
    currentDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    selection: LocalDate,
    onSelectionChange: (LocalDate) -> Unit,
    onRefresh: () -> Unit
) {
    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
    )
    val visibleWeek = rememberFirstVisibleWeekAfterScroll(state)
    TopAppBar(
        title = { Text(text = getWeekPageTitle(visibleWeek), color = MaterialTheme.colorScheme.background) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
        actions = {
            IconButton(onClick = { onRefresh() }) {
                Icon(imageVector = Icons.Filled.Refresh, tint = MaterialTheme.colorScheme.background, contentDescription = null)
            }
        }
    )
    WeekCalendar(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
        state = state,
        dayContent = { day ->
            Day(day.date, isSelected = selection == day.date) { clicked ->
                onSelectionChange(clicked)
            }
        },
    )
}

@Composable
fun DayContent(
    workout: Workout,
    onClick: () -> Unit
) {
    val equipments = getEquipmentsOfWorkout(workout)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        DayContentBackground(imageRes = getWorkoutBackground(workout))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.25f))
                .padding(horizontal = 12.dp)
                .align(Alignment.TopCenter),
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayWorkoutTitle(muscle = workout.targetMuscle!!.name)
                WorkoutOverview(workout)
                if (equipments.isNotEmpty())
                    NeededEquipments(equipments)
                ViewDaysWorkoutButton(onClick = onClick)
            }
        }
    }
}

@Composable
fun ViewDaysWorkoutButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight()
    ) {
        Button(modifier = Modifier.fillMaxWidth(), onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)) {
            Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.view_workout), textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun DayContentBackground(
    @DrawableRes imageRes: Int
) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(),
        colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply {
            this.setToSaturation(0f)
        }),
        alpha = 0.4f,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun DayWorkoutTitle(muscle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = muscle,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun WorkoutOverview(
    workout: Workout
) {
    Column(
        modifier = Modifier
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.Speed, title = "Difficulty") {
            Text(text = workout.difficulty.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                Locale.ROOT) else it.toString() })
        }
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.FitnessCenter, title = "Equipments") {
            val needEquipment = workout.exercises.any { it.exercise?.equipment != null && it.exercise?.equipment?.type != "none" }
            Text(text = if (needEquipment) "Needs equipment" else "No equipment")
        }
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.Accessibility, title = "Warmup") {
            val hasWarmup = workout.warmupExercises.any()
            Text(text = if (hasWarmup) "Planned" else "Not planned")
        }
        WorkoutOverviewRow(modifier = Modifier.weight(1f), imageVector = Icons.Filled.RotateRight, title = "Sets") {
            Text(text = "${workout.sets} sets w/ ${workout.exercises.size} exercises")
        }
    }

}

@Composable
fun NeededEquipments(equipments: List<Equipment>) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        EquipmentsHeader()
        EquipmentsBody(equipments = equipments)
    }
}

@Composable
fun EquipmentsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.equipments), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EquipmentsBody(equipments: List<Equipment>) {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        equipments.forEach {
            DetailedExerciseEquipment(equipment = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    ProgressScreenLoaded(savedWorkouts = listOf(), viewEnabled = false, onRefresh = { }, onClickWorkout = { })
}
