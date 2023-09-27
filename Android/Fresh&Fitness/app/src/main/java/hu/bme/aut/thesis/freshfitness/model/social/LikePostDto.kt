package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class LikePostDto(
    @SerializedName(value = "post_id") val postId: Int,
    val username: String
)