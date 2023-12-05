package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.amplifyframework.ui.authenticator.SignedInState
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.UploadStateAlert
import hu.bme.aut.thesis.freshfitness.ui.util.media.FullScreenImage
import hu.bme.aut.thesis.freshfitness.ui.util.media.ImagePickers
import hu.bme.aut.thesis.freshfitness.viewmodel.ProfileViewModel

@Composable
fun LoggedInScreen(
    state: SignedInState,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = false) {
        viewModel.fetchAuthSession()
    }
    Box {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(
                imageKey = viewModel.profileImageLocation,
                onClick = viewModel::showImageOptions
            )
            ProfileInfo(state, viewModel.isAdmin)
        }
        IconButton(
            onClick = viewModel::signOut,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = stringResource(R.string.logout))
        }
    }

    if (viewModel.showUpdateProfileDialog) {
        UpdateProfileImageDialog(
            updateEnabled = viewModel.updateProfileEnabled,
            onPost = { uri -> viewModel.setProfileImage(uri, context) },
            onDismiss = viewModel::dismissUpdateProfileImageDialog
        )
    }
    if (viewModel.showImageOptionsDialog) {
        OptionsDialog(
            options = viewModel.getOptionsMap(),
            onDismiss = viewModel::dismissImageOptions
        )
    }
    if (viewModel.showUploadState) {
        UploadStateAlert(text = viewModel.uploadText, fractionCompleted = viewModel.uploadState)
    }
    if (viewModel.showImageFullScreen) {
        FullScreenImage(
            imageUrl = "${BuildConfig.S3_IMAGES_BASE_URL}${viewModel.profileImageLocation}",
            onDismiss = viewModel::hideFullScreenImage
        )
    }
}

@Composable
private fun ProfileImage(
    modifier: Modifier = Modifier,
    imageKey: String = "",
    onClick: () -> Unit
) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data("${BuildConfig.S3_IMAGES_BASE_URL}${imageKey}")
        .error(R.drawable.default_profile)
        .placeholder(R.drawable.default_profile)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val painter = rememberAsyncImagePainter(model)
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .size(208.dp)
            .clickable { onClick() }
            .padding(5.dp),
        shape = CircleShape,
        border = BorderStroke(0.5.dp, Color.LightGray),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    ) {
        Image(
            painter = painter,
            contentDescription = "profile image",
            modifier = modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ProfileInfo(
    state: SignedInState,
    isAdmin: Boolean
) {
    Column(
        modifier = Modifier
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            text = state.user.username
        )

        Text(
            text = "Fresh & Fitness ${if (isAdmin) "Admin" else "User"}",
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsDialog(
    options: Map<String, (() -> Unit)>,
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
            Column {
                options.entries.forEach {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { (it.value)() }
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            text = it.key,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileImageDialog(
    updateEnabled: Boolean,
    onPost: (Uri?) -> Unit,
    onDismiss: () -> Unit
) {
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UpdateProfileImageTitle()
                ImagePickers(enabled = updateEnabled, onPhotoPicked = { photoUri = it })
                UpdateProfileImagePreview(photoUri = photoUri)
                UpdateProfileActionButtons(
                    updateEnabled = updateEnabled,
                    onUpdate = { if (photoUri != null) onPost(photoUri) else onDismiss() },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun UpdateProfileImageTitle() {
    Text(modifier = Modifier.padding(8.dp), text = stringResource(R.string.update_profile_image), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
}

@Composable
fun UpdateProfileImagePreview(photoUri: Uri?) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data(data = photoUri)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(model)
    Image(
        painter = painter,
        contentDescription = "Preview of selected image",
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .border(6.dp, Color.Gray, CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UpdateProfileActionButtons(
    updateEnabled: Boolean,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        OutlinedButton(onClick = onDismiss, enabled = updateEnabled) {
            Text(text = stringResource(R.string.cancel))
        }
        OutlinedButton(onClick = onUpdate, enabled = updateEnabled) {
            Text(text = stringResource(R.string.upload))
        }
    }
}

@Preview
@Composable
fun ProfileImagePreview() {
    ProfileImage(onClick = { })
}

@Preview
@Composable
fun OptionsDialogPreview() {
    OptionsDialog(
        options = mapOf("View" to { }, "Update" to { }, "Delete" to { }),
        onDismiss = { }
    )
}