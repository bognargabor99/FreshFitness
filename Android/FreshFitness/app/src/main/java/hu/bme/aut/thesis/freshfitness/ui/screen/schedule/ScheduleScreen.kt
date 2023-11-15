package hu.bme.aut.thesis.freshfitness.ui.screen.schedule

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.getEquipmentsOfWorkout
import hu.bme.aut.thesis.freshfitness.isValidDate
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.DetailedExerciseEquipment
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.DetailedWorkout
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutOverviewRow
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.getWorkoutBackground
import hu.bme.aut.thesis.freshfitness.ui.util.BackOnlineNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.NoConnectionNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.Day
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.displayText
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.getWeekPageTitle
import hu.bme.aut.thesis.freshfitness.ui.util.calendar.rememberFirstVisibleWeekAfterScroll
import hu.bme.aut.thesis.freshfitness.viewmodel.ScheduleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun ScheduleScreen(
    date: String = LocalDate.now().toString(),
    contentType: FreshFitnessContentType,
    viewModel: ScheduleViewModel = viewModel()
) {
    ConnectivityStatus(
        availableContent = {
            viewModel.onNetworkAvailable()
            if (viewModel.exercises.isEmpty())
                LaunchedEffect(key1 = false) {
                    viewModel.initScreen()
                }
        },
        unAvailableContent = { viewModel.onNetworkUnavailable() }
    )

    var showDetailsOfWorkout by rememberSaveable { mutableStateOf(false) }
    var detailedWorkout: Workout? by rememberSaveable { mutableStateOf(null) }

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

        if (!viewModel.savedWorkoutsFetched) {
            ScheduleScreenLoading()
        }
        else {
            ScheduleScreenLoaded(
                showDetailsOfWorkout = showDetailsOfWorkout,
                detailedWorkout = detailedWorkout,
                date = if (isValidDate(date)) date else LocalDate.now().toString(),
                savedWorkouts = viewModel.savedWorkouts,
                viewEnabled = viewModel.hasDataToShow,
                onRefresh = { viewModel.getSavedWorkouts() },
                onClickWorkout = {
                    showDetailsOfWorkout = true
                    detailedWorkout = it
                },
                onDismissShowWorkout = {
                    showDetailsOfWorkout = false
                    detailedWorkout = null
                },
                contentType = contentType
            )
        }
    }
}

@Composable
fun ScheduleScreenLoading() {
    ScreenLoading(loadingText = stringResource(R.string.fetching_data))
}

