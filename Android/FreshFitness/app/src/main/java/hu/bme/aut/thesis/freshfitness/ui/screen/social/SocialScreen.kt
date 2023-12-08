package hu.bme.aut.thesis.freshfitness.ui.screen.social

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.Post
import hu.bme.aut.thesis.freshfitness.model.state.SocialState
import hu.bme.aut.thesis.freshfitness.ui.screen.nocontent.NetworkUnavailable
import hu.bme.aut.thesis.freshfitness.ui.util.EmptyScreen
import hu.bme.aut.thesis.freshfitness.ui.util.FreshFitnessContentType
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.ui.util.OkCancelDialog
import hu.bme.aut.thesis.freshfitness.ui.util.ScreenLoading
import hu.bme.aut.thesis.freshfitness.ui.util.UploadStateAlert
import hu.bme.aut.thesis.freshfitness.ui.util.isScrollingUp
import hu.bme.aut.thesis.freshfitness.ui.util.media.FullScreenImage
import hu.bme.aut.thesis.freshfitness.ui.util.media.ImagePickers
import hu.bme.aut.thesis.freshfitness.util.parseDateToTimeSince
import hu.bme.aut.thesis.freshfitness.viewmodel.SocialFeedViewModel

@Composable
fun SocialScreen(
    contentType: FreshFitnessContentType,
    networkAvailable: Boolean,
    viewModel: SocialFeedViewModel = viewModel(factory = SocialFeedViewModel.factory)
) {
    val context = LocalContext.current
    val state by viewModel.socialState.collectAsState()

    LaunchedEffect(key1 = networkAvailable) {
        if (networkAvailable && viewModel.posts.isEmpty())
            viewModel.initFeed()
    }
    @Suppress("DEPRECATION")
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = viewModel::initFeed
    ) {
        if (!networkAvailable && viewModel.posts.isEmpty()) {
            NetworkUnavailable()
        }
        else if (state.isLoading) {
            LoadingSocialFeed()
        }
        else {
            LoadedSocialFeed(
                contentType = contentType,
                posts = viewModel.posts,
                state = state,
                onClickPost = viewModel::setDetailedPost,
                onCreatePost = viewModel::floatingButtonClick,
                onLikePost = viewModel::likePost,
                onShowLikes = viewModel::showLikes,
                onShowComments = viewModel::showComments,
                onStartComment = viewModel::startCommenting,
                onShowPostOptions = viewModel::showPostOptions,
                onImageClick = viewModel::showFullScreenImage,
                onLoadMore = viewModel::loadMorePosts,
                onShowCommentOptions = viewModel::showCommentOptions
            )
        }
        if (state.showLikesDialog) {
            LikesDialog(post = viewModel.shownLikesPost, onDismiss = viewModel::hideLikes)
        }
        if (state.showCommentsDialog) {
            CommentsDialog(
                post = viewModel.shownCommentsPost,
                deleteCommentEnabled = { state.username == it },
                onDismiss =  viewModel::hideComments,
                onShowCommentOptions = viewModel::showCommentOptions
            )
        }
        if (state.showAddCommentDialog) {
            AddCommentDialog(onComment = viewModel::addComment, onDismiss = viewModel::endCommenting)
        }
        if (state.showCreatePostDialog) {
            CreatePostDialog(
                postCreationButtonsEnabled = state.postCreationButtonsEnabled,
                onPost = { text, contentUri -> viewModel.createPost(text, contentUri, context) },
                onDismiss = viewModel::dismissCreatePostDialog
            )
        }
        if (state.showUploadState) {
            UploadStateAlert(text = state.uploadText, fractionCompleted = state.uploadState)
        }
        if (state.showPostOptionsDialog) {
            OptionsDialog(
                onDelete = viewModel::showDeletePostAlert,
                onDismiss = viewModel::dismissPostOptions
            )
        }
        if (state.showCommentOptionsDialog) {
            OptionsDialog(
                onDelete = {
                    viewModel.hideComments()
                    viewModel.showDeleteCommentAlert() },
                onDismiss = viewModel::dismissCommentOptions
            )
        }
        if (state.showDeletePostAlert) {
            DeleteAlert(
                what = "post",
                onDelete = viewModel::deletePost,
                onDismiss = viewModel::dismissDeletePostAlert
            )
        }
        if (state.showDeleteCommentAlert) {
            DeleteAlert(
                what = "comment",
                onDelete = viewModel::deleteComment,
                onDismiss = viewModel::dismissDeleteCommentAlert
            )
        }
        if (state.showImageFullScreen) {
            FullScreenImage(
                imageUrl = state.fullScreenImageLocation,
                onDismiss = viewModel::hideFullScreenImage
            )
        }
    }
}

