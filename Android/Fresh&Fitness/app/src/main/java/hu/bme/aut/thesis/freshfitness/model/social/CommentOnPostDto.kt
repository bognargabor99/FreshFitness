package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class CommentOnPostDto(
    @SerializedName(value = "post_id") val postId: Int,
    val text: String,
    val username: String
)