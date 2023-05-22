package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import hu.bme.aut.thesis.freshfitness.repository.RunningRepository
import hu.bme.aut.thesis.freshfitness.service.TrackRunningService
import kotlinx.coroutines.launch

class TrackRunningViewModel(val context: Context) : ViewModel() {

    private val repository: RunningRepository =
        RunningRepository(FreshFitnessApplication.runningDatabase.freshFitnessDao())

    var allRuns: MutableLiveData<List<RunWithCheckpoints>> = MutableLiveData()

    var locationSettingState by mutableStateOf(false)

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

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackRunningViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}