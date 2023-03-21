package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import hu.bme.aut.thesis.freshfitness.ui.screen.LoginScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.MainScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.ProfileScreen

interface FitnessDestination {
    val icon: ImageVector
    val route: String
}

object Login : FitnessDestination {
    override val icon = Icons.Filled.Login
    override val route = "login"
}

object MainPage : FitnessDestination {
    override val icon = Icons.Filled.DonutSmall
    override val route = "main"
}

object Profile : FitnessDestination {
    override val icon = Icons.Filled.Person
    override val route = "profile"
}