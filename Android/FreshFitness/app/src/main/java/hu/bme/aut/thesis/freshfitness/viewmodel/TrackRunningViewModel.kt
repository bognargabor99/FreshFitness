package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
import hu.bme.aut.thesis.freshfitness.decodeJWT
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import hu.bme.aut.thesis.freshfitness.repository.RunningRepository
import hu.bme.aut.thesis.freshfitness.service.PostService
import hu.bme.aut.thesis.freshfitness.service.TrackRunningService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class TrackRunningViewModel(val context: Context) : ViewModel() {
    // Needed for sharing on social feed
    var isLoggedIn by mutableStateOf(false)
    private var userName by mutableStateOf("")

    private val repository: RunningRepository = RunningRepository(FreshFitnessApplication.runningDatabase.runningDao())
    private var postService = PostService()

    // Image upload state
    var showUploadState by mutableStateOf(false)
    var uploadState: Double by mutableStateOf(0.0)
    var runShared by mutableStateOf(false)

    var allRuns: MutableLiveData<List<RunWithCheckpoints>> = MutableLiveData()

    var locationSettingState by mutableStateOf(false)

    fun getSession() {
        AuthService.fetchAuthSession(onSuccess = {
            if (it.isSignedIn) {
                this.isLoggedIn = true
                val session = (it as AWSCognitoAuthSession)
                val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                val jsonObject = JSONObject(jwt)
                this.userName = jsonObject.getString("username")
            } else {
                this.isLoggedIn = false
                this.userName = ""
            }
        }, onError = {
            Log.d("track_running", "Failed to fetch authentication session.")
        })
    }

    fun fetchRuns() {
        viewModelScope.launch {
            allRuns.value = repository.getRunEntities()
        }
    }

    fun deleteRun(runId: Long) {
        viewModelScope.launch {
            repository.delete(runId)
        }.invokeOnCompletion { fetchRuns() }
    }

    fun startLocationTrackingService() {
        this.context.startForegroundService(Intent(this.context, TrackRunningService::class.java))
    }

    fun stopLocationTrackingService() {
        this.context.stopService(Intent(this.context, TrackRunningService::class.java))
    }

    fun checkLocationState() {
        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(LocationSettingsRequest.Builder().setAlwaysShow(true).build())
            .addOnSuccessListener {
                locationSettingState = it.locationSettingsStates?.run { isGpsUsable || isLocationUsable || isNetworkLocationUsable } == true
            }
            .addOnFailureListener {
                locationSettingState = false
            }
    }

    fun shareRun(context: Context, bitMap: Bitmap, additionalText: String) {
        Log.d("track_running", "Sharing run")
        this.showUploadState = true
        postService.shareRun(
            context = context,
            additionalText = additionalText,
            bitMap = bitMap,
            userName = this.userName,
            onFractionCompleted = { this.uploadState = it },
            onSuccess = {
                this.showUploadState = false
                this.runShared = true
                viewModelScope.launch(Dispatchers.IO) {
                    delay(3000)
                    this@TrackRunningViewModel.runShared = false
                }
            }
        )
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackRunningViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}