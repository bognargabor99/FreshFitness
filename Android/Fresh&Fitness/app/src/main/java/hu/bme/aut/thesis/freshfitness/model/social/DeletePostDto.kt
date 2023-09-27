package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class DeletePostDto(
    @SerializedName(value = "post_id") val postId: Int,
    val username: String
)