package hu.bme.aut.thesis.freshfitness.ui.util.media

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun S3Image(
    modifier: Modifier = Modifier,
    imageUri: String,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null
) {
    val model = ImageRequest.Builder(LocalContext.current)
        .data(imageUri)
        .size(Size.ORIGINAL)
        .crossfade(true)
        .build()
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val painter = rememberAsyncImagePainter(model, imageLoader = imageLoader)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        colorFilter = colorFilter
    )
}