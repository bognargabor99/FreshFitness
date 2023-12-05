package hu.bme.aut.thesis.freshfitness.model.social

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Post(
    val id: Int,
    var details: String,
    @SerialName(value = "user_name") val username: String,
    @SerialName(value = "image_location") val imageLocation: String,
    @SerialName(value = "created_at") val createdAt: String,
    @SerialName(value = "likecount") var likeCount: Int,
    @SerialName(value = "commentcount") var commentCount: Int,
    @Transient var userProfileImage: String = "",
    @Transient var comments: MutableList<Comment> = mutableListOf(),
    @Transient var likes: MutableList<String> = mutableListOf()
)