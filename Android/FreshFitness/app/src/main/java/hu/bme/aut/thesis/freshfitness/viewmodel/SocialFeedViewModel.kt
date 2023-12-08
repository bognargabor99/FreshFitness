package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
import hu.bme.aut.thesis.freshfitness.amplify.StorageService
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.CommentOnPostDto
import hu.bme.aut.thesis.freshfitness.model.social.CreatePostDto
import hu.bme.aut.thesis.freshfitness.model.social.DeleteCommentDto
import hu.bme.aut.thesis.freshfitness.model.social.DeletePostDto
import hu.bme.aut.thesis.freshfitness.model.social.Post
import hu.bme.aut.thesis.freshfitness.model.state.SocialState
import hu.bme.aut.thesis.freshfitness.service.PostService
import hu.bme.aut.thesis.freshfitness.util.decodeJWT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection

class SocialFeedViewModel : ViewModel() {
    private val postService = PostService()
    val posts = mutableStateListOf<Post>()

    private val _socialState = MutableStateFlow(SocialState())
    val socialState: StateFlow<SocialState> = _socialState.asStateFlow()

    // Showing AlertDialog likes
    lateinit var shownLikesPost: Post

    // Showing AlertDialog comments
    lateinit var shownCommentsPost: Post

    // Showing AddCommentDialog
    private lateinit var commentedPost: Post

    fun initFeed() {
        AuthService.fetchAuthSession(onSuccess = {
            if (it.isSignedIn) {
                val session = (it as AWSCognitoAuthSession)
                val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                val jsonObject = JSONObject(jwt)
                _socialState.update { state ->
                    state.copy(isLoggedIn = true, username = jsonObject.getString("username"))
                }
            } else {
                _socialState.update { state ->
                    state.copy(isLoggedIn = false, username = "")
                }
            }
            resetFeed()
        }, onError = {
            resetFeed()
        })
    }

    fun showLikes(postId: Int) {
        this.shownLikesPost = this.posts.single { p -> p.id == postId }
        _socialState.update { state ->
            state.copy(showLikesDialog = true)
        }
    }

    fun hideLikes() {
        _socialState.update { state ->
            state.copy(showLikesDialog = false)
        }
    }

    fun showComments(postId: Int) {
        this.shownCommentsPost = this.posts.single { p -> p.id == postId }
        _socialState.update { state ->
            state.copy(showCommentsDialog = true)
        }
    }

    fun hideComments() {
        _socialState.update { state ->
            state.copy(showCommentsDialog = false)
        }
    }

    fun startCommenting(postId: Int) {
        this.commentedPost = this.posts.single { p -> p.id == postId }
        _socialState.update { state ->
            state.copy(showAddCommentDialog = true)
        }
    }

    fun endCommenting() {
        _socialState.update { state ->
            state.copy(showAddCommentDialog = false)
        }
    }

    fun addComment(text: String) {
        val commentDto = CommentOnPostDto(
            postId = this.commentedPost.id,
            text = text.trim(),
            username = this._socialState.value.username
        )
        commentOnPost(commentDto)
    }

    fun floatingButtonClick() {
        _socialState.update { state ->
            state.copy(showCreatePostDialog = true)
        }
    }

    fun dismissCreatePostDialog() {
        _socialState.update { state ->
            state.copy(showCreatePostDialog = false)
        }
    }

    fun showPostOptions(postId: Int) {
        _socialState.update { state ->
            state.copy(showPostOptionsDialog = true, showPostOptionsFor = postId)
        }
    }

    fun dismissPostOptions() {
        _socialState.update { state ->
            state.copy(showPostOptionsDialog = false, showPostOptionsFor = -1)
        }
    }

    fun showCommentOptions(comment: Comment) {
        _socialState.update { state ->
            state.copy(showCommentOptionsDialog = true, showCommentOptionsFor = comment.id)
        }
    }

    fun dismissCommentOptions() {
        _socialState.update { state ->
            state.copy(showCommentOptionsDialog = false, showCommentOptionsFor = -1)
        }
    }

    fun showDeleteCommentAlert() {
        _socialState.update { state ->
            state.copy(
                showCommentOptionsDialog = false, showDeleteCommentAlert = true
            )
        }
    }

    fun dismissDeleteCommentAlert() {
        _socialState.update { state ->
            state.copy(showDeleteCommentAlert = false, showCommentOptionsFor = -1)
        }
    }

    fun showDeletePostAlert() {
        _socialState.update { state ->
            state.copy(showPostOptionsDialog = false, showDeletePostAlert = true)
        }
    }

    fun dismissDeletePostAlert() {
        _socialState.update { state ->
            state.copy(showDeletePostAlert = false, showPostOptionsFor = -1)
        }
    }

    fun showFullScreenImage(location: String) {
        _socialState.update { state ->
            state.copy(fullScreenImageLocation = "${BuildConfig.S3_IMAGES_BASE_URL}$location", showImageFullScreen = true)
        }
    }

    fun hideFullScreenImage() {
        _socialState.update { state ->
            state.copy(showImageFullScreen = false, fullScreenImageLocation = "")
        }
    }

    fun setDetailedPost(post: Post) {
        _socialState.update { state ->
            state.copy(detailedPost = post)
        }
    }

    fun deleteComment() {
        val deleteCommentDto = DeleteCommentDto(
            id = _socialState.value.showCommentOptionsFor,
            username = _socialState.value.username
        )
        this.dismissDeleteCommentAlert()
        ApiService.deleteComment(deleteCommentDto) {
            val post = this.posts.single { p -> p.comments.singleOrNull { c -> c.id == deleteCommentDto.id } != null }.copy()
            post.commentCount--
            post.comments.removeIf { c -> c.id == deleteCommentDto.id }
            setDetailedPost(post)
            val indexOfPost = this.posts.indexOfFirst { p -> p.id == post.id }
            this.posts[indexOfPost] = post
        }
    }

