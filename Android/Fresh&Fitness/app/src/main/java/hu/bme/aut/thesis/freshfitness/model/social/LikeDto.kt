package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class LikeDto(
    @SerializedName(value = "user_name") val username: String
)