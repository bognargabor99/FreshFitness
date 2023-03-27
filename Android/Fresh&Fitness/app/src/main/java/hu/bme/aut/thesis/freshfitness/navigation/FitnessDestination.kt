package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

interface FitnessDestination {
    val icon: ImageVector
    val route: String
}

object Authentication : FitnessDestination {
    override val icon = Icons.Default.Person
    override val route = "login"
}

object Register : FitnessDestination {
    override val icon = Icons.Default.AppRegistration
    override val route = "register"
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
}

object Profile : FitnessDestination {
    override val icon = Icons.Filled.Person
    override val route = "profile"
}

val freshFitnessBottomTabs = listOf(Home, Workout, Social, Progress, Authentication)