package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.SerializedName

data class PagedPostDto(
    val id: Int,
    var details: String,
    val owner: String,
    @SerializedName(value = "image_location") val imageLocation: String,
    @SerializedName(value = "created_at") val createdAt: String,
    @SerializedName(value = "likecount") var likeCount: Int,
    @SerializedName(value = "commentcount") var commentCount: Int,
    val liker: String?
)