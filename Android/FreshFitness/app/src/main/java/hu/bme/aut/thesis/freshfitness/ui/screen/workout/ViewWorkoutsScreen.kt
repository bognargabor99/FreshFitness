package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.state.WorkoutPlanState
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.ui.screen.nocontent.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.TargetDatePicker
import hu.bme.aut.thesis.freshfitness.ui.util.TargetTimePicker
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.util.parseDateToString
import hu.bme.aut.thesis.freshfitness.viewmodel.ViewWorkoutsViewModel
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ViewWorkoutsScreen(
    contentType: FreshFitnessContentType,
    networkAvailable: Boolean,
    viewModel: ViewWorkoutsViewModel = viewModel()
) {
    LaunchedEffect(key1 = networkAvailable) {
        if (networkAvailable && viewModel.communityWorkouts.isEmpty())
            viewModel.initScreen()
    }

    val workoutPlanState by viewModel.workoutPlanState.collectAsState()
    var planWorkout by remember { mutableStateOf(false) }
    var showDetailsOfWorkout by remember { mutableStateOf(false) }
    var detailedWorkout: Workout? by remember { mutableStateOf(null) }
    val onWorkoutClick: (Workout) -> Unit = {
        detailedWorkout = it
        showDetailsOfWorkout = true
    }

    var showDateChooser by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var chosenDate by remember { mutableStateOf(LocalDate.now().toString()) }

    var permissionGranted by remember { mutableStateOf(false) }
    val permission = rememberPermissionState(
        permission = Manifest.permission.WRITE_CALENDAR,
        onPermissionResult = { permissionGranted = it }
    )

    val onDismissShowDetails: () -> Unit = {
        showDetailsOfWorkout = false
    }
    val onSaveWorkout: () -> Unit = { showDateChooser = true }
    val onPlanWorkout: () -> Unit = { planWorkout = true }
    val context = LocalContext.current

    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            if ((viewModel.planningWorkout && viewModel.plannedWorkout != null) || planWorkout) {
                PlanWorkoutListOnly(
                    planWorkoutChange = { planWorkout = it },
                    workoutPlanState = workoutPlanState,
                    planWorkout = planWorkout,
                    viewModel = viewModel
                )
            } else {
                ViewWorkoutsScreenListOnly(
                    networkAvailable = networkAvailable,
                    showDetailsOfWorkout = showDetailsOfWorkout,
                    detailedWorkout = detailedWorkout,
                    onDismissShowDetails = onDismissShowDetails,
                    permissionState = permission,
                    onSaveWorkout = onSaveWorkout,
                    onWorkoutClick = onWorkoutClick,
                    onPlanWorkout = onPlanWorkout,
                    viewModel = viewModel
                )
            }
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            if ((viewModel.planningWorkout && viewModel.plannedWorkout != null) || planWorkout) {
                PlanWorkoutListAndDetail(
                    viewModel = viewModel,
                    planWorkoutChange = { planWorkout = it },
                    workoutPlanState = workoutPlanState
                )
            } else {
                ViewWorkoutsScreenListAndDetail(
                    modifier = Modifier.fillMaxSize(),
                    networkAvailable = networkAvailable,
                    permissionState = permission,
                    detailedWorkout = detailedWorkout,
                    viewModel = viewModel,
                    onWorkoutClick = onWorkoutClick,
                    onPlanWorkout = onPlanWorkout,
                    onSaveWorkout = onSaveWorkout
                )
            }
        }
    }

    if (showDateChooser) {
        TargetDatePicker(
            selectedDate = chosenDate,
            blockedDates = viewModel.getBlockedDates(),
            onSelectDate = { date ->
                chosenDate = date
                showDateChooser = false
                showTimePicker = true
            },
            onDismiss = { showDateChooser = false }
        )
    }
    if (showTimePicker) {
        TargetTimePicker(
            onDismiss = { showTimePicker = false },
            onSelectTime = { h, m ->
                showTimePicker = false
                viewModel.saveWorkout(context = context, workout = detailedWorkout!!, dateToSave = chosenDate, hour = h, minute = m)
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ViewWorkoutsScreenListOnly(
    networkAvailable: Boolean,
    showDetailsOfWorkout: Boolean,
    detailedWorkout: Workout?,
    onDismissShowDetails: () -> Unit,
    permissionState: PermissionState,
    onSaveWorkout: () -> Unit,
    onWorkoutClick: (Workout) -> Unit,
    onPlanWorkout: () -> Unit,
    viewModel: ViewWorkoutsViewModel
) {
    if (showDetailsOfWorkout) {
        LaunchedEffect(true) {
            permissionState.launchPermissionRequest()
        }
        DetailedWorkout(
            workout = detailedWorkout!!,
            onDismiss = onDismissShowDetails,
            isSaved = viewModel.savedWorkouts.any { it.id == detailedWorkout.id },
            onSave = onSaveWorkout,
            onDelete = { },
            saveEnabled = true,
            deleteEnabled = false
        )
        
    }
    else {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            if (!networkAvailable) {
                if (!viewModel.hasDataToShow) {
                    NetworkUnavailable()
                }
                else {
                    WorkoutsLoaded(canCreateWorkout = false, communityWorkouts = viewModel.communityWorkouts, userWorkouts = viewModel.userWorkouts, onWorkoutClick = onWorkoutClick, onPlanWorkout = onPlanWorkout)
                }
            }
            else {
                if (viewModel.isLoading) {
                    ViewWorkoutsLoading(FreshFitnessContentType.LIST_ONLY)
                }
                else {
                    WorkoutsLoaded(canCreateWorkout = viewModel.isLoggedIn, communityWorkouts = viewModel.communityWorkouts, userWorkouts = viewModel.userWorkouts, onWorkoutClick = onWorkoutClick, onPlanWorkout = onPlanWorkout)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ViewWorkoutsScreenListAndDetail(
    modifier: Modifier = Modifier,
    networkAvailable: Boolean,
    permissionState: PermissionState,
    detailedWorkout: Workout?,
    viewModel: ViewWorkoutsViewModel,
    onWorkoutClick: (Workout) -> Unit,
    onPlanWorkout: () -> Unit,
    onSaveWorkout: () -> Unit
) {
    if (!networkAvailable && !viewModel.hasDataToShow) {
        NetworkUnavailable()
    }
    else if (viewModel.isLoading) {
        ViewWorkoutsLoading(contentType = FreshFitnessContentType.LIST_AND_DETAIL)
    }
    else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = modifier.weight(1f)) {
                WorkoutsLoaded(canCreateWorkout = viewModel.isLoggedIn, communityWorkouts = viewModel.communityWorkouts, userWorkouts = viewModel.userWorkouts, onWorkoutClick = onWorkoutClick, onPlanWorkout = onPlanWorkout)
            }
            Column(modifier = modifier.weight(1f)) {
                if (detailedWorkout != null) {
                    LaunchedEffect(true) {
                        permissionState.launchPermissionRequest()
                    }
                    DetailedWorkout(
                        workout = detailedWorkout,
                        onDismiss = { },
                        isSaved = viewModel.savedWorkouts.any { it.id == detailedWorkout.id },
                        onSave = onSaveWorkout,
                        onDelete = { },
                        saveEnabled = true,
                        deleteEnabled = false
                    )
                } else {
                    DetailedWorkoutEmpty()
                }
            }
        }
    }
}

@Composable
fun PlanWorkoutListOnly(
    planWorkoutChange: (Boolean) -> Unit,
    workoutPlanState: WorkoutPlanState,
    planWorkout: Boolean,
    viewModel: ViewWorkoutsViewModel
) {
    if (viewModel.planningWorkout && viewModel.plannedWorkout != null) {
        WorkoutPlanReviewScreen(
            workout = viewModel.plannedWorkout as Workout,
            onNewPlan = { viewModel.createWorkoutPlan() },
            onAccept = {
                planWorkoutChange(false)
                viewModel.createWorkout()
            },
            onCancel = {
                planWorkoutChange(false)
                viewModel.cancelWorkoutCreation()
            })
    }
    else if (planWorkout) {
        PlanWorkoutScreen(
            workoutPlanState = workoutPlanState,
            allMuscles = viewModel.muscleGroups,
            allDifficulties = viewModel.allDifficulties,
            allEquipmentTypes = viewModel.allEquipmentTypes,
            onSetCountChange = viewModel::onSetCountChange,
            onDifficultyChange = viewModel::onDifficultyChange,
            onEquipmentTypeChange = viewModel::onEquipmentTypeChange,
            onMuscleChange = viewModel::onMuscleChange,
            onCreateWarmupChange = viewModel::onCreateWarmupChange,
            onTargetDateChange = viewModel::onTargetDateChange,
            isCreationEnabled = viewModel.isCreationEnabled(),
            onCreateWorkout = viewModel::createWorkoutPlan,
            onDismiss = { planWorkoutChange(false) })
    }
}

@Composable
fun PlanWorkoutListAndDetail(
    modifier: Modifier = Modifier,
    viewModel: ViewWorkoutsViewModel,
    workoutPlanState: WorkoutPlanState,
    planWorkoutChange: (Boolean) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = modifier.weight(1f)) {
            PlanWorkoutScreen(
                workoutPlanState = workoutPlanState,
                allMuscles = viewModel.muscleGroups,
                allDifficulties = viewModel.allDifficulties,
                allEquipmentTypes = viewModel.allEquipmentTypes,
                onSetCountChange = viewModel::onSetCountChange,
                onDifficultyChange = viewModel::onDifficultyChange,
                onEquipmentTypeChange = viewModel::onEquipmentTypeChange,
                onMuscleChange = viewModel::onMuscleChange,
                onCreateWarmupChange = viewModel::onCreateWarmupChange,
                onTargetDateChange = viewModel::onTargetDateChange,
                isCreationEnabled = viewModel.isCreationEnabled(),
                onCreateWorkout = viewModel::createWorkoutPlan,
                onDismiss = { planWorkoutChange(false) })
        }
        Column(modifier = modifier.weight(1f)) {
            if (viewModel.planningWorkout && viewModel.plannedWorkout != null) {
                WorkoutPlanReviewScreen(
                    workout = viewModel.plannedWorkout as Workout,
                    onNewPlan = { viewModel.createWorkoutPlan() },
                    onAccept = {
                        planWorkoutChange(false)
                        viewModel.createWorkout()
                    },
                    onCancel = {
                        planWorkoutChange(false)
                        viewModel.cancelWorkoutCreation()
                    })
            } else {
                PlannedWorkoutEmpty()
            }
        }
    }
}

@Composable
fun ViewWorkoutsScreenHeader(
    loading: Boolean = false,
    canCreateWorkout: Boolean,
    onClickAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.workouts), color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if (canCreateWorkout)
            IconButton(onClick = onClickAdd) {
                Icon(imageVector = Icons.Filled.Add, tint = if (!loading) MaterialTheme.colorScheme.onBackground else Color.Transparent, contentDescription = null)
            }
    }
}

