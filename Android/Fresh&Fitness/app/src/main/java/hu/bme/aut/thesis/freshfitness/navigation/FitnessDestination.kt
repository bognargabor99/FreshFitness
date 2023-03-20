package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import hu.bme.aut.thesis.freshfitness.ui.screen.LoginScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.ProfileScreen

interface FitnessDestination {
    val icon: ImageVector
    val route: String
    val screen: @Composable () -> Unit
}

object Login : FitnessDestination {
    override val icon = Icons.Filled.Login
    override val route = "login"
    override val screen: @Composable () -> Unit = { LoginScreen() }
}

object Profile : FitnessDestination {
    override val icon = Icons.Filled.Person
    override val route = "profile"
    override val screen: @Composable () -> Unit = { ProfileScreen() }
}