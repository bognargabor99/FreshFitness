package hu.bme.aut.thesis.freshfitness.ui.screen.todo

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

@Composable
fun UnderConstructionScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        ), label = ""
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        rotationZ = angle
                    },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = stringResource(R.string.under_construction))
            Text(text = stringResource(R.string.thanks_for_understanding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullPreview() {
    FreshFitnessTheme {
        UnderConstructionScreen()
    }
}