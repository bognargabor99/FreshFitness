package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.thesis.freshfitness.navigation.Login
import hu.bme.aut.thesis.freshfitness.navigation.MainPage
import hu.bme.aut.thesis.freshfitness.navigation.Profile
import hu.bme.aut.thesis.freshfitness.ui.screen.LoginScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.MainScreen
import hu.bme.aut.thesis.freshfitness.ui.screen.ProfileScreen
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FreshFitnessApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshFitnessApp() {
    FreshFitnessTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Login.route) {
                    LoginScreen(
                        onSignIn = { navController.navigateSingleTopTo(MainPage.route)}
                    )
                }
                composable(route = MainPage.route) {
                    MainScreen()
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

@Preview(showBackground = true)
@Composable
fun FullPreview() {
    FreshFitnessTheme {
        FreshFitnessApp()
    }
}