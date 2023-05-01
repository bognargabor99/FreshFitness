package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun LoggedInScreen(
    onLogOut: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            ProfileInfo()
        }
        IconButton(
            onClick = onLogOut,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = stringResource(R.string.logout))
        }
    }
}

@Composable
private fun ProfileImage(modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .size(154.dp)
            .padding(5.dp),
        shape = CircleShape,
        border = BorderStroke(0.5.dp, Color.LightGray),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://i.pinimg.com/originals/ae/ec/c2/aeecc22a67dac7987a80ac0724658493.jpg")
                .crossfade(500)
                .build(),
            error = painterResource(R.drawable.default_profile)
        )
        Image(
            painter = painter,
            contentDescription = "profile image",
            modifier = modifier
                .size(145.dp)
                .background(Color.White),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ProfileInfo() {
    Column(
        modifier = Modifier
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            text = "Gabor Bela Bognar"
        )

        Text(
            text = "Android Compose Programmer",
            modifier = Modifier.padding(4.dp)
        )

        Text(
            text = "BME AUT",
            modifier = Modifier.padding(3.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview
@Composable
fun ProfileImagePreview() {
    ProfileImage()
}