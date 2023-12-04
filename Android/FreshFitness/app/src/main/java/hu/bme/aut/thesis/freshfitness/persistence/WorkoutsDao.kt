package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import hu.bme.aut.thesis.freshfitness.persistence.model.WorkoutEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.WorkoutExerciseEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.WorkoutWithExercises

@Dao
interface WorkoutsDao {
    @Insert
    fun insertWorkout(workoutEntity: WorkoutEntity): Long

    @Insert
    fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Transaction
    @Query("SELECT * from workouts")
    fun getWorkouts(): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * from workouts WHERE id = :workoutId")
    fun getWorkout(workoutId: Int): List<WorkoutWithExercises>

    @Query("DELETE FROM workouts WHERE awsId = :workoutId and savedToDate = :date")
    fun deleteWorkout(workoutId: Int, date: String)
}