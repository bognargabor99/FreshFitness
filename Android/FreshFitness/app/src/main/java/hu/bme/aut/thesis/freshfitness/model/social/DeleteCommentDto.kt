package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteCommentDto(
    @SerialName(value = "id") val id: Int,
    val username: String
)