package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun DistanceFilter(
    radius: Int,
    onValueChange: (Float) -> Unit,
    onQuery: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        targetValue = if (expanded) 12.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Column(
        modifier = Modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "Range: $radius m",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Row(
                modifier = Modifier.height(44.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    text = stringResource(R.string.distance)
                )
                Slider(
                    value = radius.toFloat(),
                    onValueChange = onValueChange,
                    steps = (10000 - 500) / 500 - 1,
                    valueRange = 500f..10000f,
                    onValueChangeFinished = onQuery
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DistanceFilterPreview() {
    DistanceFilter(radius = 2500, onValueChange = {}) {

    }
}