package hu.bme.aut.thesis.freshfitness.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.LatLng
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.model.LocationEnabledState
import hu.bme.aut.thesis.freshfitness.repository.FavouritePlacesRepository

class NearbyGymsViewModel(val context: Context) : ViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var currentLocation = LatLng(47.0, 19.0)
    private val repository = FavouritePlacesRepository(FreshFitnessApplication.runningDatabase.freshFitnessDao())

    var gyms by mutableStateOf(listOf<PlacesSearchResult>())

    var locationEnabled by mutableStateOf(LocationEnabledState.UNKNOWN)

    var savingPlaceState by mutableStateOf(false)

    var radius by mutableStateOf(2500)
        private set

    private fun findNearbyGyms() {
        val response: PlacesSearchResponse
        val geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()

        try {
            response = PlacesApi.nearbySearchQuery(geoContext, currentLocation)
                .radius(this.radius)
                .type(PlaceType.GYM)
                .language("en")
                .await()
            locationEnabled = LocationEnabledState.ENABLED_SEARCHING_FINISHED
            gyms = response.results.toList()
        } catch (_: Exception) {

        }
    }

    private fun checkLocationSettings(onLocationEnabled: () -> Unit, onFailure: () -> Unit) {
        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(LocationSettingsRequest.Builder().setAlwaysShow(true).build())
            .addOnSuccessListener {
                if (it.locationSettingsStates?.isGpsUsable  == true ||
                    it.locationSettingsStates?.isLocationUsable  == true ||
                    it.locationSettingsStates?.isNetworkLocationUsable == true)
                onLocationEnabled()
                else
                    onFailure()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    @SuppressLint("MissingPermission")
    fun queryLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener {
                this.currentLocation = LatLng(it.latitude, it.longitude)
                findNearbyGyms()
            }
    }

    fun startLocationFlow() {
        locationEnabled = LocationEnabledState.UNKNOWN
        checkLocationSettings(
            onLocationEnabled = {
                locationEnabled = LocationEnabledState.ENABLED_SEARCHING
                queryLocation()
            },
            onFailure = {
                locationEnabled = LocationEnabledState.DISABLED
            }
        )
    }

    fun changeRadius(newRadius: Int) {
        this.radius = newRadius
    }

    fun savePlace(place: PlacesSearchResult) {
        repository.savePlace(place)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NearbyGymsViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}