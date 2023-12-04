package hu.bme.aut.thesis.freshfitness.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import kotlinx.coroutines.launch

@Composable
fun FitnessNavigationWrapperUI(
    navigationType: FreshFitnessNavigationType,
    contentType: FreshFitnessContentType,
    navController: NavHostController
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val currentScreen = freshFitnessTabs.find {
        val idx = currentDestination?.route?.indexOf("/") ?: currentDestination?.route?.lastIndex ?: 0
        it.route == currentDestination?.route?.substring(0, if (idx == -1) 0 else idx)
    } ?: Social

    var navigationInfo by remember { mutableStateOf(NavigationInfo(
        allScreens = freshFitnessTabs,
        currentScreen = currentScreen,
    )) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        navigationInfo = navigationInfo.copy(
            currentScreen = freshFitnessTabs.singleOrNull {
                it.route.takeWhile { c -> c != '/' } == destination.route?.takeWhile { c -> c != '/' }
            } ?: Workout
        )
    }

    val onTabSelected: (FitnessDestination) -> Unit = { newScreen: FitnessDestination ->
        if (newScreen is Schedule)
            navController.navigateSingleTopTo(newScreen.routeWithArgs)
        else
            navController.navigateSingleTopTo(newScreen.route)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (navigationType == FreshFitnessNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerContent(navInfo = navigationInfo, onTabSelected = onTabSelected)
                }
            }
        ) {
            FreshFitnessAppContent(navController = navController, navigationType = navigationType, contentType = contentType, navInfo = navigationInfo, onTabSelected = onTabSelected)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        navInfo = navigationInfo,
                        onTabSelected = onTabSelected,
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.animateTo(DrawerValue.Closed, anim = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
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
                contentType = contentType,
                navInfo = navigationInfo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.animateTo(DrawerValue.Open, anim = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
                    }
                },
                onTabSelected = {
                    onTabSelected(it)
                    if (drawerState.isOpen) {
                        scope.launch {
                            drawerState.animateTo(DrawerValue.Closed, anim = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun FreshFitnessAppContent(
    navigationType: FreshFitnessNavigationType,
    contentType: FreshFitnessContentType,
    navInfo: NavigationInfo,
    navController: NavHostController,
    onDrawerClicked: () -> Unit = {},
    onTabSelected: (FitnessDestination) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == FreshFitnessNavigationType.NAVIGATION_RAIL) {
            FreshFitnessNavigationRail(
                navInfo = navInfo,
                onDrawerClicked = onDrawerClicked,
                onTabSelected = onTabSelected
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            FreshFitnessNavigationHost(modifier = Modifier.weight(1f), navController = navController, contentType = contentType)

            AnimatedVisibility(visible = navigationType == FreshFitnessNavigationType.BOTTOM_NAVIGATION) {
                FitnessBottomNavigation(navInfo = navInfo, onTabSelected = onTabSelected)
            }
        }
    }
}

@Composable
fun FitnessBottomNavigation(
    modifier: Modifier = Modifier,
    navInfo: NavigationInfo,
    onTabSelected: (FitnessDestination) -> Unit
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
                        contentDescription = destination.route.replaceFirstChar { c -> c.uppercaseChar() } + " screen bottom tab"
                    )
                },
                label = {
                    Text(destination.route.replaceFirstChar { c -> c.uppercaseChar() })
                },
                selected = navInfo.currentScreen == destination,
                onClick = { onTabSelected(destination) }
            )
        }
    }
}

@Composable
fun FreshFitnessNavigationRail(
    onDrawerClicked: () -> Unit = {},
    navInfo: NavigationInfo,
    onTabSelected: (FitnessDestination) -> Unit
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
                label = { Text(destination.route.replaceFirstChar { c -> c.uppercaseChar() }) },
                icon =  { Icon(imageVector = destination.icon, contentDescription = destination.route.replaceFirstChar { c -> c.uppercaseChar() } + " screen rail item") },
                onClick = {
                    onTabSelected(destination)
                }
            )
        }
    }
}

@Composable
fun NavigationDrawerContent(
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
    navInfo: NavigationInfo,
    onTabSelected: (FitnessDestination) -> Unit
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
                .padding(8.dp),
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
                    onTabSelected(destination)
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
            allScreens = freshFitnessTabs,
            currentScreen = Profile
        ),
        onTabSelected = {}
    )
}

@Preview
@Composable
fun FreshFitnessNavigationRailPreview() {
    FreshFitnessNavigationRail(
        navInfo = NavigationInfo(
            allScreens = freshFitnessTabs,
            currentScreen = Profile
        ),
        onTabSelected = {}
    )
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