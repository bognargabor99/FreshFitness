package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amplifyframework.ui.authenticator.ui.Authenticator
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun ProfileScreen(
    onNavigateViewWorkoutsScreen: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Authenticator(
            headerContent = {
                ProfileHeader()
            }
        ) {
            LoggedInScreen(
                state = it,
                onNavigateViewWorkoutsScreen = onNavigateViewWorkoutsScreen
            )
        }
    }
}

@Composable
fun ProfileHeader() {
    Image(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(),
        painter = painterResource(id = R.drawable.freshfitness_logo_big),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillHeight,
        contentDescription = null
    )
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen {}
}