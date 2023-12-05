package hu.bme.aut.thesis.freshfitness.ui.util.media

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.thesis.freshfitness.ui.screen.social.CameraImageCapture
import hu.bme.aut.thesis.freshfitness.ui.screen.social.MediaPicker

@Composable
fun ImagePickers(
    enabled: Boolean,
    onPhotoPicked: (Uri?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        MediaPicker(enabled = enabled, onPhotoPicked = onPhotoPicked)
        CameraImageCapture(enabled = enabled, onCapturedImage = onPhotoPicked)
    }
}