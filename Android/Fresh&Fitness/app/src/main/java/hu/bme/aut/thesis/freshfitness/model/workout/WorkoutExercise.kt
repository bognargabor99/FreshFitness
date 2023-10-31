package hu.bme.aut.thesis.freshfitness.model.workout

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
}