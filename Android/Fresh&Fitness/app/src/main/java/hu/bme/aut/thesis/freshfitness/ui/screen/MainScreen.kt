package hu.bme.aut.thesis.freshfitness.ui.screen

import android.annotation.SuppressLint
import hu.bme.aut.thesis.freshfitness.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        bottomBar = { FitnessBottomNavigation() }
    ) {
        ProfileScreen()
    }
}

@Composable
fun FitnessBottomNavigation() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_home))
            },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.workout))
            },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.social))
            },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.progress))
            },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_profile))
            },
            selected = true,
            onClick = {}
        )
    }
}