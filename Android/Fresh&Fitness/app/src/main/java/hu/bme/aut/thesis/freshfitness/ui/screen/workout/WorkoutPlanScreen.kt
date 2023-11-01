package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.parseDateToString
import hu.bme.aut.thesis.freshfitness.ui.screen.todo.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.util.BackOnlineNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.NoConnectionNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.viewmodel.WorkoutPlanViewModel
import java.util.Locale

@Composable
fun WorkoutPlanScreen(viewModel: WorkoutPlanViewModel = viewModel()) {
    ConnectivityStatus(
        availableContent = {
            viewModel.onNetworkAvailable()
            LaunchedEffect(key1 = false) {
                if (viewModel.communityWorkouts.isEmpty())
                    viewModel.initScreen()
            }
        },
        unAvailableContent = { viewModel.onNetworkUnavailable() }
    )

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

    var showDetailsOfWorkout by remember { mutableStateOf(false) }
    var detailedWorkout: Workout? by remember { mutableStateOf(null) }
    val onWorkoutClick: (Workout) -> Unit = {
        detailedWorkout = it
        showDetailsOfWorkout = true
    }
    if (showDetailsOfWorkout) {
        DetailedWorkout(
            workout = detailedWorkout!!,
            onDisMiss = {
                showDetailsOfWorkout = false
                detailedWorkout = null
            }
        )
    }
    else {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            if (!viewModel.networkAvailable) {
                if (!viewModel.hasDataToShow) {
                    NetworkUnavailable()
                }
                else {
                    WorkoutsLoaded(communityWorkouts = viewModel.communityWorkouts, userWorkouts = viewModel.userWorkouts, onWorkoutClick = onWorkoutClick)
                }
            }
            else {
                if (viewModel.isLoading) {
                    WorkoutPlansLoading()
                }
                else {
                    WorkoutsLoaded(communityWorkouts = viewModel.communityWorkouts, userWorkouts = viewModel.userWorkouts, onWorkoutClick = onWorkoutClick)
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.workouts), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        IconButton(onClick = { /* TODO */ }) {
            Icon(imageVector = Icons.Filled.FilterAlt, contentDescription = null)
        }
    }
}

@Composable
fun WorkoutPlansLoading() {
    ScreenLoading(loadingText = stringResource(id = R.string.fetching_workouts))
}

@Composable
fun WorkoutsLoaded(
    communityWorkouts: List<Workout>,
    userWorkouts: List<Workout> = listOf(),
    onWorkoutClick: (Workout) -> Unit
) {
    WorkoutPlanScreenHeader()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (userWorkouts.isNotEmpty()) {
            WorkoutList(title = stringResource(R.string.your_workouts), workouts = userWorkouts, onWorkoutClick = onWorkoutClick)
        }
        WorkoutList(title = stringResource(R.string.daily_workouts), workouts = communityWorkouts, onWorkoutClick = onWorkoutClick)
    }
}

@Composable
fun PlanWorkoutDialog() {

}

//@Composable
//fun WorkoutFilter() {
//
//}

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
        if (showAny) {
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
            color = Color(0xff666666),
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onShowClick) {
            Icon(imageVector = if (listShown) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowLeft, tint = Color(0xff666666), contentDescription = null)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
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
            .height(100.dp)
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
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
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
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WorkoutBadge(text = workout.difficulty)
                    WorkoutBadge(text = "${workout.sets} sets")
                    WorkoutBadge(
                        text = "warmup",
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
                color = MaterialTheme.colorScheme.background,
                style = TextStyle.Default.copy(
                    drawStyle = Fill,
                    shadow = Shadow(Color.Black, Offset(4f, 4f), blurRadius = 3f)
                )
            )
        }
    }
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
    backGroundColor: Color = MaterialTheme.colorScheme.inversePrimary,
    fontColor: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: @Composable () -> Unit = { }
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(backGroundColor, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp)),
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
                fontSize = 10.sp
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
            //warmupExercises = mutableListOf(WorkoutExercise(exerciseId = 1, sequenceNum = 1, isWarmup = 1, amount = 30))
        ),
        onWorkoutClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun NoWorkoutsBannerPreview() {
    NoWorkoutsBanner()
}