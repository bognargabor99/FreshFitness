package hu.bme.aut.thesis.freshfitness.ui.util

import hu.bme.aut.thesis.freshfitness.R

interface WorkoutScreenUtil {
    val textId: Int
    val drawableId: Int
    var onClick: () -> Unit
}

object Running : WorkoutScreenUtil {
    override val textId: Int = R.string.go_for_a_run
    override val drawableId: Int = R.drawable.workout_running
    override var onClick: () -> Unit = { }
}

object Places : WorkoutScreenUtil {
    override val textId: Int = R.string.gyms_nearby
    override val drawableId: Int = R.drawable.workout_places
    override var onClick: () -> Unit = { }
}

object Planning : WorkoutScreenUtil {
    override val textId: Int = R.string.workout_planning
    override val drawableId: Int = R.drawable.workout_planning
    override var onClick: () -> Unit = { }
}

object Exercises : WorkoutScreenUtil {
    override val textId: Int = R.string.exercise_bank
    override val drawableId: Int = R.drawable.workout_exercise_bank
    override var onClick: () -> Unit = { }
}

val workoutTabs = listOf(Exercises, Planning, Places, Running)