    fun deletePost() {
        val deletePostDto = DeletePostDto(
            postId = _socialState.value.showPostOptionsFor,
            username = _socialState.value.username
        )
        val imageToDelete = this.posts.single { p -> p.id == deletePostDto.postId }.imageLocation
        this.dismissDeletePostAlert()
        if (imageToDelete.isNotBlank())
            StorageService.deleteFile(imageToDelete.substring(7))

        ApiService.deletePost(deletePostDto) {
            val tempPosts = this.posts.map { it.copy() }.toMutableList()
            tempPosts.removeIf { p -> p.id == deletePostDto.postId }
            tempPosts.firstOrNull()
            _socialState.update { state ->
                state.copy(detailedPost = tempPosts.firstOrNull())
            }
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
            if (p.likes.contains(_socialState.value.username)) {
                p.likeCount--
                p.likes.remove(_socialState.value.username)
            } else {
                p.likeCount++
                p.likes.add(_socialState.value.username)
            }
        }
        this.posts[postToLikeIndex] = postToLike
        setDetailedPost(postToLike)

        ApiService.likePost(post.id, _socialState.value.username, onError = {
            val likedPost = this.posts.single { p -> p.id == post.id }
            val likedPostIndex = this.posts.indexOfFirst { p -> p.id == post.id }

            likedPost.also { p ->
                if (p.likes.contains(_socialState.value.username)) {
                    p.likeCount--
                    p.likes.remove(_socialState.value.username)
                } else {
                    p.likeCount++
                    p.likes.add(_socialState.value.username)
                }
            }
            this.posts[likedPostIndex] = likedPost
        })
    }

    private fun getCommentsForPost(postId: Int) {
        ApiService.getCommentsForPost(postId, onSuccess = { comments ->
            this.posts.singleOrNull { p -> p.id == postId }?.comments = comments
            if (_socialState.value.detailedPost == null || _socialState.value.detailedPost?.id == postId) {
                setDetailedPost(this.posts.single { p -> p.id == postId })
            }
        })
    }

    private fun commentOnPost(commentDto: CommentOnPostDto) {
        _socialState.update {
            it.copy(showAddCommentDialog = false)
        }

        ApiService.commentOnPost(commentDto, onSuccess = { newComment ->
            val postToComment = this.posts.single { p -> p.id == commentDto.postId }.copy()
            val postToCommentIndex = this.posts.indexOfFirst { p -> p.id == commentDto.postId }
            postToComment.comments.add(newComment)
            postToComment.commentCount++
            this.posts[postToCommentIndex] = postToComment
            setDetailedPost(postToComment)
        })
    }

    fun createPost(text: String, contentUri: Uri?, context: Context) {
        _socialState.update {
            it.copy(postCreationButtonsEnabled = false, showUploadState = true, uploadText = "Processing file...")
        }
        var mimeType: String? = null
        var buffer: ByteArrayOutputStream? = null
        if (contentUri != null) {
            val file = context.contentResolver.openInputStream(contentUri)
            buffer = ByteArrayOutputStream()
            var nRead: Int
            val data = ByteArray(16384)
            while (file!!.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            file.close()

            try {
                val bufferedStream = BufferedInputStream(context.contentResolver.openInputStream(contentUri))
                mimeType = URLConnection.guessContentTypeFromStream(bufferedStream)
                Log.i("mime_type_detection", "MimeType detected: $mimeType")
            } catch (e: Exception) {
                Log.e("mime_type_detection", "MimeType could not be determined", e)
                mimeType = null
            }
        }

        val updatePosts: (String) -> Unit = { location ->
            val createPostDto = CreatePostDto(
                details = text,
                username = _socialState.value.username,
                imageLocation = location
            )
            _socialState.update {
                it.copy(showUploadState = false, uploadState = 0.0, uploadText = "", showCreatePostDialog = false, postCreationButtonsEnabled = true)
            }
            ApiService.createPost(createPostDto) { post ->
                this.posts.add(0, post)
            }
        }

        if (buffer != null && !mimeType.isNullOrBlank() && mimeType.startsWith("image/")) {
            _socialState.update {
                it.copy(uploadText = "Uploading file...")
            }
            val f = File(context.filesDir, "tempFile.${mimeType.substring(6)}")
            f.writeBytes(buffer.toByteArray())
            postService.uploadFile(
                userName = _socialState.value.username,
                file = f,
                extension = mimeType.substring(6),
                onFractionCompleted = { fraction -> _socialState.update { it.copy(uploadState = fraction) } },
                onSuccess = { location ->
                    f.delete()
                    updatePosts(location)
                })
        } else {
            updatePosts("")
        }
    }

    private fun resetFeed() {
        posts.clear()
        _socialState.update {
            it.copy(nextPage = 0,isLoading = true)
        }
        getNextPosts()
    }

    fun loadMorePosts() {
        _socialState.update {
            it.copy(isLoadingMore = true)
        }
        getNextPosts()
    }

    private fun getNextPosts() {
        ApiService.getPosts(_socialState.value.nextPage, onSuccess = { newPosts ->
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
                        userProfileImage = pagedPost.ownerProfile,
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
            _socialState.update {
                it.copy(detailedPost = this.posts.firstOrNull(), isLoading = false, isLoadingMore = false, nextPage = it.nextPage + 1, lastFetchedCount = newPosts.size)
            }
            for (post in postsToAdd) {
                getCommentsForPost(post.id)
            }
        })
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SocialFeedViewModel()
            }
        }
    }
}
