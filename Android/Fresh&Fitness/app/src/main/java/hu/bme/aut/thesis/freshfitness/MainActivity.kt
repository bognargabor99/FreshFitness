package hu.bme.aut.thesis.freshfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.thesis.freshfitness.ui.screen.LoginScreen
import hu.bme.aut.thesis.freshfitness.ui.theme.FreshFitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FreshFitnessApp() }
    }
}

@Composable
fun FreshFitnessApp() {
    FreshFitnessTheme {
        val navController = rememberNavController()

        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FullPreview() {
    FreshFitnessTheme {
        FreshFitnessApp()
    }
}