@Composable
fun LoadingSocialFeed() {
    ScreenLoading(loadingText = stringResource(R.string.loading_posts))
}

@Composable
fun LoadedSocialFeed(
    contentType: FreshFitnessContentType,
    posts: List<Post>,
    state: SocialState,
    onClickPost: (Post) -> Unit,
    onCreatePost: () -> Unit,
    onLikePost: (Post) -> Unit,
    onShowLikes: (Int) -> Unit,
    onShowComments: (Int) -> Unit,
    onStartComment: (Int) -> Unit,
    onShowPostOptions: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onShowCommentOptions: (Comment) -> Unit
) {
    when(contentType) {
        FreshFitnessContentType.LIST_ONLY -> {
            LoadedSocialFeedListOnly(
                posts = posts,
                state = state,
                onCreatePost = onCreatePost,
                onLikePost = onLikePost,
                onShowLikes = onShowLikes,
                onShowComments = onShowComments,
                onStartComment = onStartComment,
                onShowPostOptions = onShowPostOptions,
                onImageClick = onImageClick,
                onLoadMore = onLoadMore
            )
        }
        FreshFitnessContentType.LIST_AND_DETAIL -> {
            LoadedSocialFeedListAndDetail(
                modifier = Modifier,
                posts = posts,
                state = state,
                onClickPost = onClickPost,
                onCreatePost = onCreatePost,
                onLoadMore = onLoadMore,
                onLikePost = onLikePost,
                onShowLikes = onShowLikes,
                onStartComment = onStartComment,
                onShowPostOptions = onShowPostOptions,
                onImageClick = onImageClick,
                onShowCommentOptions = onShowCommentOptions
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadedSocialFeedListOnly(
    posts: List<Post>,
    state: SocialState,
    onCreatePost: () -> Unit,
    onLikePost: (Post) -> Unit,
    onShowLikes: (Int) -> Unit,
    onShowComments: (Int) -> Unit,
    onStartComment: (Int) -> Unit,
    onShowPostOptions: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    Scaffold(floatingActionButton = { if (state.username.isNotBlank()) { NewPostFAB(lazyListState.isScrollingUp(), onCreatePost) } }) {
        if (posts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(it),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Header(stringResource(R.string.newest_posts))
                }
                itemsIndexed(items = posts, key = { _, p -> p.id }) {_, p ->
                    PostCardDetailed(
                        modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)),
                        post = p,
                        userName = state.username,
                        onLikePost = onLikePost,
                        editEnabled = state.isLoggedIn,
                        onShowLikes = onShowLikes,
                        onShowComments = onShowComments,
                        onStartComment = onStartComment,
                        onShowPostOptions = onShowPostOptions,
                        onImageClick = onImageClick,
                        deleteEnabled = p.username == state.username
                    )
                }
                item {
                    if (state.isLoadingMore)
                        InfiniteCircularProgressBar()
                    else if (state.lastFetchedCount != 0)
                        LoadMoreButton(onClick = onLoadMore)
                    else
                        EndOfFeedBanner()
                }
            }
        } else {
            NoPostsOnFeed()
        }
    }
}

