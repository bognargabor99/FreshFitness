package hu.bme.aut.thesis.freshfitness.ui.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.Post

@Composable
fun DetailedPost(
    post: Post,
    userName: String,
    onLikePost: (Post) -> Unit,
    editEnabled: Boolean,
    onShowLikes: (Int) -> Unit,
    onStartComment: (Int) -> Unit,
    onShowPostOptions: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    deleteCommentEnabled: (String) -> Boolean,
    onShowCommentOptions: (Comment) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PostCardDetailed(
            post = post,
            userName = userName,
            onLikePost = onLikePost,
            editEnabled = editEnabled,
            onShowLikes = onShowLikes,
            onStartComment = onStartComment,
            onShowPostOptions = onShowPostOptions,
            onImageClick = onImageClick,
            deleteEnabled = post.username == userName,
            alwaysShowAllRows = true
        ) {
            PostCardComments(
                post = post,
                deleteCommentEnabled = deleteCommentEnabled,
                onShowCommentOptions = onShowCommentOptions
            )
        }
    }
}

@Composable
fun PostCardComments(
    post: Post,
    deleteCommentEnabled: (String) -> Boolean,
    onShowCommentOptions: (Comment) -> Unit
) {
    CommentsDialogContent(
        post = post,
        showTitle = false,
        deleteCommentEnabled = deleteCommentEnabled,
        onShowCommentOptions = onShowCommentOptions
    )
}
