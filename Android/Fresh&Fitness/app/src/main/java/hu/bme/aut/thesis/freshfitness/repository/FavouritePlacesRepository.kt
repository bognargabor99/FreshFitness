package hu.bme.aut.thesis.freshfitness.repository

import com.google.maps.model.PlacesSearchResult
import hu.bme.aut.thesis.freshfitness.persistence.FreshFitnessDao
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavouritePlacesRepository(private val dao: FreshFitnessDao) {

    suspend fun getPlaces() =
        withContext(Dispatchers.IO) {
            dao.getFavouritePlaces()
        }


    suspend fun savePlace(place: PlacesSearchResult) {
        withContext(Dispatchers.IO) {
            dao.insertNewFavouritePlace(
                FavouritePlaceEntity(
                    id = place.placeId,
                    name = place.name,
                    location = place.vicinity,
                    latitude = place.geometry.location.lat,
                    longitude = place.geometry.location.lng,
                    rating = place.rating,
                    totalRatings = place.userRatingsTotal
                )
            )
        }
    }

    suspend fun deletePlace(placeId: String) {
        withContext(Dispatchers.IO) {
            dao.deletePlace(placeId)
        }
    }
}


