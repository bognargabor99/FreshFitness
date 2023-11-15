package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.FavouriteExerciseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: Int,
    val name: String,
    val details: String,
    val media: String,
    @SerialName(value = "muscle_group_id") val muscleGroupId: Int,
    @kotlinx.serialization.Transient var muscleGroup: MuscleGroup? = null,
    val difficulty: String,
    @SerialName(value = "unit_id") val unitId: Int,
    @kotlinx.serialization.Transient var unit: UnitOfMeasure? = null,
    @SerialName(value = "equipment_id") val equipmentId: Int,
    @kotlinx.serialization.Transient var equipment: Equipment? = null,
    @SerialName(value = "alternate_eq_id") val alternateEquipmentId: Int,
    @kotlinx.serialization.Transient var alternateEquipment: Equipment? = null,
    @SerialName(value = "intermediate_limit") val intermediateLimit: Int,
    @SerialName(value = "advanced_limit") val advancedLimit: Int,
    @SerialName(value = "for_warmup") val forWarmup: Int
) {
    fun toFavouriteExerciseEntity(): FavouriteExerciseEntity {
        return FavouriteExerciseEntity(
            id = this.id,
            name = this.name,
            media = this.media,
            details = this.details,
            difficulty = this.difficulty,
            intermediateLimit = this.intermediateLimit,
            advancedLimit = this.advancedLimit,
            muscleGroupId = this.muscleGroupId,
            equipmentId = this.equipmentId,
            alternateEquipmentId = this.alternateEquipmentId,
            unitId = this.unitId,
            forWarmup = this.forWarmup
        )
    }

    fun isForWarmup(): Boolean = forWarmup == 1

    companion object {
        fun fromFavouriteExerciseEntity(entity: FavouriteExerciseEntity): Exercise {
            return Exercise(
                id = entity.id,
                name = entity.name,
                media = entity.media,
                details = entity.details,
                difficulty = entity.difficulty,
                intermediateLimit = entity.intermediateLimit,
                advancedLimit = entity.advancedLimit,
                muscleGroupId = if (entity.muscleGroupId != null) entity.muscleGroupId!! else -1,
                equipmentId = if (entity.equipmentId != null) entity.equipmentId!! else -1,
                alternateEquipmentId = if (entity.alternateEquipmentId != null) entity.alternateEquipmentId!! else -1,
                unitId = if (entity.unitId != null) entity.unitId!! else -1,
                forWarmup = entity.forWarmup
            )
        }
    }
}