package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun LoggedInScreen(
    onLaunch: () -> Unit,
    isAdmin: Boolean,
    onLogOut: () -> Unit
) {
    LaunchedEffect(key1 = false) {
        onLaunch()
    }
    Box {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            ProfileInfo(isAdmin)
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
        Image(
            painter = painterResource(id = R.drawable.default_profile),
            contentDescription = "profile image",
            modifier = modifier
                .size(145.dp)
                .background(Color.White),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ProfileInfo(
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
            text = "Gabor Bela Bognar"
        )

        Text(
            text = if (isAdmin) "Fresh & Fitness Admin" else "Fresh & Fitness User",
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