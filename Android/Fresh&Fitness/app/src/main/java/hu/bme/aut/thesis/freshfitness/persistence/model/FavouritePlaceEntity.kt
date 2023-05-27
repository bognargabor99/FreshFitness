package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.maps.model.Geometry
import com.google.maps.model.LatLng
import com.google.maps.model.PlacesSearchResult

@Entity(tableName = "favourite_places")
data class FavouritePlaceEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val location: String,
    val rating: Float,
    val totalRatings: Int,
    val latitude: Double,
    val longitude: Double
) {
    fun toPlacesSearchResult(): PlacesSearchResult {
        val p = PlacesSearchResult()
        p.placeId = this.id
        p.name = this.name
        p.rating = this.rating
        p.vicinity = this.location
        p.userRatingsTotal = this.totalRatings
        p.geometry = Geometry()
        p.geometry.location = LatLng(this.latitude, this.longitude)
        return p
    }
}