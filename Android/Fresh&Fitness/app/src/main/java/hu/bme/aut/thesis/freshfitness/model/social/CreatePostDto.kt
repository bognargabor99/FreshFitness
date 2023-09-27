package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class CreatePostDto(
    val details: String,
    val username: String,
    @SerializedName(value = "image_location") val imageLocation: String
)