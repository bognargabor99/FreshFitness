package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import hu.bme.aut.thesis.freshfitness.navigation.Schedule
import hu.bme.aut.thesis.freshfitness.navigation.Social
import hu.bme.aut.thesis.freshfitness.navigation.TrackRunning
import hu.bme.aut.thesis.freshfitness.navigation.Workout
import hu.bme.aut.thesis.freshfitness.navigation.WorkoutPlanning
import hu.bme.aut.thesis.freshfitness.navigation.getNavigationAndContentType
import hu.bme.aut.thesis.freshfitness.ui.screen.home.HomeScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.profile.ProfileScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.schedule.ScheduleScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.social.SocialScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ExerciseBankScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.NearbyGymsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.TrackRunningScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ViewWorkoutsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutScreen
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme
import hu.bme.aut.thesis.freshfitness.ui.util.BackOnlineNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.DevicePosture
import hu.bme.aut.thesis.freshfitness.ui.util.Exercises
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import hu.bme.aut.thesis.freshfitness.ui.util.NoConnectionNotification
import hu.bme.aut.thesis.freshfitness.ui.util.Places
import hu.bme.aut.thesis.freshfitness.ui.util.Planning
import hu.bme.aut.thesis.freshfitness.ui.util.Running
import hu.bme.aut.thesis.freshfitness.ui.util.isBookPosture
import hu.bme.aut.thesis.freshfitness.ui.util.isSeparating
import hu.bme.aut.thesis.freshfitness.viewmodel.ConnectivityViewModel
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

@Composable
fun FreshFitnessNavigationHost(
    modifier: Modifier = Modifier,
    viewModel: ConnectivityViewModel = viewModel(),
    navController: NavHostController,
    contentType: FreshFitnessContentType,
) {
    setWorkoutOnClickListeners(navController)
    Column(
        modifier = modifier
    ) {
        ConnectivityStatus(
            availableContent = {
                LaunchedEffect(key1 = false) { viewModel.onNetworkAvailable() }
            },
            unAvailableContent = {
                LaunchedEffect(key1 = false) { viewModel.onNetworkUnavailable() }
            }
        )
        AnimatedVisibility(
            visible = !viewModel.networkAvailable,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            NoConnectionNotification()
        }

        AnimatedVisibility(
            visible = viewModel.showBackOnline,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            BackOnlineNotification()
        }

        NavHost(
            navController = navController,
            startDestination = Social.route
        ) {
            composable(route = Profile.route) {
                ProfileScreen()
            }
            composable(route = Workout.route) {
                WorkoutScreen(contentType = contentType)
            }
            composable(route = Social.route) {
                SocialScreen(contentType = contentType)
            }
            composable(
                route = Schedule.routeWithArgs,
                arguments = Schedule.arguments,
                deepLinks = Schedule.deepLinks
            ) { navBackStackEntry ->
                val dateArg = navBackStackEntry.arguments?.getString(Schedule.accountTypeArg) ?: ""
                ScheduleScreen(date = dateArg, contentType = contentType, networkAvailable = viewModel.networkAvailable)
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
                ViewWorkoutsScreen(contentType = contentType, networkAvailable = viewModel.networkAvailable)
            }
            composable(route = ExerciseBank.route) {
                ExerciseBankScreen(contentType = contentType, networkAvailable = viewModel.networkAvailable)
            }
        }
    }
}

fun setWorkoutOnClickListeners(navController: NavHostController) {
    Running.onClick = { navController.navigate(TrackRunning.route) }
    Places.onClick = { navController.navigate(NearbyGyms.route) }
    Planning.onClick = { navController.navigate(WorkoutPlanning.route) }
    Exercises.onClick = { navController.navigate(ExerciseBank.route) }
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