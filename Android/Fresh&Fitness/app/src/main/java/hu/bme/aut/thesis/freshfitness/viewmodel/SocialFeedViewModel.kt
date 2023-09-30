package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import hu.bme.aut.thesis.freshfitness.decodeJWT
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
import org.json.JSONObject
import java.io.File

class SocialFeedViewModel(val context: Context) : ViewModel() {
    val posts = mutableStateListOf<Post>()
    var isLoading by mutableStateOf(true)
    private var nextPage: Int = 0

    // For enabling user input
    var isLoggedIn by mutableStateOf(false)
    var userName by mutableStateOf("")

    // Showing AlertDialog likes
    var showLikesDialog by mutableStateOf(false)
    lateinit var shownLikesPost: Post

    // Showing AlertDialog comments
    var showCommentsDialog by mutableStateOf(false)
    lateinit var shownCommentsPost: Post

    fun initFeed() {
        Amplify.Auth.fetchAuthSession(
            {
                if (it.isSignedIn) {
                    this.isLoggedIn = true
                    val session = (it as AWSCognitoAuthSession)
                    val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                    val jsonObject = JSONObject(jwt)
                    this.userName = jsonObject.getString("username")
                } else {
                    this.isLoggedIn = false
                    this.userName = ""
                }
                resetFeed()
            },{
                Log.e("social_feed_fetch_auth_session", "Failed to fetch auth session", it)
                resetFeed()
            }
        )
    }

    fun showLikes(postId: Int) {
        this.shownLikesPost = this.posts.single { p -> p.id == postId }
        this.showLikesDialog = true
    }

    fun showComments(postId: Int) {
        this.shownCommentsPost = this.posts.single { p -> p.id == postId }
        this.showCommentsDialog = true
    }

    fun floatingButtonClick() {
        Log.d("", "")
    }

    fun getLikesForPost(postId: Int) {
        val options = RestOptions.builder()
            .addPath("/likes")
            .addQueryParameters(mapOf("post_id" to postId.toString()))
            .build()

        Amplify.API.get(options,
            {
                Log.i("social_feed_post", "GET succeeded: $it")
                val likes = Json.decodeFromString<List<LikeDto>>(it.data.asString())
                this.posts.singleOrNull { p -> p.id == postId }?.likes = likes.map { l -> l.username }.toMutableList()
            },
            {
                Log.e("social_feed_post", "GET failed", it)
            }
        )
    }

    fun deleteComment(deleteCommentDto: DeleteCommentDto) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addBody(Json.encodeToString(deleteCommentDto).toByteArray())
            .build()

