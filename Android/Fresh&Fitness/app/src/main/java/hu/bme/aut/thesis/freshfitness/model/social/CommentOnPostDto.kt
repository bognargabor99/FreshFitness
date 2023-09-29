package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentOnPostDto(
    @SerialName(value = "post_id") val postId: Int,
    val text: String,
    val username: String
)