package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.ui.screen.todo.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.screen.todo.UnderConstructionScreen
import hu.bme.aut.thesis.freshfitness.ui.util.BackOnlineNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilter
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilterChangers
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilters
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.NoConnectionNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.ui.util.exercises.MusclesAndEquipments
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.viewmodel.ExerciseBankViewModel

@Composable
fun ExerciseBankScreen(
    contentType: FreshFitnessContentType,
    viewModel: ExerciseBankViewModel = viewModel()
) {
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

        val filters by viewModel.filters.collectAsState()
        val filterChangers = ExerciseFilterChangers(
            onNameFilter = { viewModel.saveNameFilter(it) },
            clearNameFilter = { viewModel.clearNameFilter() },
            onApplyNewFilters = { difficulty, muscle, equipment -> viewModel.saveOtherFilters(difficulty, muscle, equipment) }
        )

        if (!viewModel.networkAvailable) {
            if (!viewModel.hasDataToShow)
                NetworkUnavailable()
            else {
                val musclesAndEquipments = MusclesAndEquipments(muscles = viewModel.muscleGroups, equipments = viewModel.equipments)
                ExerciseListLoaded(
                    contentType = contentType,
                    exercises = viewModel.filteredExercises,
                    filters = filters,
                    musclesAndEquipments = musclesAndEquipments,
                    filterChangers = filterChangers,
                    favourites = viewModel.favouriteExercises.map { it.id },
                    onClickHeart = { viewModel.heartExercise(it) }
                )
            }
        } else {
            if (viewModel.isLoading)
                ExerciseListLoading()
            else {
                val musclesAndEquipments = MusclesAndEquipments(muscles = viewModel.muscleGroups, equipments = viewModel.equipments)
                ExerciseListLoaded(
                    contentType = contentType,
                    exercises = viewModel.filteredExercises,
                    filters = filters,
                    musclesAndEquipments = musclesAndEquipments,
                    filterChangers = filterChangers,
                    favourites = viewModel.favouriteExercises.map { it.id },
                    onClickHeart = { viewModel.heartExercise(it) }
                )
            }
        }
    }
}

@Composable
fun ExerciseListLoading() {
    ScreenLoading(loadingText = stringResource(id = R.string.fetching_exercises))
}

@Composable
fun ExerciseListLoaded(
    contentType: FreshFitnessContentType,
    exercises: List<Exercise>,
    filters: ExerciseFilters,
    musclesAndEquipments: MusclesAndEquipments,
    filterChangers: ExerciseFilterChangers,
    favourites: List<Int>,
    onClickHeart: (Exercise) -> Unit
) {
    var showDetailsOfExercise: Boolean by remember { mutableStateOf(false) }
    var showFilterBottomSheet: Boolean by remember { mutableStateOf(false) }
    var detailedExercise: Exercise by remember { mutableStateOf(exercises.first()) }

    val onChooseExercise: (Exercise) -> Unit = {
        detailedExercise = it
        showDetailsOfExercise = true
    }

    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            ExerciseListLoadedList(
                exercises = exercises,
                filters = filters,
                musclesAndEquipments = musclesAndEquipments,
                filterChangers = filterChangers,
                favourites = favourites,
                onClickHeart = onClickHeart,
                showFilterBottomSheet = showFilterBottomSheet,
                changeShowFilterBottomSheet = { showFilterBottomSheet = it },
                onChooseExercise = onChooseExercise
            )
            if (showDetailsOfExercise)
                DetailedExercise(exercise = detailedExercise, onDismiss = { showDetailsOfExercise = false })
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            ExerciseListLoadedListAndDetail(
                exercises = exercises,
                filters = filters,
                musclesAndEquipments = musclesAndEquipments,
                filterChangers = filterChangers,
                favourites = favourites,
                onClickHeart = onClickHeart,
                showFilterBottomSheet = showFilterBottomSheet,
                changeShowFilterBottomSheet = { showFilterBottomSheet = it },
                onChooseExercise = onChooseExercise
            )
        }
    }
}

@Composable
fun ExerciseListLoadedList(
    exercises: List<Exercise>,
    filters: ExerciseFilters,
    musclesAndEquipments: MusclesAndEquipments,
    filterChangers: ExerciseFilterChangers,
    favourites: List<Int>,
    onClickHeart: (Exercise) -> Unit,
    showFilterBottomSheet: Boolean,
    changeShowFilterBottomSheet: (Boolean) -> Unit,
    onChooseExercise: (Exercise) -> Unit,
) {
    ExerciseListHeader(onFilterIconClick = { changeShowFilterBottomSheet(true) })
    ExerciseNameFilter(filters.name, filterChangers.onNameFilter, filterChangers.clearNameFilter)
    ExerciseList(
        exercises = exercises,
        onChooseExercise = onChooseExercise,
        favourites = favourites,
        onClickHeart = onClickHeart
    )
    if (showFilterBottomSheet) {
        ExerciseFilter(
            filters = filters,
            musclesAndEquipments = musclesAndEquipments,
            onApplyFilters = { difficulty, muscle, equipment ->
                filterChangers.onApplyNewFilters(difficulty, muscle, equipment)
                changeShowFilterBottomSheet(false) },
            onDismiss = { changeShowFilterBottomSheet(false) }
        )
    }
}

@Composable
fun ExerciseListLoadedListAndDetail(
    modifier: Modifier = Modifier,
    exercises: List<Exercise>,
    filters: ExerciseFilters,
    musclesAndEquipments: MusclesAndEquipments,
    filterChangers: ExerciseFilterChangers,
    favourites: List<Int>,
    onClickHeart: (Exercise) -> Unit,
    showFilterBottomSheet: Boolean,
    changeShowFilterBottomSheet: (Boolean) -> Unit,
    onChooseExercise: (Exercise) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = modifier.weight(1f)) {
            ExerciseListLoadedList(
                exercises = exercises,
                filters = filters,
                musclesAndEquipments = musclesAndEquipments,
                filterChangers = filterChangers,
                favourites = favourites,
                onClickHeart = onClickHeart,
                showFilterBottomSheet = showFilterBottomSheet,
                changeShowFilterBottomSheet = changeShowFilterBottomSheet,
                onChooseExercise = onChooseExercise
            )
        }
        Column(modifier = modifier.weight(1f)) {
            UnderConstructionScreen()
        }
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
        placeholder = { Text(stringResource(R.string.name_with_dots)) },
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
    onChooseExercise: (Exercise) -> Unit,
    favourites: List<Int>,
    onClickHeart: (Exercise) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
    ) {
        itemsIndexed(exercises) {index, it ->
            ExerciseRow(
                exercise = it,
                onClick = onChooseExercise,
                isFavourite = favourites.contains(it.id),
                onClickHeart = onClickHeart)
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
fun ExerciseRow(
    exercise: Exercise,
    onClick: (Exercise) -> Unit,
    isFavourite: Boolean,
    onClickHeart: (Exercise) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(exercise) },
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (exercise.muscleGroup != null) {
            S3Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .heightIn(min = 60.dp, max = 60.dp)
                    .widthIn(min = 60.dp, max = 60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(6.dp)),
                imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${exercise.muscleGroup!!.imgKey}"
            )
        }
        Text(
            text = exercise.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { onClickHeart(exercise) }) {
                Icon(imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = null)
            }
        }
    }
}