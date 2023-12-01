package hu.bme.aut.thesis.freshfitness.ui.screen.schedule

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import org.junit.Rule
import org.junit.Test

class ScheduleScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val workout = Workout(
        muscleId = 3,
        targetMuscle = MuscleGroup(id = 3, name = "Back & Biceps", imgKey = "public/images/workout/musclegroups/target_muscle_backbiceps.jpg"),
        sets = 4,
        difficulty = "beginner",
        date = "2023-10-31T00:00:00.000Z".take(16),
        owner = "community",
        equipmentTypes = "gym"
    )

    @Test
    fun dayWorkoutTitleTest() {
        composeTestRule.setContent {
            DayWorkoutTitle("Chest & Triceps")
        }

        composeTestRule
            .onNodeWithText("Chest & Triceps")
            .assertExists()
    }

    @Test
    fun workoutOverviewTest() {
        composeTestRule.setContent {
            WorkoutOverview(workout = workout)
        }

        composeTestRule
            .onRoot()
            .onChildren()
            .assertCountEquals(12)

        composeTestRule
            .onNode(hasText(workout.difficulty.replaceFirstChar { it.uppercase() }))
            .assertExists()

        composeTestRule
            .onNode(hasText("No equipment"))
            .assertExists()

        composeTestRule
            .onNode(hasText("Not planned"))
            .assertExists()

        composeTestRule
            .onNode(
                hasText("${workout.sets} sets", substring = true) and
                        hasText("w/", substring = true) and
                        hasText("${workout.exercises.size} exercises", substring = true))
            .assertExists()
    }
}