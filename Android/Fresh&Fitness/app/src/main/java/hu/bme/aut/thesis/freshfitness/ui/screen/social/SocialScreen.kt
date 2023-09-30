package hu.bme.aut.thesis.freshfitness.ui.screen.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.Post
import hu.bme.aut.thesis.freshfitness.parseDateToString
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.viewmodel.SocialFeedViewModel

@Composable
fun SocialScreen(
    viewModel: SocialFeedViewModel = viewModel(factory = SocialFeedViewModel.factory)
) {
    LaunchedEffect(key1 = false) {
        viewModel.initFeed()
    }
    @Suppress("DEPRECATION")
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = { viewModel.initFeed() }
    ) {
        if (viewModel.isLoading) {
            LoadingSocialFeed()
        }
        else {
            LoadedSocialFeed(
                posts = viewModel.posts,
                userName = viewModel.userName,
                onCreatePost = viewModel::floatingButtonClick,
                onLikePost = { viewModel.likePost(it) },
                editEnabled = viewModel.isLoggedIn,
                onShowLikes = { viewModel.showLikes(it.id) },
                onShowComments = { viewModel.showComments(it.id) },
                onStartComment = { viewModel.startCommenting(it.id) }
            )
        }
        if (viewModel.showLikesDialog) {
            LikesDialog(viewModel.shownLikesPost) { viewModel.showLikesDialog = false }
        }
        if (viewModel.showCommentsDialog) {
            CommentsDialog(viewModel.shownCommentsPost) { viewModel.showCommentsDialog = false }
        }
        if (viewModel.showAddCommentDialog) {
            AddCommentDialog(onComment = viewModel::addComment) { viewModel.showAddCommentDialog = false }
        }
    }
}

@Composable
fun LoadingSocialFeed() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfiniteCircularProgressBar()
            Text(
                text = stringResource(R.string.loading_posts),
                fontStyle = FontStyle.Italic,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun LoadedSocialFeed(
    posts: List<Post>,
    userName: String,
    onCreatePost: () -> Unit,
    onLikePost: (Post) -> Unit,
    editEnabled: Boolean,
    onShowLikes: (Post) -> Unit,
    onShowComments: (Post) -> Unit,
    onStartComment: (Post) -> Unit
) {
    Scaffold(
        floatingActionButton = { NewPostFAB(onCreatePost) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        if (posts.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    Header(stringResource(R.string.newest_posts))
                }

                items(posts.size) {i ->
                    PostCard(
                        post = posts[i],
                        userName = userName,
                        onLikePost = onLikePost,
                        editEnabled = editEnabled,
                        onShowLikes = onShowLikes,
                        onShowComments = onShowComments,
                        onStartComment = onStartComment)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = stringResource(R.string.no_posts),
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun Header(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.semantics { heading() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun PostCard(
    post: Post,
    userName: String,
    onLikePost: (Post) -> Unit = { },
    editEnabled: Boolean = false,
    onShowLikes: (Post) -> Unit = { },
    onShowComments: (Post) -> Unit = { },
    onStartComment: (Post) -> Unit = { }
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(text = parseDateToString(post.createdAt),
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp)
            }
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = post.details,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(enabled = editEnabled, onClick = { onLikePost(post) }) {
                    Icon(
                        imageVector = if (post.likes.any { it == userName }) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier.clickable(enabled = post.likeCount > 0) { onShowLikes(post) },
                    text = if (post.likeCount == 1) "1 like" else "${post.likeCount} likes"
                )
                IconButton(enabled = editEnabled, onClick = { onStartComment(post) }) {
                    Icon(imageVector = Icons.Outlined.Chat, contentDescription = null)
                }
                Text(
                    modifier = Modifier.clickable(enabled = post.commentCount > 0) { onShowComments(post) },
                    text = if (post.commentCount == 1) "1 comment" else "${post.commentCount} comments"
                )
            }
        }
    }
}

@Composable
fun Comment(comment: Comment) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_profile),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = comment.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp)
                    Text(text = parseDateToString(comment.createdAt),
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp)
                }
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = comment.text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun NewPostFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        elevation = FloatingActionButtonDefaults.elevation(12.dp),
        shape = CircleShape
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Create post")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikesDialog(post: Post, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.likes), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn {
                items(post.likeCount) {index ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = post.likes[index], fontSize = 18.sp)
                }
            }
        }
    }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsDialog(post: Post, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.comments), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn {
                    items(post.commentCount) {index ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Comment(post.comments[index])
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentDialog(onComment: (String) -> Unit = { }, onDismiss: () -> Unit = { }) {
    var comment: String by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Add comment", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(0.87f),
                        maxLines = 3,
                        value = comment,
                        onValueChange = { comment = it }
                    )
                    IconButton(modifier = Modifier.weight(0.1f), enabled = comment.isNotBlank(), onClick = { onComment(comment) }) {
                        Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview() {
    PostCard(
        Post(
            id = 1,
            details = "My new personal best on the squat machine",
            username = "gaborbognar123",
            imageLocation = "",
            createdAt = "2023-08-21T21:34",
            likeCount = 2,
            commentCount = 7),
        ""
    )
}

@Preview(showBackground = true)
@Composable
fun LikesDialogPreview() {
    LikesDialog(
        post = Post(
            id = 0,
            details = "",
            imageLocation = "",
            createdAt = "",
            username = "gaborbognar123",
            likeCount = 5,
            likes = mutableListOf("andrew_huberman", "jason_todd", "dick_grayson", "emily_monroe", "keanu_reeves"),
            commentCount = 0,
            comments = mutableListOf()
        )
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun AddCommentDialogPreview() {
    AddCommentDialog()
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header(text = stringResource(id = R.string.newest_posts))
}

@Preview(showBackground = true)
@Composable
fun CommentPreview() {
    Comment(comment = Comment(0, 0, "", "", ""))
}