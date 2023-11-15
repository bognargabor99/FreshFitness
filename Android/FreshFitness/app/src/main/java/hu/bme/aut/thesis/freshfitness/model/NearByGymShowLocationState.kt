package hu.bme.aut.thesis.freshfitness.model

import com.google.android.gms.maps.model.LatLng

sealed interface NearByGymShowLocationState {
    data class Show(val place: LatLng) : NearByGymShowLocationState
    object NotShow : NearByGymShowLocationState
}