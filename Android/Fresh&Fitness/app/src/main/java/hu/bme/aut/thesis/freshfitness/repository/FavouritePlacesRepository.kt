package hu.bme.aut.thesis.freshfitness.repository

import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.persistence.FreshFitnessDao
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity

class FavouritePlacesRepository(private val dao: FreshFitnessDao) {
    fun savePlace(place: PlacesSearchResult) {
        dao.insertNewFavouritePlace(
            FavouritePlaceEntity(
                name = place.name,
                location = place.vicinity
            )
        )
    }
}