@Composable
fun ViewWorkoutsLoading(contentType: FreshFitnessContentType) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.49f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            LoadingWorkoutListListOnly(alpha = alpha)
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            LoadingWorkoutListListAndDetail(alpha = alpha)
        }
    }
}

@Composable
fun LoadingWorkoutListListOnly(alpha: Float) {
    LoadingWorkoutList(alpha = alpha)
}

@Composable
fun LoadingWorkoutListListAndDetail(alpha: Float) {
    Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            LoadingWorkoutList(alpha = alpha)
        }
        Column(modifier = Modifier.weight(1f)) {
            LoadingDetailedWorkout(alpha)
        }
    }
}

@Composable
fun WorkoutsLoaded(
    canCreateWorkout: Boolean,
    communityWorkouts: List<Workout>,
    userWorkouts: List<Workout> = listOf(),
    onWorkoutClick: (Workout) -> Unit,
    onPlanWorkout: () -> Unit
) {
    ViewWorkoutsScreenHeader(
        canCreateWorkout = canCreateWorkout,
        onClickAdd = onPlanWorkout
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (userWorkouts.isNotEmpty()) {
            WorkoutList(title = stringResource(R.string.your_workouts), workouts = userWorkouts.sortedByDescending { it.date }, onWorkoutClick = onWorkoutClick)
        }
        WorkoutList(title = stringResource(R.string.daily_workouts), workouts = communityWorkouts.sortedByDescending { it.date }, onWorkoutClick = onWorkoutClick)
    }
}