@Composable
fun LoadedSocialFeedListAndDetail(
    modifier: Modifier = Modifier,
    posts: List<Post>,
    state: SocialState,
    onClickPost: (Post) -> Unit,
    onCreatePost: () -> Unit,
    onLoadMore: () -> Unit,
    onLikePost: (Post) -> Unit,
    onShowLikes: (Int) -> Unit,
    onStartComment: (Int) -> Unit,
    onShowPostOptions: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    onShowCommentOptions: (Comment) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = modifier.weight(1f)) {
            PostListListAndDetail(
                posts = posts,
                createPostEnabled = state.username.isNotBlank(),
                onCreatePost = onCreatePost,
                canLoadMore = state.lastFetchedCount != 0,
                onLoadMore = onLoadMore,
                isLoadingMore = state.isLoadingMore,
                onClickPost = onClickPost
            )
        }
        Column(modifier = modifier.weight(1f)) {
            if (state.detailedPost != null) {
                DetailedPost(
                    post = state.detailedPost!!,
                    userName = state.username,
                    onLikePost = onLikePost,
                    editEnabled = state.isLoggedIn,
                    onShowLikes = onShowLikes,
                    onStartComment = onStartComment,
                    onShowPostOptions = onShowPostOptions,
                    onImageClick = onImageClick,
                    deleteCommentEnabled = { it == state.username },
                    onShowCommentOptions = onShowCommentOptions
                )
            }
            else {
                NoPostSelected()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostListListAndDetail(
    posts: List<Post>,
    createPostEnabled: Boolean,
    onCreatePost: () -> Unit,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    onClickPost: (Post) -> Unit
) {
    val lazyListState = rememberLazyListState()
    Scaffold(floatingActionButton = { if (createPostEnabled) { NewPostFAB(lazyListState.isScrollingUp(), onCreatePost) } }) {
        if (posts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(it),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Header(stringResource(R.string.newest_posts))
                }
                itemsIndexed(items = posts, key = { _, p -> p.id }) {_, p ->
                    PostCardSimple(
                        modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)),
                        post = p,
                        onClick = onClickPost
                    )
                }
                item {
                    if (isLoadingMore)
                        InfiniteCircularProgressBar()
                    else if (canLoadMore)
                        LoadMoreButton(onClick = onLoadMore)
                    else
                        EndOfFeedBanner()
                }
            }
        } else {
            NoPostsOnFeed()
        }
    }
}

@Composable
fun NoPostsOnFeed() {
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

@Composable
fun LoadMoreButton(
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(text = stringResource(R.string.load_more))
    }
}

@Composable
fun EndOfFeedBanner() {
    Text(modifier = Modifier.padding(4.dp), text = stringResource(R.string.end_of_feed), fontStyle = FontStyle.Italic)
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
                .padding(horizontal = 16.dp, vertical = 8.dp),

        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCardDetailed(
    modifier: Modifier = Modifier,
    post: Post,
    userName: String,
    onLikePost: (Post) -> Unit = { },
    editEnabled: Boolean = false,
    onShowLikes: (Int) -> Unit = { },
    onShowComments: (Int) -> Unit = { },
    onStartComment: (Int) -> Unit = { },
    onShowPostOptions: (Int) -> Unit = { },
    onImageClick: (String) -> Unit = { },
    deleteEnabled: Boolean,
    alwaysShowAllRows: Boolean = false,
    additionalContent: @Composable () -> Unit = { },
) {
    var showAllRows by remember { mutableStateOf(false) }
    val maxLines by animateIntAsState(if (showAllRows) 10 else 1, label = "")
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .combinedClickable(onLongClick = {
                if (editEnabled && deleteEnabled)
                    onShowPostOptions(post.id)
            }) { showAllRows = !showAllRows },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PostCardHeader(post = post, deleteEnabled = deleteEnabled, onShowPostOptions = onShowPostOptions)
            PostCardDetails(details = post.details, maxLines = if (alwaysShowAllRows) 20 else maxLines)
            if (post.imageLocation.isNotBlank())
                PostCardImage(imageLocation = post.imageLocation, onImageClick = onImageClick)
            PostCardActions(
                post = post,
                userName = userName,
                onLikePost = onLikePost,
                editEnabled = editEnabled,
                onShowLikes = onShowLikes,
                onShowComments = onShowComments,
                onStartComment = onStartComment
            )
            additionalContent()
        }
    }
}

@Composable
fun PostCardSimple(
    modifier: Modifier = Modifier,
    post: Post,
    onClick: (Post) -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .clickable { onClick(post) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PostCardHeader(post = post, deleteEnabled = false)
            PostCardDetails(details = post.details, maxLines = 1)
            PostStats(likeCount = post.likeCount, commentCount = post.commentCount)
        }
    }
}

@Composable
fun NoPostSelected() {
    EmptyScreen("No post selected.", "Click on a post to show the details of it")
}

