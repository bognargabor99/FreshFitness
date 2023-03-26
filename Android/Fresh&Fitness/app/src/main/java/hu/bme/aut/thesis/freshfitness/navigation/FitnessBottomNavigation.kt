package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FitnessBottomNavigation(
    modifier: Modifier = Modifier,
    allScreens: List<FitnessDestination>,
    onTabSelected: (FitnessDestination) -> Unit,
    currentScreen: FitnessDestination
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        allScreens.forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.route.replaceFirstChar { c -> c.uppercaseChar() } + " screen"
                    )
                },
                label = {
                    Text(destination.route.replaceFirstChar { c -> c.uppercaseChar() })
                },
                selected = currentScreen == destination,
                onClick = {
                    onTabSelected(destination)
                }
            )
        }
    }
}