package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.ui.screen.nocontent.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilter
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilterChangers
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilters
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.exercises.MusclesAndEquipments
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image
import hu.bme.aut.thesis.freshfitness.viewmodel.ExerciseBankViewModel
import kotlin.random.Random

@Composable
fun ExerciseBankScreen(
    contentType: FreshFitnessContentType,
    networkAvailable: Boolean,
    viewModel: ExerciseBankViewModel = viewModel()
) {
    LaunchedEffect(key1 = networkAvailable) {
        if (networkAvailable && viewModel.exercises.isEmpty())
            viewModel.fetchData()
    }

    Column {
        val filters by viewModel.filters.collectAsState()
        val filterChangers = ExerciseFilterChangers(
            onNameFilter = { viewModel.saveNameFilter(it) },
            clearNameFilter = { viewModel.clearNameFilter() },
            onApplyNewFilters = { difficulty, muscle, equipment -> viewModel.saveOtherFilters(difficulty, muscle, equipment) }
        )

        if (!networkAvailable) {
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
                    onClickHeart = viewModel::heartExercise
                )
            }
        } else {
            if (viewModel.isLoading)
                ExerciseListLoading(contentType = contentType)
            else {
                val musclesAndEquipments = MusclesAndEquipments(muscles = viewModel.muscleGroups, equipments = viewModel.equipments)
                ExerciseListLoaded(
                    contentType = contentType,
                    exercises = viewModel.filteredExercises,
                    filters = filters,
                    musclesAndEquipments = musclesAndEquipments,
                    filterChangers = filterChangers,
                    favourites = viewModel.favouriteExercises.map { it.id },
                    onClickHeart = viewModel::heartExercise
                )
            }
        }
    }
}

@Composable
fun ExerciseListLoading(
    contentType: FreshFitnessContentType
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            ExerciseListLoadingListOnly(alpha)
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            ExerciseListLoadingListAndDetail(alpha)
        }
    }
}

@Composable
fun ExerciseListLoadingListOnly(alpha: Float) {
    Column {
        ExerciseListHeader(filterEnabled = false)
        ExerciseNameFilter(
            enabled = false,
            nameFilter = "",
            onNameFilter = {},
            clearNameFilter = {})
        LoadingExerciseList(alpha)
    }
}

@Composable
fun ExerciseListLoadingListAndDetail(alpha: Float) {
    Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            ExerciseListLoadingListOnly(alpha)
        }
        Column(modifier = Modifier.weight(1f)) {
            LoadingDetailedExercise(alpha)
        }
    }
}

@Composable
fun LoadingExerciseList(alpha: Float) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
    ) {
        (1..20).forEach {
            LoadingExerciseRow(alpha)
            if (it < 20)
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
            ExerciseListLoadedListOnly(
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
                DetailedExerciseBottomSheet(exercise = detailedExercise, onDismiss = { showDetailsOfExercise = false })
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
                onChooseExercise = onChooseExercise,
                detailedExercise = detailedExercise
            )
        }
    }
}

@Composable
fun ExerciseListLoadedListOnly(
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
    ExerciseNameFilter(
        nameFilter = filters.name,
        onNameFilter = filterChangers.onNameFilter,
        clearNameFilter = filterChangers.clearNameFilter
    )
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
    onChooseExercise: (Exercise) -> Unit,
    detailedExercise: Exercise
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = modifier.weight(1f)) {
            ExerciseListLoadedListOnly(
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
            DetailedExercise(exercise = detailedExercise)
        }
    }
}

@Composable
fun ExerciseListHeader(
    onFilterIconClick: () -> Unit = { },
    filterEnabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.exercises), color = if (filterEnabled) MaterialTheme.colorScheme.onBackground else Color.Transparent, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        IconButton(onClick = onFilterIconClick) {
            Icon(imageVector = Icons.Filled.FilterAlt, tint = if (filterEnabled) MaterialTheme.colorScheme.onBackground else Color.Transparent, contentDescription = null)
        }
    }
}

@Composable
fun ExerciseNameFilter(enabled: Boolean = true, nameFilter: String, onNameFilter: (String) -> Unit, clearNameFilter: () -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        value = nameFilter,
        onValueChange = onNameFilter,
        enabled = enabled,
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
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { onClickHeart(exercise) }) {
                Icon(imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
            }
        }
    }
}

@Composable
fun LoadingExerciseRow(alpha: Float) {
    val length by remember { mutableStateOf(Random.nextFloat() * 0.3f + 0.3f) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Gray.copy(alpha = alpha)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(length)
                .heightIn(14.dp, 14.dp)
                .background(Color.Gray.copy(alpha = alpha))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(enabled = false, onClick = { }) {
                Icon(imageVector = Icons.Filled.Favorite, tint = Color.Gray.copy(alpha = alpha), contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingExerciseRowPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    LoadingExerciseRow(alpha)
}

@Preview(showBackground = true)
@Composable
fun ExerciseListLoadingListOnlyPreview() {
    ExerciseListLoading(contentType = FreshFitnessContentType.LIST_ONLY)
}

@Preview(
    showBackground = true,
    widthDp = 840
)
@Composable
fun ExerciseListLoadingListAndDetailPreview() {
    ExerciseListLoading(contentType = FreshFitnessContentType.LIST_AND_DETAIL)
}