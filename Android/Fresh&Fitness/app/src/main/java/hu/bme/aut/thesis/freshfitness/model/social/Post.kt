package hu.bme.aut.thesis.freshfitness.model.social

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    var details: String,
    @SerializedName(value = "user_name") val username: String,
    @SerializedName(value = "image_location") val imageLocation: String,
    @SerializedName(value = "created_at") val createdAt: String,
    @SerializedName(value = "likecount") var likeCount: Int,
    @SerializedName(value = "commentcount") var commentCount: Int,
    @Expose(serialize = false, deserialize = false) var comments: MutableList<Comment> = mutableListOf(),
    @Expose(serialize = false, deserialize = true) var likes: MutableList<String> = mutableListOf()
)