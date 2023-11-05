package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.MuscleEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroup(
    val id: Int,
    val name: String,
    @SerialName(value = "img_key") val imgKey: String
) {
    fun toMuscleEntity(): MuscleEntity {
        return MuscleEntity(
            id = this.id,
            name = this.name,
            imageKey = this.imgKey
        )
    }

    companion object {
        fun fromMuscleEntity(entity: MuscleEntity): MuscleGroup {
            return MuscleGroup(
                id = entity.id,
                name = entity.name,
                imgKey = entity.imageKey
            )
        }
    }
}