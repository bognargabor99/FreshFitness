package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.WorkoutExerciseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WorkoutExercise(
    var id: Int = -1,
    @SerialName("exercise_id") val exerciseId: Int,
    @Transient var exercise: Exercise? = null,
    @SerialName("workout_id") var workoutId: Int = -1,
    @SerialName("sequence_number") val sequenceNum: Int,
    @SerialName("is_warmup") val isWarmup: Int,
    val amount: Int
) {
    fun isWarmup(): Boolean = this.isWarmup == 1

    fun toWorkoutExerciseEntity(): WorkoutExerciseEntity {
        return WorkoutExerciseEntity(
            id = id,
            exerciseId = exerciseId,
            workoutId = workoutId,
            sequenceNum = sequenceNum,
            isWarmup = isWarmup,
            amount = amount
        )
    }

    companion object {
        fun fromWorkoutExerciseEntity(entity: WorkoutExerciseEntity): WorkoutExercise {
            return WorkoutExercise(
                id = entity.id,
                exerciseId = entity.exerciseId,
                workoutId = entity.workoutId ?: -1,
                sequenceNum = entity.sequenceNum,
                isWarmup = entity.isWarmup,
                amount = entity.amount
            )
        }
    }
}