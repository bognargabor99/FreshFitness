package hu.bme.aut.thesis.freshfitness.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import hu.bme.aut.thesis.freshfitness.model.NearByGymShowLocationState
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity
import hu.bme.aut.thesis.freshfitness.repository.FavouritePlacesRepository
import kotlinx.coroutines.launch

class NearbyGymsViewModel(val context: Context) : ViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    var currentLocation by mutableStateOf(com.google.android.gms.maps.model.LatLng(47.0, 19.0))
    private val repository = FavouritePlacesRepository(FreshFitnessApplication.runningDatabase.runningDao())

    var showLocationState: NearByGymShowLocationState by mutableStateOf(NearByGymShowLocationState.NotShow)
        private set

    var shownLocation: com.google.android.gms.maps.model.LatLng by mutableStateOf(com.google.android.gms.maps.model.LatLng(47.0, 19.0))
        private set

    var showSavedList by mutableStateOf(false)
        private set

    var gyms by mutableStateOf(listOf<PlacesSearchResult>())
    var favouritePlaces by mutableStateOf(listOf<FavouritePlaceEntity>())

    var locationEnabled by mutableStateOf(LocationEnabledState.UNKNOWN)

    var radius by mutableStateOf(2500)
        private set

    init {
        getPlaces()
    }

    private fun findNearbyGyms() {
        val response: PlacesSearchResponse
        val geoContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()

        try {
            response = PlacesApi.nearbySearchQuery(geoContext, LatLng(currentLocation.latitude, currentLocation.longitude))
                .radius(this.radius)
                .type(PlaceType.GYM)
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
                this.currentLocation = com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude)
                findNearbyGyms()
            }
    }

    fun startLocationFlow() {
        locationEnabled = LocationEnabledState.UNKNOWN
        showSavedList = false
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

    fun showPlaceOnMap(place: PlacesSearchResult) {
        Log.d("fitness_places", "Showing map")
        showLocationState = NearByGymShowLocationState.Show(
            place = com.google.android.gms.maps.model.LatLng(place.geometry.location.lat, place.geometry.location.lng)
        )
        shownLocation = (showLocationState as NearByGymShowLocationState.Show).place
    }

    fun hideMap() {
        Log.d("fitness_places", "Hiding map")
        showLocationState = NearByGymShowLocationState.NotShow
    }

    fun setShowSavedList() {
        showSavedList = !showSavedList
        if (!showSavedList)
            startLocationFlow()
        else
            gyms = favouritePlaces.map { it.toPlacesSearchResult() }
    }

    private fun getPlaces() {
        viewModelScope.launch {
            favouritePlaces = repository.getPlaces()
            Log.d("fitness_places", favouritePlaces.joinToString { it.name })
        }
    }

    fun savePlace(place: PlacesSearchResult) {
        viewModelScope.launch {
            if (!favouritePlaces.any { it.id == place.placeId }) {
                Log.d("fitness_places", "Saving new place: ${place.name}")
                repository.savePlace(place)
            }
            else {
                Log.d("fitness_places", "Deleting place: ${place.name}")
                repository.deletePlace(place.placeId)
            }
        }.invokeOnCompletion {
            getPlaces()
        }

    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NearbyGymsViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}