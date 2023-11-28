package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.workout.Workout

@Composable
fun WorkoutPlanReviewScreen(
    workout: Workout,
    onNewPlan: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    BackHandler { onCancel() }
    Column {
        WorkoutPlanReviewHeader(onNewPlan = onNewPlan, onAccept = onAccept)
        DetailedWorkout(workout = workout, onDismiss = onCancel, isSaved = false, onSave = { }, saveEnabled = false, deleteEnabled = false, onDelete = { })
    }
}

@Composable
fun WorkoutPlanReviewHeader(
    onNewPlan: () -> Unit,
    onAccept: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.review_workout),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Row {
            IconButton(onClick = onNewPlan) {
                Icon(imageVector = Icons.Filled.Refresh, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
            }
            IconButton(onClick = onAccept) {
                Icon(imageVector = Icons.Filled.Check, tint = MaterialTheme.colorScheme.onBackground, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutPlanReviewHeaderPreview() {
    WorkoutPlanReviewHeader({}, {})
}