@Composable
fun PostCardHeader(
    post: Post,
    deleteEnabled: Boolean,
    onShowPostOptions: (Int) -> Unit = { }
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PostCardHeaderProfileImage(post.userProfileImage)
            PostCardHeaderTexts(post.username, post.createdAt)
        }
        PostCardOptionsButton(
            deleteEnabled = deleteEnabled,
            onClick = { onShowPostOptions(post.id) }
        )
    }
}


@Composable
fun PostStats(
    likeCount: Int,
    commentCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 8.dp, bottom = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (likeCount == 1) "1 like" else "$likeCount likes",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = if (commentCount == 1) "1 comment" else "$commentCount comments",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PostCardHeaderProfileImage(profileImageKey: String) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data("${BuildConfig.S3_IMAGES_BASE_URL}${profileImageKey}")
        .error(R.drawable.default_profile)
        .placeholder(R.drawable.default_profile)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val painter = rememberAsyncImagePainter(model)
    SizeableProfileImage(painter, 50.dp)
}

@Composable
fun PostCardHeaderTexts(userName: String, createdAt: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = userName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = parseDateToTimeSince(createdAt),
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PostCardOptionsButton(deleteEnabled: Boolean, onClick: () -> Unit) {
    if (deleteEnabled) {
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Default.MoreVert, tint = MaterialTheme.colorScheme.onBackground, contentDescription = stringResource(R.string.delete_post))
        }
    }
}

@Composable
fun PostCardDetails(details: String, maxLines: Int) {
    if (details.isNotBlank()) {
        Text(
            text = details,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            maxLines = maxLines
        )
    }
}

@Composable
fun PostCardImage(imageLocation: String, onImageClick: (String) -> Unit) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data("${BuildConfig.S3_IMAGES_BASE_URL}$imageLocation")
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val painter = rememberAsyncImagePainter(model, imageLoader = imageLoader)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Image from $imageLocation",
            modifier = Modifier
                .heightIn(max = 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(6.dp, Color.Gray, RoundedCornerShape(16.dp))
                .clickable { onImageClick(imageLocation) }
                .fillMaxWidth(0.9f),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun PostCardActions(
    post: Post,
    userName: String,
    onLikePost: (Post) -> Unit = { },
    editEnabled: Boolean = false,
    onShowLikes: (Int) -> Unit = { },
    onShowComments: (Int) -> Unit = { },
    onStartComment: (Int) -> Unit = { },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(enabled = editEnabled, onClick = { onLikePost(post) }) {
            Icon(
                imageVector = if (post.likes.any { it == userName }) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = "Like or dislike post"
            )
        }
        Text(
            modifier = Modifier.clickable(enabled = post.likeCount > 0) { onShowLikes(post.id) },
            text = if (post.likeCount == 1) "1 like" else "${post.likeCount} likes"
        )
        IconButton(enabled = editEnabled, onClick = { onStartComment(post.id) }) {
            Icon(imageVector = Icons.Outlined.Chat, contentDescription = "Comment on post")
        }
        Text(
            modifier = Modifier.clickable(enabled = post.commentCount > 0) { onShowComments(post.id) },
            text = if (post.commentCount == 1) "1 comment" else "${post.commentCount} comments"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(
    modifier: Modifier = Modifier,
    comment: Comment,
    deleteCommentEnabled: (String) -> Boolean,
    onShowCommentOptions: (Comment) -> Unit = { }
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(enabled = deleteCommentEnabled(comment.username), onLongClick = {
                onShowCommentOptions(comment)
            }) { },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CommentProfileImage(imageKey = comment.userImage)
            CommentContent(userName = comment.username, text = comment.text, createdAt = comment.createdAt)
        }
    }
}

@Composable
fun CommentProfileImage(imageKey: String) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data("${BuildConfig.S3_IMAGES_BASE_URL}${imageKey}")
        .error(R.drawable.default_profile)
        .placeholder(R.drawable.default_profile)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val painter = rememberAsyncImagePainter(model)
    SizeableProfileImage(painter, 50.dp)
}

