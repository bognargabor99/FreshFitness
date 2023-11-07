package hu.bme.aut.thesis.freshfitness.ui.screen.progress

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = viewModel()) {
    LaunchedEffect(key1 = false) {
        if (!viewModel.dataFetched)
            viewModel.initScreen()
    }

    if (!viewModel.dataFetched) {
        ProgressScreenLoading()
    }
    else {
        ProgressScreenLoaded()
    }
}

@Composable
fun ProgressScreenLoading() {
    ScreenLoading(loadingText = stringResource(R.string.fetching_data))
}

@Composable
fun ProgressScreenLoaded() {
    Text(text = "Loaded", fontSize = 40.sp)
}