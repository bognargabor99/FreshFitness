package hu.bme.aut.thesis.freshfitness.model.workout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UnitOfMeasure(
    val id: Int,
    @SerialName(value = "name") val unit: String,
    val type: String
)