@Composable
fun WorkoutList(
    title: String,
    workouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit
) {
    var showAny: Boolean by remember { mutableStateOf(true) }
    var showAll: Boolean by remember { mutableStateOf(false) }
    Column {
        WorkoutTitle(title = title, listShown = showAny, onShowClick = { showAny = !showAny })
        AnimatedVisibility(visible = showAny) {
            Column {
                if (workouts.any()) {
                    val count = if (workouts.size >= 3) { if (!showAll) 3 else workouts.size } else { workouts.size }
                    for (i in 0 until count)
                        WorkoutRow(
                            modifier = Modifier,
                            workout = workouts[i],
                            onWorkoutClick = onWorkoutClick
                        )
                    if (workouts.size >= 3)
                        WorkoutShowMoreOrLess(showMore = !showAll, onClick = { showAll = !showAll})
                } else
                    NoWorkoutsBanner()
            }
        }
    }
}

@Composable
fun WorkoutTitle(title: String, listShown: Boolean, onShowClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = title.uppercase(),
            color = Color(0xff888888),
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onShowClick) {
            Icon(imageVector = if (listShown) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft, tint = Color(0xff888888), contentDescription = null)
        }
    }
}

@OptIn(ExperimentalTextApi::class, ExperimentalLayoutApi::class)
@Composable
fun WorkoutRow(
    modifier: Modifier = Modifier,
    workout: Workout,
    onWorkoutClick: (Workout) -> Unit
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 100.dp)
            .clickable { onWorkoutClick(workout) }
            .clip(RoundedCornerShape(8.dp))
            .border(3.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = getWorkoutBackground(workout)),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { setToScale(0.6f,0.6f,0.6f,1f) }),
            contentDescription = null)
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            S3Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .heightIn(min = 80.dp, max = 80.dp)
                    .widthIn(min = 80.dp, max = 80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)),
                imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${workout.targetMuscle!!.imgKey}",
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { setToSaturation(0f) })
            )
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = workout.targetMuscle!!.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = TextStyle.Default.copy(
                            drawStyle = Fill,
                            shadow = Shadow(Color.Black, Offset(4f, 4f), blurRadius = 3f)
                        )
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkoutBadge(text = workout.difficulty, fontSize = 10.sp)
                    WorkoutBadge(text = "${workout.sets} sets", fontSize = 10.sp)
                    WorkoutBadge(
                        text = "warmup",
                        fontSize = 10.sp,
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.heightIn(min = 14.dp, max = 14.dp),
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = if (workout.warmupExercises.any()) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null
                            )
                        }
                    )
                }

            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                text = parseDateToString(workout.date.take(16)),
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle.Default.copy(
                    drawStyle = Fill,
                    shadow = Shadow(Color.Black, Offset(4f, 4f), blurRadius = 3f)
                )
            )
        }
    }
}

