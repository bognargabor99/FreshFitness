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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import hu.bme.aut.thesis.freshfitness.navigation.ExerciseBank
import hu.bme.aut.thesis.freshfitness.navigation.FitnessBottomNavigation
import hu.bme.aut.thesis.freshfitness.navigation.FitnessDestination
import hu.bme.aut.thesis.freshfitness.navigation.FitnessNavigationWrapperUI
import hu.bme.aut.thesis.freshfitness.navigation.FreshFitnessNavigationRail
import hu.bme.aut.thesis.freshfitness.navigation.Home
import hu.bme.aut.thesis.freshfitness.navigation.NavigationInfo
import hu.bme.aut.thesis.freshfitness.navigation.NearbyGyms
import hu.bme.aut.thesis.freshfitness.navigation.Profile
import hu.bme.aut.thesis.freshfitness.navigation.Progress
import hu.bme.aut.thesis.freshfitness.navigation.Social
import hu.bme.aut.thesis.freshfitness.navigation.TrackRunning
import hu.bme.aut.thesis.freshfitness.navigation.Workout
import hu.bme.aut.thesis.freshfitness.navigation.WorkoutPlanning
import hu.bme.aut.thesis.freshfitness.navigation.getNavigationType
import hu.bme.aut.thesis.freshfitness.ui.screen.home.HomeScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.profile.ProfileScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.progress.ProgressScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.social.SocialScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ExerciseBankScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.NearbyGymsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.TrackRunningScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ViewWorkoutsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutScreen
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.DevicePosture
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
        val navigationType = getNavigationType(windowSize, foldingDevicePosture)
        FitnessNavigationWrapperUI(navigationType = navigationType)
    }
}

@Composable
fun FreshFitnessAppContent(
    navigationType: FreshFitnessNavigationType,
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

            FreshFitnessNavigationHost(navController = navController, modifier = Modifier.weight(1f))

            AnimatedVisibility(visible = navigationType == FreshFitnessNavigationType.BOTTOM_NAVIGATION) {
                FitnessBottomNavigation(navInfo = navInfo, onTabSelected = onTabSelected)
            }
        }
    }
}

@Composable
fun FreshFitnessNavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home.route
    ) {
        composable(route = Profile.route) {
            ProfileScreen()
        }
        composable(route = Workout.route) {
            WorkoutScreen(
                onNavigateNearbyGyms = { navController.navigate(NearbyGyms.route) },
                onNavigateRunning = { navController.navigate(TrackRunning.route) },
                onNavigateWorkoutPlanning = { navController.navigate(WorkoutPlanning.route) },
                onNavigateExerciseBank = { navController.navigate(ExerciseBank.route) }
            )
        }
        composable(route = Social.route) {
            SocialScreen()
        }
        composable(
            route = Progress.routeWithArgs,
            arguments = Progress.arguments,
            deepLinks = Progress.deepLinks
        ) { navBackStackEntry ->
            val dateArg = navBackStackEntry.arguments?.getString(Progress.accountTypeArg) ?: ""
            ProgressScreen(date = dateArg)
        }
        composable(route = Home.route) {
            HomeScreen()
        }
        composable(route = NearbyGyms.route) {
            NearbyGymsScreen()
        }
        composable(route = TrackRunning.route) {
            TrackRunningScreen()
        }
        composable(route = WorkoutPlanning.route) {
            ViewWorkoutsScreen()
        }
        composable(route = ExerciseBank.route) {
            ExerciseBankScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }