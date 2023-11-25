package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amplifyframework.ui.authenticator.ui.Authenticator
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun ProfileScreen() {
    Authenticator(
        headerContent = {
            Image(
                modifier = Modifier.height(160.dp).fillMaxWidth(),
                painter = painterResource(id = R.drawable.freshfitness_logo_big),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillHeight,
                contentDescription = null
            )
        }
    ) {
        LoggedInScreen()
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}