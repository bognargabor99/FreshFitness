package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Int,
    @SerializedName(value = "post_id") val postId: Int,
    val text: String,
    @SerializedName(value = "user_name") val username: String,
    @SerializedName(value = "created_at") val createdAt: String,
)