        Amplify.API.delete(options,
            {
                Log.i("social_feed_post", "DELETE succeeded: $it")
                resetFeed()
            },
            {
                Log.e("social_feed_post", "DELETE failed", it)
            }
        )
    }

    fun deletePost(deletePostDto: DeletePostDto) {
        val options = RestOptions.builder()
            .addPath("/posts")
            .addBody(Json.encodeToString(deletePostDto).toByteArray())
            .build()

        Amplify.API.delete(options,
            {
                Log.i("social_feed_post", "DELETE succeeded: $it")
                resetFeed()
            },
            {
                Log.e("social_feed_post", "DELETE failed", it)
            }
        )
    }

    fun likePost(post: Post) {
        val likePostDto = LikePostDto(post.id, this.userName)
        val options = RestOptions.builder()
            .addPath("/likes")
            .addBody(Json.encodeToString(likePostDto).toByteArray())
            .build()

        val postToLike = this.posts.single { p -> p.id == post.id }.copy()
        val postToLikeIndex = this.posts.indexOfFirst { p -> p.id == post.id }

        postToLike.also { p ->
            if (p.likes.contains(this.userName)) {
                p.likeCount--
                p.likes.remove(this.userName)
            } else {
                p.likeCount++
                p.likes.add(this.userName)
            }
        }

        this.posts[postToLikeIndex] = postToLike

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "POST succeeded: $it")
            },
            {
                Log.e("social_feed_post", "POST failed", it)
                val likedPost = this.posts.single { p -> p.id == post.id }
                val likedPostIndex = this.posts.indexOfFirst { p -> p.id == post.id }

                likedPost.also { p ->
                    if (p.likes.contains(this.userName)) {
                        p.likeCount--
                        p.likes.remove(this.userName)
                    } else {
                        p.likeCount++
                        p.likes.add(this.userName)
                    }
                }

                this.posts[likedPostIndex] = likedPost
            }
        )
    }

    private fun getCommentsForPost(postId: Int) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addQueryParameters(mapOf("post_id" to postId.toString()))
            .build()

        Amplify.API.get(options,
            {
                Log.i("social_feed_post", "GET succeeded: $it")
                val comments = Json.decodeFromString<MutableList<Comment>>(it.data.asString())
                this.posts.singleOrNull { p -> p.id == postId }?.comments = comments
            },
            {
                Log.e("social_feed_post", "POST failed", it)
            }
        )
    }

    fun commentOnPost(commentDto: CommentOnPostDto) {
        val options = RestOptions.builder()
            .addPath("/comments")
            .addBody(Json.encodeToString(commentDto).toByteArray())
            .build()

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "POST succeeded: $it")
                resetFeed()
            },
            {
                Log.e("social_feed_post", "POST failed", it)
            }
        )
    }

    fun createPost(postDto: CreatePostDto) {
        val options = RestOptions.builder()
            .addPath("/posts")
            .addBody(Json.encodeToString(postDto).toByteArray())
            .build()

        Amplify.API.post(options,
            {
                Log.i("social_feed_post", "POST succeeded: $it")
                resetFeed()
            },
            {
                Log.e("social_feed_post", "POST failed", it)
            }
        )
    }

    private fun resetFeed() {
        posts.clear()
        nextPage = 0
        getNextPosts()
    }

    private fun uploadFile() {
        val exampleFile = File(context.filesDir, "ExampleKey")
        exampleFile.writeText("Example file contents")

        val options = StorageUploadFileOptions.defaultInstance()
        Amplify.Storage.uploadFile("ExampleKey", exampleFile, options,
            {
                Log.i("MyAmplifyApp", "Fraction completed: ${it.fractionCompleted}")
            },
            {
                Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
            },
            {
                Log.e("MyAmplifyApp", "Upload failed", it)
            }
        )
    }

    private fun getNextPosts() {
        isLoading = true
        val options = RestOptions.builder()
            .addPath("/posts")
            .addQueryParameters(mapOf("page" to nextPage.toString()))
            .addQueryParameters(mapOf("username" to this.userName))
            .build()

        Amplify.API.get(options,
            {
                val jsonPosts = it.data.asString()
                val newPosts = Json.decodeFromString<List<PagedPostDto>>(jsonPosts)
                isLoading = false
                val postsToAdd = mutableListOf<Post>()
                newPosts.forEach { pagedPost ->
                    val index = postsToAdd.indexOfFirst { p -> p.id == pagedPost.id }
                    if (index == -1) {
                        val postToAdd = Post(
                            id = pagedPost.id,
                            details = pagedPost.details,
                            createdAt = pagedPost.createdAt,
                            likeCount = pagedPost.likeCount,
                            commentCount = pagedPost.commentCount,
                            username = pagedPost.owner,
                            imageLocation = pagedPost.imageLocation
                        )
                        if (!pagedPost.liker.isNullOrBlank()) {
                            postToAdd.likes.add(pagedPost.liker)
                        }
                        postsToAdd.add(postToAdd)
                    } else {
                        postsToAdd[index].likes.add(pagedPost.liker!!)
                    }
                }
                this.posts.addAll(postsToAdd)
                nextPage++
                for (post in postsToAdd) {
                    getCommentsForPost(post.id)
                }
                Log.i("social_feed_init", "GET succeeded: ${it.data.asString()}")
            },
            { Log.e("social_feed_init", "GET failed.", it) }
        )
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SocialFeedViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}