@Composable
fun LoadingWorkoutList(alpha: Float) {
    Column {
        ViewWorkoutsScreenHeader(loading = true, canCreateWorkout = true, onClickAdd = { })
        Column {
            repeat(2) {
                LoadingWorkoutListTitle(alpha = alpha)
                repeat(3) {
                    LoadingWorkoutRow(alpha = alpha)
                }
            }
        }
    }
}

@Composable
fun LoadingWorkoutListTitle(alpha: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.4f)
                .heightIn(18.dp, 18.dp)
                .background(Color.Gray.copy(alpha = alpha), RoundedCornerShape(6.dp))
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoadingWorkoutRow(alpha: Float) {
    val length by remember { mutableStateOf(Random.nextFloat() * 0.3f + 0.4f) }
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray.copy(alpha = alpha), shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .sizeIn(80.dp, 80.dp, 80.dp, 80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Gray.copy(alpha = alpha), RoundedCornerShape(6.dp)),
            )
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(length)
                        .heightIn(18.dp, 18.dp)
                        .background(Color.Gray.copy(alpha = alpha), RoundedCornerShape(4.dp))
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { LoadingWorkoutBadge(alpha) }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .heightIn(14.dp, 14.dp)
                        .padding(end = 4.dp)
                        .background(Color.Gray.copy(alpha = alpha), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun LoadingWorkoutBadge(alpha: Float) {
    val length by remember { mutableStateOf(Random.nextInt(50, 70)) }
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .width(length.dp)
            .height(14.dp)
            .background(Color.Gray.copy(alpha = alpha), shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun WorkoutShowMoreOrLess(showMore: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { onClick() }) {
            Text(text = stringResource(id = if (showMore) R.string.show_more else R.string.show_less))
        }
    }
}

@Composable
fun NoWorkoutsBanner() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.no_workouts_found),
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun WorkoutBadge(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 12.sp,
    backGroundColor: Color = MaterialTheme.colorScheme.inversePrimary,
    fontColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: @Composable () -> Unit = { }
) {
    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
            .wrapContentSize()
            .background(backGroundColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon()
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp)),
                text = text.uppercase(Locale.ROOT),
                color = fontColor,
                fontSize = fontSize
            )
        }
    }
}