@Composable
fun ScheduleScreenLoaded(
    showDetailsOfWorkout: Boolean,
    detailedWorkout: Workout?,
    date: String,
    savedWorkouts: List<Workout>,
    viewEnabled: Boolean,
    onRefresh: () -> Unit,
    onClickWorkout: (Workout) -> Unit,
    onDismissShowWorkout: () -> Unit,
    contentType: FreshFitnessContentType
) {
    val currentDate = remember { LocalDate.now() }
    var selection by remember { mutableStateOf(LocalDate.parse(date)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.25f)),
    ) {
        val coroutineScope = rememberCoroutineScope()
        when (contentType) {
            FreshFitnessContentType.LIST_ONLY -> {
                ScheduleScreenLoadedListOnly(
                    scope = coroutineScope,
                    showDetailsOfWorkout = showDetailsOfWorkout,
                    detailedWorkout = detailedWorkout,
                    savedWorkouts = savedWorkouts,
                    viewEnabled = viewEnabled,
                    onRefresh = onRefresh,
                    onClickWorkout = onClickWorkout,
                    onDismissShowWorkout = onDismissShowWorkout,
                    currentDate = currentDate,
                    selection = selection,
                    changeSelection = { selection = it }
                )
            }
            FreshFitnessContentType.LIST_AND_DETAIL -> {
                ScheduleScreenLoadedListAndDetail(
                    showDetailsOfWorkout = showDetailsOfWorkout,
                    detailedWorkout = detailedWorkout,
                    savedWorkouts = savedWorkouts,
                    viewEnabled = viewEnabled,
                    onRefresh = onRefresh,
                    onClickWorkout = onClickWorkout,
                    onDismissShowWorkout = onDismissShowWorkout,
                    selection = selection,
                    changeSelection = { selection = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreenLoadedListOnly(
    scope: CoroutineScope,
    showDetailsOfWorkout: Boolean,
    detailedWorkout: Workout?,
    savedWorkouts: List<Workout>,
    viewEnabled: Boolean,
    onRefresh: () -> Unit,
    onClickWorkout: (Workout) -> Unit,
    onDismissShowWorkout: () -> Unit,
    currentDate: LocalDate,
    selection: LocalDate,
    changeSelection: (LocalDate) -> Unit
) {
    val startDate = remember { currentDate.with(WeekFields.ISO.dayOfWeek(), 1) }
    val endDate = remember { currentDate.plusWeeks(1).with(WeekFields.ISO.dayOfWeek(), 7) }
    val pagerState = rememberPagerState(initialPage = DAYS.between(startDate, selection).absoluteValue.toInt())
    val weekCalendarState = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = selection,
    )
    if (showDetailsOfWorkout) {
        DetailedWorkout(
            workout = detailedWorkout!!,
            saveEnabled = false,
            onDismiss = onDismissShowWorkout
        )
    }
    else {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                changeSelection(currentDate.plusDays((page - (currentDate.dayOfWeek.value - 1)).toLong()))
                weekCalendarState.animateScrollToWeek(selection)
            }
        }
        UpperWeekCalendar(
            state = weekCalendarState,
            selection = selection,
            onSelectionChange = { clicked ->
                if (selection != clicked) { changeSelection(clicked) }
                scope.launch {
                    pagerState.animateScrollToPage(DAYS.between(startDate, clicked).absoluteValue.toInt())
                } },
            onRefresh = onRefresh
        )
        HorizontalPager(
            state = pagerState,
            pageCount = 14,
            beyondBoundsPageCount = 2
        ) {
            val viewedDate = currentDate.plusDays((it - (currentDate.dayOfWeek.value - 1)).toLong())
            val w = savedWorkouts.singleOrNull { sw -> sw.savedToDate.take(10) == viewedDate.toString() }
            if (w != null) {
                DayContent(
                    workout = w,
                    viewEnabled = viewEnabled,
                    onClick = { onClickWorkout(w) }
                )
            } else {
                NoWorkoutsForTheDay()
            }
        }
    }
}

@Composable
fun ScheduleScreenLoadedListAndDetail(
    showDetailsOfWorkout: Boolean,
    detailedWorkout: Workout?,
    modifier: Modifier = Modifier,
    savedWorkouts: List<Workout>,
    viewEnabled: Boolean,
    onRefresh: () -> Unit,
    onClickWorkout: (Workout) -> Unit,
    onDismissShowWorkout: () -> Unit,
    selection: LocalDate,
    changeSelection: (LocalDate) -> Unit
) {
    Row(modifier = modifier) {
        Column(modifier = modifier.weight(1f)) {
            FullScreenCalendar(
                onRefresh = onRefresh,
                selection = selection,
                changeSelection = {
                    onDismissShowWorkout()
                    changeSelection(it)
                }
            )
        }
        Column(modifier = modifier.weight(1f)) {
            if (showDetailsOfWorkout) {
                DetailedWorkout(
                    workout = detailedWorkout!!,
                    saveEnabled = false,
                    onDismiss = onDismissShowWorkout
                )
            }
            else {
                val w = savedWorkouts.singleOrNull { sw -> sw.savedToDate.take(10) == selection.toString() }
                if (w != null) {
                    DayContent(
                        workout = w,
                        viewEnabled = viewEnabled,
                        onClick = { onClickWorkout(w) }
                    )
                } else {
                    NoWorkoutsForTheDay()
                }
            }
        }
    }
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
    state: WeekCalendarState,
    selection: LocalDate,
    onSelectionChange: (LocalDate) -> Unit,
    onRefresh: () -> Unit
) {
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
        userScrollEnabled = true,
        dayContent = { day ->
            Day(day.date, isSelected = selection == day.date) { clicked ->
                onSelectionChange(clicked)
            }
        },
    )
}

@Composable
fun FullScreenCalendar(
    onRefresh: () -> Unit,
    selection: LocalDate,
    changeSelection: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val today = remember { LocalDate.now() }
    val daysOfWeek = remember { daysOfWeek() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CalendarTop(daysOfWeek = daysOfWeek,  selection = selection, onRefresh = onRefresh)
        FullScreenVerticalCalendar(
            state = state,
            today = today,
            selection = selection,
            changeSelection = changeSelection
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTop(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek>,
    selection: LocalDate,
    onRefresh: () -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TopAppBar(
                    title = { Text(text = selection.month.name.lowercase().replaceFirstChar { it.uppercase() }, color = MaterialTheme.colorScheme.background) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                    actions = {
                        IconButton(onClick = { onRefresh() }) {
                            Icon(imageVector = Icons.Filled.Refresh, tint = MaterialTheme.colorScheme.background, contentDescription = null)
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        text = dayOfWeek.displayText(),
                        fontSize = 15.sp,
                    )
                }
            }
        }
        Divider()
    }
}

@Composable
fun FullScreenVerticalCalendar(
    state: CalendarState,
    today: LocalDate,
    selection: LocalDate,
    changeSelection: (LocalDate) -> Unit
) {
    VerticalCalendar(
        state = state,
        contentPadding = PaddingValues(bottom = 100.dp),
        dayContent = { value ->
            FullScreenDay(
                day = value,
                today = today,
                selection = selection,
                onClick = changeSelection
            )
        },
        monthHeader = { month -> MonthHeader(month) },
    )
}

@Composable
private fun MonthHeader(calendarMonth: CalendarMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = calendarMonth.yearMonth.displayText(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun FullScreenDay(
    day: CalendarDay,
    today: LocalDate,
    selection: LocalDate,
    onClick: (LocalDate) -> Unit
) {
    val isInMonth: Boolean = day.position == DayPosition.MonthDate
    Box(
        modifier = Modifier
            .aspectRatio(3f / 4f),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(CircleShape)
                .clickable(
                    enabled = isInMonth,
                    onClick = { onClick(day.date) },
                )
                .background(color = if (isInMonth && selection == day.date) MaterialTheme.colorScheme.inversePrimary else Color.Transparent, shape = CircleShape)
                .border(
                    width = 0.dp,
                    color = if (isInMonth && today == day.date) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isInMonth) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun DayContent(
    workout: Workout,
    viewEnabled: Boolean,
    onClick: () -> Unit
) {
    val equipments = getEquipmentsOfWorkout(workout)
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                DayWorkoutTitle(muscle = workout.targetMuscle?.name ?: "")
                WorkoutOverview(workout)
                if (equipments.isNotEmpty())
                    NeededEquipments(equipments)
                ViewDaysWorkoutButton(onClick = onClick, viewEnabled = viewEnabled)
            }
        }
    }
}

@Composable
fun ViewDaysWorkoutButton(modifier: Modifier = Modifier, onClick: () -> Unit, viewEnabled: Boolean) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight()
    ) {
        Button(modifier = Modifier.fillMaxWidth(), enabled = viewEnabled, onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inversePrimary)) {
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