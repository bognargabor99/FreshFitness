package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("workoutId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val awsId: Int,
    val exerciseId: Int,
    val exerciseName: String,
    var workoutId: Int? = null,
    val sequenceNum: Int,
    val isWarmup: Int,
    val amount: Int
)