package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostDto(
    val details: String,
    val username: String,
    @SerialName(value = "image_location") val imageLocation: String
)