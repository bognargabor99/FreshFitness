package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikePostDto(
    @SerialName(value = "post_id") val postId: Int,
    val username: String
)