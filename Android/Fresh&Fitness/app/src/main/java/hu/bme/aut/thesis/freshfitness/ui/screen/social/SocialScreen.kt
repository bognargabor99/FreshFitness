package hu.bme.aut.thesis.freshfitness.ui.screen.social

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.createImageFile
import hu.bme.aut.thesis.freshfitness.model.social.Comment
import hu.bme.aut.thesis.freshfitness.model.social.Post
import hu.bme.aut.thesis.freshfitness.parseDateToString
import hu.bme.aut.thesis.freshfitness.ui.util.FullScreenImage
import hu.bme.aut.thesis.freshfitness.ui.util.InfiniteCircularProgressBar
import hu.bme.aut.thesis.freshfitness.viewmodel.SocialFeedViewModel
import java.util.Objects
import kotlin.math.round

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
                createPostEnabled = viewModel.userName.isNotBlank(),
                onCreatePost = { viewModel.floatingButtonClick() },
                onLikePost = { viewModel.likePost(it) },
                editEnabled = viewModel.isLoggedIn,
                onShowLikes = { viewModel.showLikes(it.id) },
                onShowComments = { viewModel.showComments(it.id) },
                onStartComment = { viewModel.startCommenting(it.id) },
                onShowPostOptions = { viewModel.showPostOptions(it.id) },
                onImageClick = { viewModel.showFullScreenImage(it) }
            )
        }
        if (viewModel.showLikesDialog) {
            LikesDialog(viewModel.shownLikesPost) { viewModel.showLikesDialog = false }
        }
        if (viewModel.showCommentsDialog) {
            CommentsDialog(
                post = viewModel.shownCommentsPost,
                deleteCommentEnabled = { viewModel.userName == it },
                onDismiss =  { viewModel.showCommentsDialog = false },
                onShowCommentOptions = { viewModel.showCommentOptions(it.id) }
            )
        }
        if (viewModel.showAddCommentDialog) {
            AddCommentDialog(onComment = viewModel::addComment) { viewModel.showAddCommentDialog = false }
        }
        if (viewModel.showCreatePostDialog) {
            CreatePostDialog(
                postCreationButtonsEnabled = viewModel.postCreationButtonsEnabled,
                onPost = { text, contentUri -> viewModel.createPost(text, contentUri) },
                onDismiss = { viewModel.dismissCreatePostDialog() })
        }
        if (viewModel.showUploadState) {
            UploadStateAlert(text = viewModel.uploadText, fractionCompleted = viewModel.uploadState) { }
        }
        if (viewModel.showPostOptionsDialog) {
            OptionsDialog(
                onDelete = { viewModel.showDeletePostAlert() },
                onDismiss = { viewModel.dismissPostOptions()}
                )
        }
        if (viewModel.showCommentOptionsDialog) {
            OptionsDialog(
                onDelete = {
                    viewModel.showCommentsDialog = false
                    viewModel.showDeleteCommentAlert() },
                onDismiss = { viewModel.dismissCommentOptions()}
            )
        }
        if (viewModel.showDeletePostAlert) {
            DeleteAlert(
                what = "post",
                onDelete = { viewModel.deletePost() },
                onDismiss = { viewModel.dismissDeletePostAlert() })
        }
        if (viewModel.showDeleteCommentAlert) {
            DeleteAlert(
                what = "comment",
                onDelete = { viewModel.deleteComment() },
                onDismiss = { viewModel.dismissDeleteCommentAlert() })
        }
        if (viewModel.showImageFullScreen) {
            FullScreenImage(
                imageUrl = viewModel.fullScreenImageLocation,
                onDismiss = { viewModel.hideFullScreenImage() })
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadedSocialFeed(
    posts: List<Post>,
    userName: String,
    createPostEnabled: Boolean,
    onCreatePost: () -> Unit,
    onLikePost: (Post) -> Unit,
    editEnabled: Boolean,
    onShowLikes: (Post) -> Unit,
    onShowComments: (Post) -> Unit,
    onStartComment: (Post) -> Unit,
    onShowPostOptions: (Post) -> Unit,
    onImageClick: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = { if (createPostEnabled) { NewPostFAB(onCreatePost) } },
        floatingActionButtonPosition = FabPosition.End
    ) {
        if (posts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item {
                    Header(stringResource(R.string.newest_posts))
                }

                itemsIndexed(items = posts, key = { _, p -> p.id }) {_, p ->
                    PostCard(
                        modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)),
                        post = p,
                        userName = userName,
                        onLikePost = onLikePost,
                        editEnabled = editEnabled,
                        onShowLikes = onShowLikes,
                        onShowComments = onShowComments,
                        onStartComment = onStartComment,
                        onShowPostOptions = onShowPostOptions,
                        onImageClick = onImageClick)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: Post,
    userName: String,
    onLikePost: (Post) -> Unit = { },
    editEnabled: Boolean = false,
    onShowLikes: (Post) -> Unit = { },
    onShowComments: (Post) -> Unit = { },
    onStartComment: (Post) -> Unit = { },
    onShowPostOptions: (Post) -> Unit = { },
    onImageClick: (String) -> Unit = { }
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .combinedClickable(onLongClick = {
                onShowPostOptions(post)
            }) { },
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
            if (post.imageLocation.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://freshfitness-social-media-bucket100821-dev.s3.eu-north-1.amazonaws.com/${post.imageLocation}"),
                        contentDescription = null,
                        modifier = Modifier
                            .widthIn(min = 140.dp)
                            .heightIn(min = 100.dp, max = 200.dp)
                            .border(6.dp, Color.Gray)
                            .clickable { onImageClick(post.imageLocation) }
                    )
                }
            }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Comment(
    comment: Comment,
    deleteCommentEnabled: (String) -> Boolean,
    onShowCommentOptions: (Comment) -> Unit = { }
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .combinedClickable(enabled = deleteCommentEnabled(comment.username), onLongClick = {
                onShowCommentOptions(comment)
            }) { },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(postCreationButtonsEnabled: Boolean, onPost: (String, Uri?) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var photoUri: Uri? by remember { mutableStateOf(null) }
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = photoUri)
            .build()
    )
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() }
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
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Create post", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PhotoPicker(enabled = postCreationButtonsEnabled, onPhotoPicked = {
                        photoUri = it
                    })
                    CameraImageCapture(enabled = postCreationButtonsEnabled, onCapturedImage = {
                        photoUri = it
                    })
                }
                if (photoUri != null) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .widthIn(min = 260.dp)
                            .heightIn(min = 200.dp, max = 200.dp)
                            .border(6.0.dp, Color.Gray),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .heightIn(min = 200.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(6.0.dp, Color.Gray)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    enabled = postCreationButtonsEnabled,
                    minLines = 3,
                    maxLines = 5,
                    value = text,
                    onValueChange = { text = it }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = { onDismiss() }, enabled = postCreationButtonsEnabled) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(onClick = { onPost(text, photoUri) }, enabled = postCreationButtonsEnabled) {
                        Text(text = stringResource(R.string.post))
                    }
                }
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

