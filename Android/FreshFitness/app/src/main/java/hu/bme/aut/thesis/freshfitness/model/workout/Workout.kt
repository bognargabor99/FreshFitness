package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.WorkoutEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Workout(
    val id: Int = -1,
    @SerialName("target_id") val muscleId: Int,
    @Transient var targetMuscle: MuscleGroup? = null,
    @Transient val warmupExercises: MutableList<WorkoutExercise> = mutableListOf(),
    @Transient val exercises: MutableList<WorkoutExercise> = mutableListOf(),
    val difficulty: String,
    val owner: String,
    @SerialName("set_count") val sets: Int,
    @SerialName("equipment_types") val equipmentTypes: String,
    @SerialName("target_date") val date: String,
    @Transient var savedToDate: String = "",
    @Transient var calendarEventId: Int = -1
) {
    fun toWorkoutEntity(): WorkoutEntity {
        return WorkoutEntity(
            awsId = id,
            muscleId = muscleId,
            difficulty = difficulty,
            owner = owner,
            sets = sets,
            equipmentTypes = equipmentTypes,
            date = date,
            savedToDate = savedToDate,
            calendarEventId = calendarEventId
        )
    }

    companion object {
        fun fromWorkoutEntity(entity: WorkoutEntity): Workout {
            return Workout(
                id = entity.awsId,
                muscleId = entity.muscleId,
                difficulty = entity.difficulty,
                owner = entity.owner,
                equipmentTypes = entity.equipmentTypes,
                sets = entity.sets,
                date = entity.date,
                savedToDate = entity.savedToDate,
                calendarEventId = entity.calendarEventId
            )
        }
    }
}