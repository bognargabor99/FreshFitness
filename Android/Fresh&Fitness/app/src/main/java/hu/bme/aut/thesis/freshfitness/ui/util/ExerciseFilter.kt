package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.ui.util.media.S3Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFilter(
    difficultyFilter: String,
    muscleFilter: String,
    equipmentFilter: String,
    allMuscles: List<MuscleGroup>,
    allEquipments: List<Equipment>,
    onApplyFilters: (difficulty: String, muscle: String, equipment: String) -> Unit,
    onDismiss: () -> Unit
) {
    var localDifficultyFilter: String by remember { mutableStateOf(difficultyFilter) }
    var localMuscleFilter: String by remember { mutableStateOf(muscleFilter) }
    var localEquipmentFilter: String by remember { mutableStateOf(equipmentFilter) }
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { }
    ) {
        ExerciseFilterHeader(onDismiss = onDismiss)
        ExerciseFilterBody(
            difficultyFilter = localDifficultyFilter,
            muscleFilter = localMuscleFilter,
            equipmentFilter = localEquipmentFilter,
            onMuscleFilterChange = { muscle -> localMuscleFilter = if (localMuscleFilter != muscle) muscle else "" },
            onEquipmentFilterChange = { equipment -> localEquipmentFilter = if (localEquipmentFilter != equipment) equipment else "" },
            allMuscles = allMuscles,
            allEquipments = allEquipments,
            onApplyFilters = { onApplyFilters(localDifficultyFilter, localMuscleFilter, localEquipmentFilter) },
            onDifficultyFilterChange = { localDifficultyFilter = if (localDifficultyFilter != it) it else "" }
        )
    }
}

@Composable
fun ExerciseFilterHeader(onDismiss: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(contentAlignment = Alignment.CenterEnd) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Filter exercises",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
    }
}

@Composable
fun ExerciseFilterBody(
    difficultyFilter: String,
    muscleFilter: String,
    equipmentFilter: String,
    onMuscleFilterChange: (String) -> Unit,
    onEquipmentFilterChange: (String) -> Unit,
    allMuscles: List<MuscleGroup>,
    allEquipments: List<Equipment>,
    onApplyFilters: () -> Unit,
    onDifficultyFilterChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        DifficultyFilter(difficultyFilter, listOf("Beginner", "Intermediate", "Advanced"), onDifficultyFilterChange)
        MuscleFilter(muscleFilter = muscleFilter, onMuscleFilterChange = onMuscleFilterChange, allMuscles = allMuscles)
        EquipmentFilter(equipmentFilter = equipmentFilter, onEquipmentFilterChange = onEquipmentFilterChange, allEquipments = allEquipments)
        ExerciseFilterSubmit(onApplyFilters = onApplyFilters)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DifficultyFilter(currentDifficulty: String, difficulties: List<String>, onDifficultyFilterChange: (String) -> Unit) {
    FilterTitle(title = stringResource(id = R.string.difficulty))
    FlowRow {
        difficulties.forEach {
            Badge(it, isSelected = currentDifficulty == it, onClick = onDifficultyFilterChange)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuscleFilter(
    muscleFilter: String,
    onMuscleFilterChange: (String) -> Unit,
    allMuscles: List<MuscleGroup>
) {
    FilterTitle(title = stringResource(R.string.muscle_group))
    FlowRow {
        allMuscles.forEach {
            MuscleGroupCard(muscleGroup = it, isSelected = muscleFilter == it.name, onClick = onMuscleFilterChange)
        }
    }
}

@Composable
fun MuscleGroupCard(muscleGroup: MuscleGroup, isSelected: Boolean, onClick: (String) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(12.dp)
            .widthIn(min = 50.dp, max = 90.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick(muscleGroup.name) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        MuscleImage(imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${muscleGroup.imgKey}", contentDescription = "Image: ${muscleGroup.name}")
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            text = muscleGroup.name,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun MuscleImage(imageUri: String, contentDescription: String? = null) {
    S3Image(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp),
        imageUri = imageUri,
        contentDescription = contentDescription
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EquipmentFilter(
    equipmentFilter: String,
    onEquipmentFilterChange: (String) -> Unit,
    allEquipments: List<Equipment>
) {
    FilterTitle(title = stringResource(id = R.string.equipment))
    FlowRow(
        modifier = Modifier.padding(8.dp)
    ) {
        allEquipments.forEach {
            Badge(text = it.name, isSelected = it.name == equipmentFilter, onClick = onEquipmentFilterChange)
        }
    }
}

@Composable
fun Badge(text: String, isSelected: Boolean, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick(text) }
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 3.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun ExerciseFilterSubmit(
    onApplyFilters: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onApplyFilters
    ) {
        Text(stringResource(R.string.apply), fontSize = 14.sp)
    }
}

@Composable
fun FilterTitle(title: String) {
    Text(modifier = Modifier.padding(8.dp), text = title.uppercase(), color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold)
}