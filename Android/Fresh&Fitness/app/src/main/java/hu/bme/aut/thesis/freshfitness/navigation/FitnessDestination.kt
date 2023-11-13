package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

interface FitnessDestination {
    val icon: ImageVector
    val route: String
}


/**
 * Main screens
 */

object Profile : FitnessDestination {
    override val icon = Icons.Default.Person
    override val route = "profile"
}

object Home : FitnessDestination {
    override val icon = Icons.Default.Home
    override val route = "home"
}

object Workout : FitnessDestination {
    override val icon = Icons.Default.FitnessCenter
    override val route = "workout"
}

object Social : FitnessDestination {
    override val icon = Icons.Default.Groups
    override val route = "social"
}

object Progress : FitnessDestination {
    override val icon = Icons.Default.BarChart
    override val route = "progress"
    const val accountTypeArg = "date"
    val routeWithArgs = "$route/{$accountTypeArg}"
    val arguments = listOf(
        navArgument(accountTypeArg) { type = NavType.StringType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "fresh_fitness://$route/{$accountTypeArg}" },
        navDeepLink { uriPattern = "http://hu.bme.aut.thesis.freshfitness/{$accountTypeArg}" },
        navDeepLink { uriPattern = "https://hu.bme.aut.thesis.freshfitness/{$accountTypeArg}" }
    )
}

data class NavigationInfo(
    val allScreens: List<FitnessDestination>,
    val currentScreen: FitnessDestination
)

val freshFitnessBottomTabs = listOf(Home, Workout, Social, Progress, Profile)

/**
 * Sub screens
 */

object NearbyGyms : FitnessDestination {
    override val icon = Icons.Default.FitnessCenter
    override val route = "nearby_gyms"
}

object TrackRunning : FitnessDestination {
    override val icon = Icons.Default.FitnessCenter
    override val route = "track_running"
}

object WorkoutPlanning : FitnessDestination {
    override val icon = Icons.Default.FitnessCenter
    override val route = "workout_plan"
}

object ExerciseBank : FitnessDestination {
    override val icon = Icons.Default.FitnessCenter
    override val route = "exercise_bank"
}