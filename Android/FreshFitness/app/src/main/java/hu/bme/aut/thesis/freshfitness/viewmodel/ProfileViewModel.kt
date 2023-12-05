package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
import hu.bme.aut.thesis.freshfitness.amplify.StorageService
import hu.bme.aut.thesis.freshfitness.model.social.SetProfileImageDto
import hu.bme.aut.thesis.freshfitness.util.decodeJWT
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection
import java.util.UUID

class ProfileViewModel : ViewModel() {
    var isAdmin by mutableStateOf(false)
        private set

    var username by mutableStateOf("")

    var profileImageLocation by mutableStateOf("")
    var showUpdateProfileDialog by mutableStateOf(false)
    var updateProfileEnabled by mutableStateOf(true)

    var showImageOptionsDialog by mutableStateOf(false)

    // Show uploaded percentage of file
    var showUploadState by mutableStateOf(false)
    var uploadState: Double by mutableStateOf(0.0)
    var uploadText by mutableStateOf("")

    // Show fullscreen of image
    var showImageFullScreen by mutableStateOf(false)

    fun fetchAuthSession() {
        AuthService.fetchAuthSession(
            {
                this.isAdmin = false
                val session = (it as AWSCognitoAuthSession)
                if (session.accessToken != null) {
                    val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                    this.isAdmin = false
                    val jsonObject = JSONObject(jwt)
                    if (jsonObject.has("cognito:groups")) {
                        val groups = jsonObject.getJSONArray("cognito:groups")
                        for (i in 0 until groups.length()) {
                            if (groups.getString(i) == "Admins") {
                                this.isAdmin = true
                            }
                        }
                    }
                    if (jsonObject.has("username")) {
                        this.username = jsonObject.getString("username")
                        getProfileImage()
                    }
                    Log.i("fresh_fitness_profile", "Auth session = $it")
                }
            },
            {
                this.isAdmin = false
                this.username = ""
                Log.e("fresh_fitness_profile", "Failed to fetch auth session")
            }
        )
    }

    private fun getProfileImage() {
        this.profileImageLocation = ""
        ApiService.getProfileImageForUser(
            name = this.username,
            onSuccess = { imageLocation ->
                this.profileImageLocation = imageLocation
            }
        )
    }

    fun setProfileImage(contentUri: Uri?, context: Context) {
        Log.d("update_profile", "Uri: ${contentUri?.path}")
        val oldProfile = this.profileImageLocation
        this.updateProfileEnabled = false
        this.showUploadState = true
        this.uploadText = "Processing file..."
        var mimeType: String? = null
        var buffer: ByteArrayOutputStream? = null
        if (contentUri != null) {
            val file = context.contentResolver.openInputStream(contentUri)
            buffer = ByteArrayOutputStream()
            var nRead: Int
            val data = ByteArray(16384)
            while (file!!.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            file.close()

            try {
                val bufferedStream = BufferedInputStream(context.contentResolver.openInputStream(contentUri))
                mimeType = URLConnection.guessContentTypeFromStream(bufferedStream)
                Log.i("mime_type_detection", "MimeType detected: $mimeType")
            } catch (e: Exception) {
                Log.e("mime_type_detection", "MimeType could not be determined", e)
                mimeType = null
            }
        }

        if (buffer != null && !mimeType.isNullOrBlank() && mimeType.startsWith("image/")) {
            val randomUuid = UUID.randomUUID()
            val extension = mimeType.substring(6)
            this.uploadText = "Uploading file..."
            val f = File(context.filesDir, "tempFile.$extension")
            f.writeBytes(buffer.toByteArray())

            StorageService.uploadFile(
                key = "images/$username/profile_$randomUuid.$extension",
                file = f,
                onFractionCompleted = { this.uploadState = it },
                onSuccess = { location ->
                    ApiService.setProfileImageForUser(
                        SetProfileImageDto(this.username, location),
                        onSuccess = {
                            if (oldProfile.isNotBlank())
                                StorageService.deleteFile(oldProfile.substring(7))
                            this.profileImageLocation = it
                            f.delete()
                            this.showUpdateProfileDialog = false
                            this.showUploadState = false
                            this.uploadText = ""
                            this.uploadState = 0.0
                            this.updateProfileEnabled = true
                        }
                    )
                }
            )
        }
    }

    private fun deleteProfileImage() {
        this.showImageOptionsDialog = false
        ApiService.setProfileImageForUser(
            setProfileImageDto = SetProfileImageDto(this.username, ""),
            onSuccess = {
                StorageService.deleteFile(this.profileImageLocation.substring(7))
                this.profileImageLocation = ""
                this.showUpdateProfileDialog = false
                this.uploadText = ""
                this.showUploadState = false
                this.updateProfileEnabled = true
            }
        )
    }

    private fun showUpdateProfileImageDialog() {
        this.showImageOptionsDialog = false
        this.showUpdateProfileDialog = true
    }

    fun dismissUpdateProfileImageDialog() {
        this.showUpdateProfileDialog = false
        this.showImageOptionsDialog = false
    }

    fun showImageOptions() {
        this.showImageOptionsDialog = true
    }

    fun dismissImageOptions() {
        this.showImageOptionsDialog = false
    }

    private fun showFullScreenImage() {
        showImageOptionsDialog = false
        showImageFullScreen = true
    }

    fun hideFullScreenImage() {
        showImageFullScreen = false
    }

    fun signOut() {
        AuthService.signOut()
    }

    fun getOptionsMap(): Map<String, () -> Unit> =
        mutableMapOf<String, () -> Unit>().apply {
            if (this@ProfileViewModel.profileImageLocation.isNotBlank())
                put("View", ::showFullScreenImage)
            put("Update", ::showUpdateProfileImageDialog)
            if (this@ProfileViewModel.profileImageLocation.isNotBlank())
                put("Delete", ::deleteProfileImage)
        }
}