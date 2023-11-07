package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.DetailedExerciseEquipment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeededEquipmentsModal(
    equipments: List<Equipment>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(1f)
            .nestedScroll(rememberNestedScrollInteropConnection()),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        onDismissRequest = onDismiss,
        dragHandle = { }
    ) {
        NeededEquipmentsHeader(onDismiss = onDismiss)
        NeededEquipmentsBody(equipments = equipments)
    }
}

@Composable
fun NeededEquipmentsHeader(
    onDismiss: () -> Unit = { }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(R.string.equipments), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NeededEquipmentsBody(equipments: List<Equipment>) {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        equipments.forEach {
            DetailedExerciseEquipment(equipment = it)
        }
    }
}