@Composable
fun PhotoPicker(enabled: Boolean, onPhotoPicked: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onPhotoPicked(uri)
    }
    Button(enabled = enabled, onClick = {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }) {
        Text(text = "Select photo")
    }
}

@Composable
fun CameraImageCapture(enabled: Boolean, onCapturedImage: (Uri) -> Unit) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            onCapturedImage(uri)
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Button(enabled = enabled, onClick = {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }) {
        Text(text = "Take picture")
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
                        Comment(post.comments[index], deleteCommentEnabled, onShowCommentOptions)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAlert(
    what: String,
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
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Delete $what", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(Modifier.height(6.dp))
                Text("Do you really want to delete this $what?")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { onDismiss() }, colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Button(onClick = { onDelete() }, colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
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
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .clickable { onDelete() },
                    text = stringResource(R.string.delete),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadStateAlert(text: String, fractionCompleted: Double, onDismiss: () -> Unit) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = fractionCompleted.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("${round(fractionCompleted*100.0).toInt()}% completed", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview() {
    PostCard(
        post = Post(
            id = 1,
            details = "My new personal best on the squat machine",
            username = "gaborbognar123",
            imageLocation = "",
            createdAt = "2023-08-21T21:34",
            likeCount = 2,
            commentCount = 7),
        userName = ""
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
    Comment(comment = Comment(0, 0, "", "", ""), { false }) { }
}

@Preview(showBackground = true)
@Composable
fun DeleteAlertPreview() {
    DeleteAlert(what = "post", onDelete = { }, onDismiss = { })
}

@Preview(showBackground = true)
@Composable
fun UploadStateAlertPreview() {
    UploadStateAlert(text = "Processing file...", fractionCompleted = 0.5) {

    }
}