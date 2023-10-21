package hu.bme.aut.thesis.freshfitness.model.workout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Exercise(
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
    @SerialName(value = "advanced_limit") val advancedLimit: Int
)