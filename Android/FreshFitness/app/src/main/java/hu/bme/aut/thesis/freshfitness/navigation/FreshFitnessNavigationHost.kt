package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hu.bme.aut.thesis.freshfitness.ui.screen.profile.ProfileScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.schedule.ScheduleScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.social.SocialScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ExerciseBankScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.NearbyGymsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.TrackRunningScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.ViewWorkoutsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutScreen
import hu.bme.aut.thesis.freshfitness.ui.util.BackOnlineNotification
import hu.bme.aut.thesis.freshfitness.ui.util.ConnectivityStatus
import hu.bme.aut.thesis.freshfitness.ui.util.Exercises
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.NoConnectionNotification
import hu.bme.aut.thesis.freshfitness.ui.util.Places
import hu.bme.aut.thesis.freshfitness.ui.util.Planning
import hu.bme.aut.thesis.freshfitness.ui.util.Running
import hu.bme.aut.thesis.freshfitness.viewmodel.ConnectivityViewModel

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
                ProfileScreen(onNavigateViewWorkoutsScreen = Planning.onClick)
            }
            composable(route = Workout.route) {
                WorkoutScreen(contentType = contentType)
            }
            composable(route = Social.route) {
                SocialScreen(contentType = contentType, networkAvailable = viewModel.networkAvailable)
            }
            composable(
                route = Schedule.routeWithArgs,
                arguments = Schedule.arguments,
                deepLinks = Schedule.deepLinks
            ) { navBackStackEntry ->
                val dateArg = navBackStackEntry.arguments?.getString(Schedule.accountTypeArg) ?: ""
                ScheduleScreen(date = dateArg, contentType = contentType, networkAvailable = viewModel.networkAvailable)
            }
            composable(route = NearbyGyms.route) {
                NearbyGymsScreen(networkAvailable = viewModel.networkAvailable, contentType = contentType)
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