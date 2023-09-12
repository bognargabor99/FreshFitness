package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.thesis.freshfitness.navigation.FitnessBottomNavigation
import hu.bme.aut.thesis.freshfitness.navigation.Home
import hu.bme.aut.thesis.freshfitness.navigation.NearbyGyms
import hu.bme.aut.thesis.freshfitness.navigation.Profile
import hu.bme.aut.thesis.freshfitness.navigation.Progress
import hu.bme.aut.thesis.freshfitness.navigation.Social
import hu.bme.aut.thesis.freshfitness.navigation.TrackRunning
import hu.bme.aut.thesis.freshfitness.navigation.Workout
import hu.bme.aut.thesis.freshfitness.navigation.freshFitnessBottomTabs
import hu.bme.aut.thesis.freshfitness.ui.screen.home.HomeScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.profile.ProfileScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.progress.ProgressScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.social.SocialScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.NearbyGymsScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.TrackRunningScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.workout.WorkoutScreen
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

class FreshFitnessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FreshFitnessApp() }
    }
}

@Composable
fun FreshFitnessApp() {
    FreshFitnessTheme {
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()
        // Fetch your currentDestination:
        val currentDestination = currentBackStack?.destination

        val currentScreen = freshFitnessBottomTabs.find { it.route == currentDestination?.route } ?: Home

        Scaffold(
            bottomBar = { FitnessBottomNavigation(
                allScreens = freshFitnessBottomTabs,
                currentScreen = currentScreen,
                onTabSelected = { newScreen ->
                    navController.navigateSingleTopTo(newScreen.route)
                }
            ) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Profile.route) {
                    ProfileScreen()
                }
                composable(route = Workout.route) {
                    WorkoutScreen(
                        onNavigateNearbyGyms = { navController.navigate(NearbyGyms.route) },
                        onNavigateRunning = { navController.navigate(TrackRunning.route) }
                    )
                }
                composable(route = Social.route) {
                    SocialScreen()
                }
                composable(route = Progress.route) {
                    ProgressScreen()
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
            }
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