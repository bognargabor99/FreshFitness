package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedPostDto(
    val id: Int,
    var details: String,
    val owner: String,
    @SerialName(value = "image_location") val imageLocation: String,
    @SerialName(value = "created_at") val createdAt: String,
    @SerialName(value = "likecount") var likeCount: Int,
    @SerialName(value = "commentcount") var commentCount: Int,
    val liker: String?
)