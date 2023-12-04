package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import hu.bme.aut.thesis.freshfitness.navigation.FitnessBottomNavigation
import hu.bme.aut.thesis.freshfitness.navigation.FitnessDestination
import hu.bme.aut.thesis.freshfitness.navigation.FitnessNavigationWrapperUI
import hu.bme.aut.thesis.freshfitness.navigation.FreshFitnessNavigationHost
import hu.bme.aut.thesis.freshfitness.navigation.FreshFitnessNavigationRail
import hu.bme.aut.thesis.freshfitness.navigation.NavigationInfo
import hu.bme.aut.thesis.freshfitness.navigation.getNavigationAndContentType
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.DevicePosture
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import hu.bme.aut.thesis.freshfitness.ui.util.isBookPosture
import hu.bme.aut.thesis.freshfitness.ui.util.isSeparating
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FreshFitnessActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        /**
         * Flow of [DevicePosture] that emits every time there's a change in the windowLayoutInfo
         */
        val devicePostureFlow =  WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
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

@Composable
fun FreshFitnessAppContent(
    navigationType: FreshFitnessNavigationType,
    contentType: FreshFitnessContentType,
    navInfo: NavigationInfo,
    navController: NavHostController,
    onDrawerClicked: () -> Unit = {},
    onTabSelected: (FitnessDestination) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == FreshFitnessNavigationType.NAVIGATION_RAIL) {
            FreshFitnessNavigationRail(
                navInfo = navInfo,
                onDrawerClicked = onDrawerClicked,
                onTabSelected = onTabSelected
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            FreshFitnessNavigationHost(modifier = Modifier.weight(1f), navController = navController, contentType = contentType)

            AnimatedVisibility(visible = navigationType == FreshFitnessNavigationType.BOTTOM_NAVIGATION) {
                FitnessBottomNavigation(navInfo = navInfo, onTabSelected = onTabSelected)
            }
        }
    }
}