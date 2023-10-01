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
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
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

    // Creating a Post
    var showCreatePostDialog by mutableStateOf(false)
    var uploadingPost by mutableStateOf(false)

    // Deleting a post
    var showDeletePostAlert by mutableStateOf(false)

    // Show options dialog for a post
    var showPostOptionsDialog by mutableStateOf(false)
    var showPostOptionsFor: Int = -1

    fun initFeed() {
        AuthService.fetchAuthSession(onSuccess = {
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
        }, onError = {
            resetFeed()
        })
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
        this.showCreatePostDialog = true
    }

    fun dismissCreatePostDialog() {
        this.showCreatePostDialog = false
    }

    fun showPostOptions(postId: Int) {
        this.showPostOptionsDialog = true
        this.showPostOptionsFor = postId
    }

    fun dismissPostOptions() {
        this.showPostOptionsDialog = false
        this.showPostOptionsFor = -1
    }

    fun showDeletePostAlert() {
        this.showPostOptionsDialog = false
        this.showDeletePostAlert = true
    }

    fun dismissDeletePostAlert() {
        this.showDeletePostAlert = false
        this.showPostOptionsFor = -1
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

    fun deletePost() {
        val deletePostDto = DeletePostDto(
            postId = this.showPostOptionsFor,
            username = userName
        )
        this.dismissDeletePostAlert()
        ApiService.deletePost(deletePostDto) {
            val tempPosts = this.posts.map { it.copy() }.toMutableList()
            tempPosts.removeIf { p -> p.id == deletePostDto.postId }
            this.posts.run {
                clear()
                addAll(tempPosts)
            }
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

    fun createPost(text: String, imageLocation: String = "") {
        val createPostDto = CreatePostDto(
            details = text,
            username = this.userName,
            imageLocation = imageLocation
        )
        this.showCreatePostDialog = false
        ApiService.createPost(createPostDto) { post ->
            post.details
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
            this.posts.removeIf { p -> postsToAdd.any { it.id == p.id } }
            this.posts.addAll(postsToAdd)
            this.posts.sortByDescending { it.id }
            isLoading = false
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
