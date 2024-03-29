package hu.bme.aut.thesis.freshfitness.ui.screen.workout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.WorkoutScreenUtil
import hu.bme.aut.thesis.freshfitness.ui.util.workoutTabs

@Composable
fun WorkoutScreen(
    contentType: FreshFitnessContentType,
    tabs: List<WorkoutScreenUtil> = workoutTabs
) {
    when (contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            WorkoutScreenListOnly(tabs = tabs)
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            WorkoutScreenListAndDetail(tabs = tabs)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkoutScreenListOnly(
    tabs: List<WorkoutScreenUtil>
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Adaptive(minSize = 240.dp),
    ) {
        items(tabs.size) {
            ImageWithTextOverlay(
                modifier = Modifier.aspectRatio(4f / 3f),
                painter = painterResource(tabs[it].drawableId),
                contentDescription = "${stringResource(tabs[it].textId)} screen",
                text = stringResource(tabs[it].textId),
                onClick = tabs[it].onClick
            )
        }
    }
}

@Composable
fun WorkoutScreenListAndDetail(
    tabs: List<WorkoutScreenUtil>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        listOf(tabs.take(2), tabs.takeLast(2)).forEach {
            Row(
                modifier = Modifier.fillMaxWidth(1f).weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                it.forEachIndexed { index, it ->
                    ImageWithTextOverlay(
                        modifier = Modifier.fillMaxWidth((index.toFloat() + 1f) / 2f).fillMaxHeight(),
                        painter = painterResource(it.drawableId),
                        contentDescription = null,
                        text = stringResource(it.textId),
                        onClick = it.onClick
                    )
                }
            }
        }
    }
}

@Composable
fun ImageWithTextOverlay(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
        )
        Text(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            fontSize = 32.sp,
            fontFamily = FontFamily.Monospace,
            color = White.copy(alpha = 0.9f)
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 640,
    widthDp = 480
)
@Composable
fun WorkoutScreenListOnlyPreview() {
    FreshFitnessTheme {
        WorkoutScreen(
            contentType = FreshFitnessContentType.LIST_ONLY,
            tabs = workoutTabs
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 800,
    widthDp = 1000
)
@Composable
fun WorkoutScreenListAndDetailPreview() {
    FreshFitnessTheme {
        WorkoutScreen(
            contentType = FreshFitnessContentType.LIST_AND_DETAIL,
            tabs = workoutTabs
        )
    }
}