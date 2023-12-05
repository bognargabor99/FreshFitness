package hu.bme.aut.thesis.freshfitness.amplify

import android.util.Log
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageRemoveOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import java.io.File

object StorageService {
    /**
     * Upload
     */
    fun uploadFile(key: String, file: File, onFractionCompleted: (Double) -> Unit, onSuccess: (String) -> Unit, onError: () -> Unit = { }) {
        val options = StorageUploadFileOptions.defaultInstance()
        Amplify.Storage.uploadFile(key, file, options,
            {
                Log.i("uploadFile", "Fraction completed: ${it.fractionCompleted}")
                onFractionCompleted(it.fractionCompleted)
            },
            {
                Log.i("uploadFile", "Successfully uploaded: ${it.key}")
                onSuccess(it.key)
            },
            {
                onError()
                Log.e("uploadFile", "Upload failed", it)
            }
        )
    }

    /**
     * Delete
     */
    fun deleteFile(key: String) {
        val options = StorageRemoveOptions
            .builder()
            .accessLevel(StorageAccessLevel.PUBLIC)
            .build()

        Amplify.Storage.remove(key, options,
            {
                Log.i("file_deletion", "Successfully removed: ${it.key}")
            },
            {
                Log.e("file_deletion", "Remove failure", it)
            })
    }
}