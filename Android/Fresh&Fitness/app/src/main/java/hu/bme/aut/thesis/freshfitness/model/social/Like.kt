package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Like(
    val id: Int,
    @SerialName(value = "post_id") val postId: Int,
    @SerialName(value = "user_name") val username: String
)