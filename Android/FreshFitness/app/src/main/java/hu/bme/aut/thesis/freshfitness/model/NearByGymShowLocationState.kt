package hu.bme.aut.thesis.freshfitness.model

import com.google.maps.model.PlacesSearchResult

sealed interface NearByGymShowLocationState {
    data class Show(val place: PlacesSearchResult) : NearByGymShowLocationState
    object NotShow : NearByGymShowLocationState
}