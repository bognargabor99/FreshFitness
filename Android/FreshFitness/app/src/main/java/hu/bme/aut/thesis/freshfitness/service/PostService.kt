package hu.bme.aut.thesis.freshfitness.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.amplify.StorageService
import hu.bme.aut.thesis.freshfitness.model.social.CreatePostDto
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

class PostService {
    fun uploadFile(
        userName: String,
        file: File,
        extension: String,
        onFractionCompleted: (Double) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        val randomUuid = UUID.randomUUID()
        StorageService.uploadFile(
            key = "images/${userName}/$randomUuid.$extension",
            file = file,
            onFractionCompleted = onFractionCompleted,
            onSuccess = onSuccess
        )
    }

    fun shareRun(
        context: Context,
        additionalText: String,
        bitMap: Bitmap,
        userName: String,
        onFractionCompleted: (Double) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        Log.d("track_running", "${bitMap.width} * ${bitMap.height}")
        convertBitMapToFile(context = context, bitMap = bitMap)
        val file = File(context.filesDir, fileName)

        this.uploadFile(
            userName = userName,
            file = file,
            extension = "png",
            onFractionCompleted = onFractionCompleted,
            onSuccess = { location ->
                val createPostDto = CreatePostDto(
                    details = "I completed another day of running ${if (additionalText.isNotBlank()) "\n$additionalText" else ""}",
                    username = userName,
                    imageLocation = location
                )
                ApiService.createPost(createPostDto) {
                    onSuccess(location)
                    file.delete()
                }
            }
        )
    }

    private fun convertBitMapToFile(context: Context, bitMap: Bitmap) {
        val file = File(context.filesDir, fileName)
        val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
        bitMap.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.close()
    }

    companion object {
        private const val fileName = "run.png"
    }
}