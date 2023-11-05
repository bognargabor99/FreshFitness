package hu.bme.aut.thesis.freshfitness.repository

import hu.bme.aut.thesis.freshfitness.model.state.WorkoutPlanState
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import kotlin.math.roundToInt

class WorkoutCreationRepository(
    private val exercises: List<Exercise>,
) {
    fun createWorkoutPlan(workoutPlanState: WorkoutPlanState): Workout {
        val workout = Workout(
            sets = workoutPlanState.setCount,
            date = workoutPlanState.targetDate,
            difficulty = workoutPlanState.difficulty.lowercase(),
            owner = workoutPlanState.owner,
            equipmentTypes = workoutPlanState.equipmentType.lowercase(),
            muscleId = workoutPlanState.muscleId
        )
        if (workoutPlanState.createWarmup)
            workout.warmupExercises.addAll(this.createWarmupExercises(workoutPlanState))
        workout.exercises.addAll(this.createWorkoutExercises(workoutPlanState))
        return workout
    }

    private fun createWarmupExercises(workoutPlanState: WorkoutPlanState): List<WorkoutExercise> {
        val warmupExercises = this.exercises.filter { it.isForWarmup() }.shuffled().take(3)
        return warmupExercises.mapIndexed { index, it ->
            WorkoutExercise(
                exerciseId = it.id,
                exercise = it.copy(),
                sequenceNum = index + 1,
                isWarmup = 1,
                amount = getWarmupAmount(it, workoutPlanState.difficulty)
            )
        }
    }

    private fun createWorkoutExercises(workoutPlanState: WorkoutPlanState): List<WorkoutExercise> {
        val exerciseCount = if (workoutPlanState.setCount > 4) 6 else 8
        var equipmentTypes = listOf("none", "intermediate", "advanced")
        val index = equipmentTypes.indexOf(workoutPlanState.equipmentType.lowercase())
        equipmentTypes = equipmentTypes.take(index + 1)
        val exercises = this.exercises.filter {
            it.muscleGroupId == workoutPlanState.muscleId &&
            (equipmentTypes.contains(it.equipment!!.type) || equipmentTypes.contains(it.alternateEquipment!!.type))
        }.shuffled().take(exerciseCount)
        return exercises.map {
            WorkoutExercise(
                exerciseId = it.id,
                exercise = it.copy(),
                sequenceNum = index + 1,
                isWarmup = 1,
                amount = getWarmupExerciseAmount(it, workoutPlanState.difficulty)
            )
        }
    }

    private fun getWarmupAmount(exercise: Exercise, difficulty: String): Int {
        return when(difficulty) {
            "advanced" -> exercise.intermediateLimit
            else -> (exercise.intermediateLimit * 0.8).roundToInt()
        }
    }

    private fun getWarmupExerciseAmount(exercise: Exercise, difficulty: String): Int {
        return when(difficulty) {
            "advanced" -> exercise.advancedLimit
            "intermediate" -> exercise.intermediateLimit
            else -> (exercise.intermediateLimit * 0.8).roundToInt()
        }
    }
}