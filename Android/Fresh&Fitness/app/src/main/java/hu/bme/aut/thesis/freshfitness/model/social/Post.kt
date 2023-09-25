package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val details: String,
    @SerializedName(value = "user_name") val username: String,
    @SerializedName(value = "image_location") val imageLocation: String,
    @SerializedName(value = "created_at") val createdAt: String,
    @SerializedName(value = "likecount") val likes: Int,
    @SerializedName(value = "commentcount") val comments: Int
)