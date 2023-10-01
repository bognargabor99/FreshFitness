package hu.bme.aut.thesis.freshfitness.amplify

import android.util.Log
import aws.smithy.kotlin.runtime.time.Instant
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.CommentOnPostDto
import hu.bme.aut.thesis.freshfitness.model.social.CreatePostDto
import hu.bme.aut.thesis.freshfitness.model.social.DeleteCommentDto
import hu.bme.aut.thesis.freshfitness.model.social.DeletePostDto
import hu.bme.aut.thesis.freshfitness.model.social.LikeDto
import hu.bme.aut.thesis.freshfitness.model.social.LikePostDto
import hu.bme.aut.thesis.freshfitness.model.social.PagedPostDto
import hu.bme.aut.thesis.freshfitness.model.social.Post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ApiService {
    /**
     * GET operations
     */
    fun getPosts(nextPage: Int, userName: String, onSuccess: (List<PagedPostDto>) -> Unit, onError: () -> Unit = {}) {
        val options = RestOptions.builder()
            .addPath("/posts")
            .addQueryParameters(mapOf("page" to nextPage.toString()))
            .addQueryParameters(mapOf("username" to userName))
            .build()

        Amplify.API.get(options,
            {
                Log.i("social_feed_init", "GET succeeded: ${it.data.asString()}")
                val jsonPosts = it.data.asString()
                val newPosts = Json.decodeFromString<List<PagedPostDto>>(jsonPosts)
                onSuccess(newPosts)
            },
            {
                Log.e("social_feed_init", "GET failed.", it)
                onError()
            }
        )
    }

    fun getLikesForPost(postId: Int, onSuccess: (List<LikeDto>) -> Unit) {
        val options = RestOptions.builder()
            .addPath("/likes")
            .addQueryParameters(mapOf("post_id" to postId.toString()))
            .build()

        Amplify.API.get(options,
            {
                Log.i("social_feed_post", "GET succeeded: $it")
                val likes = Json.decodeFromString<List<LikeDto>>(it.data.asString())
                onSuccess(likes)
            },
            {
                Log.e("social_feed_post", "GET failed", it)
            }
        )
    }

    fun getCommentsForPost(postId: Int, onSuccess: (MutableList<Comment>) -> Unit, onError: () -> Unit = {}) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addQueryParameters(mapOf("post_id" to postId.toString()))
            .build()

        Amplify.API.get(options,
            {
                Log.i("social_feed_post", "GET succeeded: $it")
                val comments = Json.decodeFromString<MutableList<Comment>>(it.data.asString())
                onSuccess(comments)
            },
            {
                Log.e("social_feed_post", "POST failed", it)
                onError()
            }
        )
    }


    /**
     * POST operations
     */

    fun createPost(postDto: CreatePostDto, onSuccess: (Post) -> Unit) {
        val options = RestOptions.builder()
            .addPath("/posts")
            .addBody(Json.encodeToString(postDto).toByteArray())
            .build()

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "POST succeeded: $it")
                val post = Json.decodeFromString<Post>(it.data.asString())
                onSuccess(post)
            },
            {
                Log.e("social_feed_post", "POST failed", it)
            }
        )
    }

    fun likePost(postId: Int, userName: String, onError: () -> Unit) {
        val likePostDto = LikePostDto(postId, userName)
        val options = RestOptions.builder()
            .addPath("/likes")
            .addBody(Json.encodeToString(likePostDto).toByteArray())
            .build()

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "Liking post succeeded: $it")
            },
            {
                Log.e("social_feed_post", "Liking post failed", it)
                onError()
            }
        )
    }

    fun commentOnPost(commentDto: CommentOnPostDto, onSuccess: (Comment) -> Unit, onError: () -> Unit = {}) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addBody(Json.encodeToString(commentDto).toByteArray())
            .build()

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "Commenting on post succeeded: $it")
                val commentId = it.data.asJSONObject().getInt("commentId")
                val newComment = Comment(
                    id = commentId,
                    postId = commentDto.postId,
                    text = commentDto.text,
                    username = commentDto.username,
                    createdAt = Instant.now().toString()
                )
                onSuccess(newComment)
            },
            {
                Log.e("social_feed_post", "POST failed", it)
                onError()
            }
        )
    }

    /**
     * DELETE operations
     */

    fun deletePost(deletePostDto: DeletePostDto, onSuccess: () -> Unit) {
        val options = RestOptions.builder()
            .addPath("/posts")
            .addBody(Json.encodeToString(deletePostDto).toByteArray())
            .build()

        Amplify.API.delete(options,
            {
                Log.i("social_feed_post", "DELETE succeeded: $it")
                onSuccess()
            },
            {
                Log.e("social_feed_post", "DELETE failed", it)
            }
        )
    }

    fun deleteComment(deleteCommentDto: DeleteCommentDto, onSuccess: () -> Unit) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addBody(Json.encodeToString(deleteCommentDto).toByteArray())
            .build()

        Amplify.API.delete(options,
            {
                Log.i("social_feed_post", "DELETE succeeded: $it")
                onSuccess()
            },
            {
                Log.e("social_feed_post", "DELETE failed", it)
            }
        )
    }
}