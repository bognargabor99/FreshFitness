package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class DeleteCommentDto(
    @SerializedName(value = "id") val id: Int,
    val username: String
)