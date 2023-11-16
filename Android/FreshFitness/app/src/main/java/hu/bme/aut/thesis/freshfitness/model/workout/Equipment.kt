package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.EquipmentEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val id: Int,
    val name: String,
    @SerialName(value = "img_key") val imgKey: String,
    val type: String
) {
    fun toEquipmentEntity(): EquipmentEntity {
        return EquipmentEntity(
            id = this.id,
            name = this.name,
            type = this.type,
            imageKey = this.imgKey
        )
    }

    companion object {
        fun fromEquipmentEntity(entity: EquipmentEntity): Equipment {
            return Equipment(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                imgKey = entity.imageKey
            )
        }
    }
}