fun getWorkoutBackground(workout: Workout): Int {
    return when (workout.equipmentTypes) {
        "gym" -> R.drawable.gym
        "calisthenics" -> R.drawable.calisthenics
        else -> R.drawable.none
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutTitlePreview() {
    WorkoutTitle(title = "Daily workouts", listShown = true, onShowClick = { })
}

@Preview(showBackground = true)
@Composable
fun WorkoutShowMoreOrLessPreview() {
    WorkoutShowMoreOrLess(showMore = true) { }
}

@Preview(showBackground = true)
@Composable
fun WorkoutRowPreview() {
    WorkoutRow(
        workout = Workout(
            muscleId = 3,
            targetMuscle = MuscleGroup(id = 3, name = "Back & Biceps", imgKey = "public/images/workout/musclegroups/target_muscle_backbiceps.jpg"),
            sets = 4,
            difficulty = "beginner",
            date = "2023-10-31T00:00:00.000Z".take(16),
            owner = "community",
            equipmentTypes = "gym",
        ),
        onWorkoutClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun NoWorkoutsBannerPreview() {
    NoWorkoutsBanner()
}

@Preview(showBackground = true)
@Composable
fun LoadingWorkoutRowPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.49f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    LoadingWorkoutRow(alpha)
}

@Preview(showBackground = true)
@Composable
fun LoadingWorkoutListListOnlyPreview() {
    ViewWorkoutsLoading(FreshFitnessContentType.LIST_ONLY)
}

@Preview(showBackground = true)
@Composable
fun LoadingWorkoutListListAndDetailPreview() {
    ViewWorkoutsLoading(FreshFitnessContentType.LIST_AND_DETAIL)
}