package hu.bme.aut.thesis.freshfitness.model.workout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Equipment(
    val id: Int,
    val name: String,
    @SerialName(value = "img_key") val imgKey: String,
    val type: String
)