package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import hu.bme.aut.thesis.freshfitness.navigation.FitnessNavigationWrapperUI
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.DevicePosture
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import hu.bme.aut.thesis.freshfitness.ui.util.isBookPosture
import hu.bme.aut.thesis.freshfitness.ui.util.isSeparating
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FreshFitnessActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val devicePostureFlow = getDevicePostureFlow()

        setContent {
            val windowSize = calculateWindowSizeClass(this)
            val devicePosture = devicePostureFlow.collectAsState().value
            FreshFitnessApp(windowSize = windowSize.widthSizeClass, foldingDevicePosture = devicePosture)
        }
    }

}

@Composable
fun FreshFitnessApp(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture
) {
    FreshFitnessTheme {
        val (navigationType, contentType) = getNavigationAndContentType(windowSize, foldingDevicePosture)
        val navController = rememberNavController()
        FitnessNavigationWrapperUI(navigationType = navigationType, contentType = contentType, navController = navController)
    }
}

/**
 * Flow of [DevicePosture] that emits every time there's a change in the windowLayoutInfo
 */
private fun ComponentActivity.getDevicePostureFlow(): StateFlow<DevicePosture> {
    return WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
        .flowWithLifecycle(this.lifecycle)
        .map { layoutInfo ->
            val foldingFeature =
                layoutInfo.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()
            when {
                isBookPosture(foldingFeature) ->
                    DevicePosture.BookPosture(foldingFeature.bounds)

                isSeparating(foldingFeature) ->
                    DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

                else -> DevicePosture.NormalPosture
            }
        }
        .stateIn(
            scope = lifecycleScope,
            started = SharingStarted.Eagerly,
            initialValue = DevicePosture.NormalPosture
        )
}

fun getNavigationAndContentType(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture
): Pair<FreshFitnessNavigationType, FreshFitnessContentType> {
    val navigationType: FreshFitnessNavigationType
    val contentType: FreshFitnessContentType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = FreshFitnessNavigationType.BOTTOM_NAVIGATION
            contentType = FreshFitnessContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = FreshFitnessNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture is DevicePosture.BookPosture
                || foldingDevicePosture is DevicePosture.Separating) {
                FreshFitnessContentType.LIST_AND_DETAIL
            } else {
                FreshFitnessContentType.LIST_ONLY
            }
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                FreshFitnessNavigationType.NAVIGATION_RAIL
            } else {
                FreshFitnessNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = FreshFitnessContentType.LIST_AND_DETAIL
        }
        else -> {
            navigationType = FreshFitnessNavigationType.BOTTOM_NAVIGATION
            contentType = FreshFitnessContentType.LIST_ONLY
        }
    }
    return navigationType to contentType
}