package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class Like(
    val id: Int,
    @SerializedName(value = "post_id") val postId: Int,
    @SerializedName(value = "user_name") val username: String
)