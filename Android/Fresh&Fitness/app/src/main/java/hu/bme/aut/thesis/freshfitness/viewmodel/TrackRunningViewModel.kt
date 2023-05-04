package hu.bme.aut.thesis.freshfitness.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import hu.bme.aut.thesis.freshfitness.model.LocationState

class TrackRunningViewModel(val context: Context) : ViewModel() {
    private var locationCallback: LocationCallback? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var currentLocation by mutableStateOf(LocationState())
    var isTracking by mutableStateOf(false)

    init {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (lo in p0.locations) {
                    // Update UI with location data
                    currentLocation = LocationState(lo.latitude, lo.longitude)
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .build()

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
            isTracking = true
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
        isTracking = false
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackRunningViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}