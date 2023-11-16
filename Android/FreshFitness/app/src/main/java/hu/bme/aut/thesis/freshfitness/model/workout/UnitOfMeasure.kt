package hu.bme.aut.thesis.freshfitness.model.workout

import hu.bme.aut.thesis.freshfitness.persistence.model.UnitOfMeasureEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitOfMeasure(
    val id: Int,
    @SerialName(value = "name") val unit: String,
    val type: String
) {
    fun toUnitOfMeasureEntity(): UnitOfMeasureEntity {
        return UnitOfMeasureEntity(
            id = this.id,
            name = this.unit,
            type = this.type
        )
    }

    companion object {
        fun fromUnitOfMeasureEntity(entity: UnitOfMeasureEntity): UnitOfMeasure {
            return UnitOfMeasure(
                id = entity.id,
                unit = entity.name,
                type = entity.type
            )
        }
    }
}