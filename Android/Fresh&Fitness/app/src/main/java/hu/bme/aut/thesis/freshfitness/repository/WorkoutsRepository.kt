package hu.bme.aut.thesis.freshfitness.repository

import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import hu.bme.aut.thesis.freshfitness.persistence.WorkoutsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutsRepository(private val workoutsDao: WorkoutsDao) {
    suspend fun insertWorkout(workout: Workout) {
        val entity = workout.toWorkoutEntity()
        val exerciseEntities = (workout.exercises + workout.warmupExercises).map { it.toWorkoutExerciseEntity() }
        withContext(Dispatchers.IO) {
            workoutsDao.insertWorkout(entity)
            workoutsDao.insertWorkoutExercises(exerciseEntities)
        }
    }

    suspend fun getWorkouts(): List<Workout> =
        withContext(Dispatchers.IO) {
            val workoutsWithExercises = workoutsDao.getWorkouts()
            val list = mutableListOf<Workout>()
            workoutsWithExercises.forEach {
                val workout = Workout.fromWorkoutEntity(it.workout)
                val exercises = it.exercises.map { we -> WorkoutExercise.fromWorkoutExerciseEntity(we) }
                workout.warmupExercises.addAll(exercises.filter { e -> e.isWarmup() })
                workout.exercises.addAll(exercises.filter { e -> !e.isWarmup() })
                list.add(workout)
            }
            list
        }

    suspend fun deleteWorkout(workout: Workout) =
        withContext(Dispatchers.IO) {
            workoutsDao.deleteWorkout(workout.toWorkoutEntity())
        }
}