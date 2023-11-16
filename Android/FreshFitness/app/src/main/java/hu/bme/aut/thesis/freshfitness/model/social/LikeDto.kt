package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeDto(
    @SerialName(value = "user_name") val username: String
)