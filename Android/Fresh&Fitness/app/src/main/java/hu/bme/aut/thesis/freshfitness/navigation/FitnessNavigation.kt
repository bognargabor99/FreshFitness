package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hu.bme.aut.thesis.freshfitness.FreshFitnessAppContent
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.DevicePosture
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import kotlinx.coroutines.launch

@Composable
fun FitnessNavigationWrapperUI(
    navController: NavHostController,
    navigationType: FreshFitnessNavigationType,
    navInfo: NavigationInfo
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (navigationType == FreshFitnessNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerContent(navInfo = navInfo)
                }
            }
        ) {
            FreshFitnessAppContent(navController = navController, navigationType = navigationType, navInfo = navInfo)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        navInfo = navInfo,
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            },
            drawerState = drawerState
        ) {
            FreshFitnessAppContent(
                navController = navController,
                navigationType = navigationType,
                navInfo = navInfo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

@Composable
fun FitnessBottomNavigation(
    modifier: Modifier = Modifier,
    navInfo: NavigationInfo,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        navInfo.allScreens.forEach { destination ->
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
                selected = navInfo.currentScreen == destination,
                onClick = {
                    navInfo.onTabSelected(destination)
                }
            )
        }
    }
}

@Composable
fun FreshFitnessNavigationRail(
    onDrawerClicked: () -> Unit = {},
    navInfo: NavigationInfo
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon =  { Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.navigation_drawer)) }
        )
        navInfo.allScreens.forEach { destination ->
            NavigationRailItem(
                selected = navInfo.currentScreen == destination,
                //label = { Text(destination.route.replaceFirstChar { c -> c.uppercaseChar() }) },
                icon =  { Icon(imageVector = destination.icon, contentDescription = destination.route.replaceFirstChar { c -> c.uppercaseChar() } + " screen") },
                onClick = {
                    navInfo.onTabSelected(destination)
                    onDrawerClicked()
                }
            )
        }
    }
}

@Composable
fun NavigationDrawerContent(
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
    navInfo: NavigationInfo
) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.MenuOpen,
                    contentDescription = stringResource(id = R.string.navigation_drawer)
                )
            }
        }

        navInfo.allScreens.forEach { destination ->
            NavigationDrawerItem(
                selected = navInfo.currentScreen == destination,
                label = { Text(destination.route.replaceFirstChar { c -> c.uppercaseChar() }) },
                icon = { Icon(imageVector = destination.icon, contentDescription = destination.route.replaceFirstChar { c -> c.uppercaseChar() } + " screen") },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {
                    navInfo.onTabSelected(destination)
                    onDrawerClicked()
                }
            )
        }
    }
}

@Preview
@Composable
fun NavigationDrawerContentPreview() {
    NavigationDrawerContent(
        navInfo = NavigationInfo(
            allScreens = listOf(Home, Workout, Social, Progress, Profile),
            currentScreen = Home,
            onTabSelected = {}
        )
    )
}

@Preview
@Composable
fun FreshFitnessNavigationRailPreview() {
    FreshFitnessNavigationRail(
        navInfo = NavigationInfo(
            allScreens = listOf(Home, Workout, Social, Progress, Profile),
            currentScreen = Home,
            onTabSelected = {}
        )
    )
}

fun getNavigationType(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture
): FreshFitnessNavigationType {
    val navigationType: FreshFitnessNavigationType = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            FreshFitnessNavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            FreshFitnessNavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            if (foldingDevicePosture is DevicePosture.BookPosture) {
                FreshFitnessNavigationType.NAVIGATION_RAIL
            } else {
                FreshFitnessNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            FreshFitnessNavigationType.BOTTOM_NAVIGATION
        }
    }
    return navigationType
}