@Composable
fun CommentContent(userName: String, text: String, createdAt: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp)
            Text(text = parseDateToTimeSince(createdAt),
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp)
        }
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(postCreationButtonsEnabled: Boolean, onPost: (String, Uri?) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var photoUri: Uri? by remember { mutableStateOf(null) }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CreatePostDialogTitle()
                ImagePickers(enabled = postCreationButtonsEnabled, onPhotoPicked = { photoUri = it })
                CreatePostDialogImage(photoUri = photoUri)
                CreatePostDialogTextField(
                    enabled = postCreationButtonsEnabled,
                    text = text,
                    onValueChange = { text = it }
                )
                CreatePostDialogActionButtons(
                    enabled = postCreationButtonsEnabled,
                    onPost = {  if (text.isEmpty() && photoUri == null) onDismiss() else onPost(text, photoUri) },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun CreatePostDialogTitle() {
    Text(text = stringResource(R.string.create_post), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
}

@Composable
fun CreatePostDialogImage(photoUri: Uri?) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data(data = photoUri)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val painter = rememberAsyncImagePainter(model, imageLoader = imageLoader)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .heightIn(max = (if (photoUri != null) 300 else 200).dp)
            .clip(RoundedCornerShape(16.dp))
            .border(6.dp, Color.Gray, RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f)
            .background(Color.LightGray),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
fun CreatePostDialogTextField(
    enabled: Boolean,
    text: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        minLines = 3,
        maxLines = 5,
        placeholder = { Text(text = stringResource(R.string.write_about_your_fitness_journey)) },
        value = text,
        onValueChange = onValueChange
    )
}

@Composable
fun CreatePostDialogActionButtons(
    enabled: Boolean,
    onPost: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = onDismiss, enabled = enabled) {
            Text(text = stringResource(R.string.cancel))
        }
        Button(onClick = onPost, enabled = enabled) {
            Text(text = stringResource(R.string.post))
        }
    }
}

@Composable
fun SizeableProfileImage(painter: Painter, size: Dp) {
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}

@Composable
fun NewPostFAB(
    extended: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        elevation = FloatingActionButtonDefaults.elevation(12.dp),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Create post")
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(R.string.create),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 3.dp)
                )
            }
        }
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
fun CommentsDialog(
    post: Post,
    deleteCommentEnabled: (String) -> Boolean,
    onDismiss: () -> Unit,
    onShowCommentOptions: (Comment) -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            CommentsDialogContent(
                post = post,
                deleteCommentEnabled = deleteCommentEnabled,
                onShowCommentOptions = onShowCommentOptions
            )
        }
    }
}

@Composable
fun CommentsDialogContent(
    post: Post,
    showTitle: Boolean = true,
    deleteCommentEnabled: (String) -> Boolean,
    onShowCommentOptions: (Comment) -> Unit
) {
    if (post.comments.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showTitle)
                Text(text = stringResource(R.string.comments), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            Column {
                post.comments.forEach {
                    Comment(
                        comment = it,
                        deleteCommentEnabled = deleteCommentEnabled,
                        onShowCommentOptions = onShowCommentOptions
                    )
                }
            }
        }
    }
    else {
        NoCommentsContent()
    }
}

@Composable
fun NoCommentsContent() {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.no_comments), fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        Text(text = stringResource(R.string.be_the_first_to_comment), fontSize = 16.sp)
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
            modifier = Modifier.wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Add comment", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
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

@Composable
fun DeleteAlert(
    what: String,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    OkCancelDialog(
        title = "Delete $what",
        subTitle = "Do you really want to delete this $what?",
        onOk = onDelete,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsDialog(
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDelete() }
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    text = stringResource(R.string.delete),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun CreatePostDialogPreview() {
    CreatePostDialog(postCreationButtonsEnabled = true, onPost = { _, _ -> }, {})
}

@Preview(showBackground = true)
@Composable
fun PostPreview() {
    PostCardDetailed(
        post = Post(
            id = 1,
            details = "My new personal best on the squat machine",
            username = "gaborbognar123",
            userProfileImage = "",
            imageLocation = "",
            createdAt = "2023-08-21T21:34",
            likeCount = 2,
            commentCount = 7
        ),
        userName = "gaborbognar123",
        deleteEnabled = true
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
            userProfileImage = "",
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
    Comment(
        comment = Comment(
            id = 0,
            postId = 0,
            text = "Comment in preview",
            username = "test_user",
            userImage = "",
            createdAt = "2023-12-05T18:42:00"
        ),
        deleteCommentEnabled = { false },
        onShowCommentOptions = { }
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteAlertPreview() {
    DeleteAlert(what = "post", onDelete = { }, onDismiss = { })
}