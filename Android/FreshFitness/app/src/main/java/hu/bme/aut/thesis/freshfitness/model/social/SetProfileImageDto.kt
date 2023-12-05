package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetProfileImageDto(
    @SerialName(value = "name") val username: String,
    @SerialName(value = "profile_image") val image: String
)