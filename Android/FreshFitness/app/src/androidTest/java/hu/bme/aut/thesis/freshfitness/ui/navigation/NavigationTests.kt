package hu.bme.aut.thesis.freshfitness.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import hu.bme.aut.thesis.freshfitness.navigation.ExerciseBank
import hu.bme.aut.thesis.freshfitness.navigation.FitnessNavigationWrapperUI
import hu.bme.aut.thesis.freshfitness.navigation.Profile
import hu.bme.aut.thesis.freshfitness.navigation.Schedule
import hu.bme.aut.thesis.freshfitness.navigation.Social
import hu.bme.aut.thesis.freshfitness.navigation.Workout
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessNavigationType
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTests {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var navController: TestNavHostController

    @Before
    fun setupAppNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            FitnessNavigationWrapperUI(
                navigationType = FreshFitnessNavigationType.BOTTOM_NAVIGATION,
                contentType = FreshFitnessContentType.LIST_ONLY,
                navController = navController
            )
        }
    }

    @Test
    fun startDestinationTest() {
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Social screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        composeTestRule
            .onNodeWithText("Loading posts")
            .assertIsDisplayed()
    }

    @Test
    fun navigateWorkoutScreenTest() {
        composeTestRule
            .onNodeWithContentDescription("Workout screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Workout screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        val route = navController.currentBackStackEntry?.destination?.route
        assert(route == Workout.route)
    }

    @Test
    fun navigateScheduleScreenTest() {
        composeTestRule
            .onNodeWithContentDescription("Schedule screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Schedule screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        val route = navController.currentBackStackEntry?.destination?.route
        assert(route == Schedule.routeWithArgs)
    }

    @Test
    fun navigateProfileScreenTest() {
        composeTestRule
            .onNodeWithContentDescription("Profile screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Profile screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        val route = navController.currentBackStackEntry?.destination?.route
        assert(route == Profile.route)
    }

    @Test
    fun navigateToWorkoutScreenAndBackTest() {
        composeTestRule
            .onNodeWithContentDescription("Workout screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Workout screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        var route = navController.currentBackStackEntry?.destination?.route
        assert(route == Workout.route)

        runOnUiThread {
            navController.popBackStack()
        }

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Social screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == Social.route)
    }

    @Test
    fun navigateToScheduleScreenAndBackTest() {
        composeTestRule
            .onNodeWithContentDescription("Schedule screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Schedule screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        var route = navController.currentBackStackEntry?.destination?.route
        assert(route == Schedule.routeWithArgs)

        runOnUiThread {
            navController.popBackStack()
        }

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Social screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == Social.route)
    }

    @Test
    fun navigateToProfileScreenAndBackTest() {
        composeTestRule
            .onNodeWithContentDescription("Profile screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Profile screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        var route = navController.currentBackStackEntry?.destination?.route
        assert(route == Profile.route)

        runOnUiThread {
            navController.popBackStack()
        }

        composeTestRule
            .onNode(hasAnyChild(hasAnyChild(hasContentDescription("Social screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == Social.route)
    }

    @Test
    fun navigateToExerciseBankScreenAndBackTest() {
        composeTestRule.onNodeWithContentDescription("Workout screen bottom tab", useUnmergedTree = true)
            .performClick()

        composeTestRule.onNode(hasAnyChild(hasAnyChild(hasContentDescription("Workout screen bottom tab"))), useUnmergedTree = true)
            .assertExists().assertIsSelected()

        var route = navController.currentBackStackEntry?.destination?.route
        assert(route == Workout.route)

        composeTestRule.onNodeWithContentDescription("Exercise bank screen", useUnmergedTree = true)
            .performClick()

        composeTestRule.onNode(hasAnyChild(hasAnyChild(hasContentDescription("Workout screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == ExerciseBank.route)

        runOnUiThread {
            navController.popBackStack()
        }

        composeTestRule.onNode(hasAnyChild(hasAnyChild(hasContentDescription("Workout screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == Workout.route)

        runOnUiThread {
            navController.popBackStack()
        }

        composeTestRule.onNode(hasAnyChild(hasAnyChild(hasContentDescription("Social screen bottom tab"))), useUnmergedTree = true)
            .assertExists()
            .assertIsSelected()

        route = navController.currentBackStackEntry?.destination?.route
        assert(route == Social.route)
    }
}