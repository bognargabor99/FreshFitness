package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    @SerialName(value = "post_id") val postId: Int,
    val text: String,
    @SerialName(value = "user_name") val username: String,
    @SerialName(value = "created_at") val createdAt: String,
)