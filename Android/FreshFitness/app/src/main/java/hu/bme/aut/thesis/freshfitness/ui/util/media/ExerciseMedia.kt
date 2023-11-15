package hu.bme.aut.thesis.freshfitness.ui.util.media

import android.view.LayoutInflater
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun ExerciseMedia(mediaLink: String) {
    if (mediaLink.startsWith("youtube/")) {
        YoutubeVideo(videoId = mediaLink.split("/").last())
    }
    else if (mediaLink.endsWith(".mp4")) {
        VideoPlayer(videoUri = "${BuildConfig.S3_IMAGES_BASE_URL}${mediaLink}")
    }
    else if (mediaLink.isNotEmpty()) {
        ExerciseImage(imageUri = "${BuildConfig.S3_IMAGES_BASE_URL}${mediaLink}")
    }
}

@Composable
fun VideoPlayer(videoUri: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    AndroidView(factory = {
        PlayerView(it).apply {
            player = exoPlayer
            this.useController = false
            this.player?.repeatMode = Player.REPEAT_MODE_ONE
            this.player?.volume = 0f
            this.player?.setPlaybackSpeed(0.75f)
            this.player?.play()
        } },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
    )
}

@Composable
fun YoutubeVideo(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            /// GitHub issue: https://github.com/PierfrancescoSoffritti/android-youtube-player/issues/1024
            val inflatedView = LayoutInflater.from(context).inflate(R.layout.youtube_player_xml, null, false)
            val ytPlayer: YouTubePlayerView = inflatedView as YouTubePlayerView
            ytPlayer.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val controller = DefaultPlayerUiController(ytPlayer, youTubePlayer)
                    controller.run {
                        showPlayPauseButton(true)
                        showYouTubeButton(false)
                        showSeekBar(false)
                        showCurrentTime(false)
                        showFullscreenButton(false)
                        showVideoTitle(false)
                        enableLiveVideoUi(false)
                    }
                    ytPlayer.setCustomPlayerUi(controller.rootView)
                    youTubePlayer.cueVideo(videoId, 0f)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    if (state == PlayerConstants.PlayerState.ENDED)
                        youTubePlayer.seekTo(0f)
                }
            }, handleNetworkEvents = false, IFramePlayerOptions.Builder()
                .controls(0)
                .mute(1)
                .build())
            ytPlayer
        },
        onRelease = {
            lifecycleOwner.lifecycle.removeObserver(it)
            it.release()
        }
    )
}

@Composable
fun ExerciseImage(imageUri: String) {
    S3Image(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
        imageUri = imageUri
    )
}