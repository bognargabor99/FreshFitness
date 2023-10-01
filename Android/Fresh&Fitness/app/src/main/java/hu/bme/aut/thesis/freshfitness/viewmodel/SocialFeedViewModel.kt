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
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.decodeJWT
import hu.bme.aut.thesis.freshfitness.model.social.CommentOnPostDto
import hu.bme.aut.thesis.freshfitness.model.social.CreatePostDto
import hu.bme.aut.thesis.freshfitness.model.social.DeleteCommentDto
import hu.bme.aut.thesis.freshfitness.model.social.DeletePostDto
import hu.bme.aut.thesis.freshfitness.model.social.Post
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

    // Showing AddCommentDialog
    var showAddCommentDialog by mutableStateOf(false)
    private lateinit var commentedPost: Post

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

    fun startCommenting(postId: Int) {
        this.commentedPost = this.posts.single { p -> p.id == postId }
        this.showAddCommentDialog = true
    }

    fun addComment(text: String) {
        val commentDto = CommentOnPostDto(
            postId = this.commentedPost.id,
            text = text.trim(),
            username = this.userName
        )
        commentOnPost(commentDto)
    }

    fun floatingButtonClick() {
        Log.d("", "")
    }

    fun getLikesForPost(postId: Int) {
        ApiService.getLikesForPost(postId) { likes ->
            this.posts.singleOrNull { p -> p.id == postId }?.likes = likes.map { l -> l.username }.toMutableList()
        }
    }

    fun deleteComment(deleteCommentDto: DeleteCommentDto) {
        ApiService.deleteComment(deleteCommentDto) {
            val post = this.posts.single { p -> p.comments.singleOrNull { c -> c.id == deleteCommentDto.id } != null }.copy()
            post.commentCount--
            post.comments.removeIf { c -> c.id == deleteCommentDto.id }
            val indexOfPost = this.posts.indexOfFirst { p -> p.id == post.id }
            this.posts[indexOfPost] = post
        }
    }

    fun deletePost(deletePostDto: DeletePostDto) {
        ApiService.deletePost(deletePostDto) {
            this.posts.removeIf { p -> p.id == deletePostDto.postId }
        }
    }

    fun likePost(post: Post) {
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

        ApiService.likePost(post.id, this.userName, onError = {
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
        })
    }

    private fun getCommentsForPost(postId: Int) {
        ApiService.getCommentsForPost(postId, onSuccess = { comments ->
            this.posts.singleOrNull { p -> p.id == postId }?.comments = comments
        })
    }

    private fun commentOnPost(commentDto: CommentOnPostDto) {
        this.showAddCommentDialog = false

        ApiService.commentOnPost(commentDto, onSuccess = { newComment ->
            val postToComment = this.posts.single { p -> p.id == commentDto.postId }.copy()
            val postToCommentIndex = this.posts.indexOfFirst { p -> p.id == commentDto.postId }
            postToComment.comments.add(newComment)
            postToComment.commentCount++
            this.posts[postToCommentIndex] = postToComment
        })
    }

    fun createPost(postDto: CreatePostDto) {
        ApiService.createPost(postDto) { post ->
            this.posts.add(0, post)
        }
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

        ApiService.getPosts(this.nextPage, this.userName, onSuccess = { newPosts ->
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
        })
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SocialFeedViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}
