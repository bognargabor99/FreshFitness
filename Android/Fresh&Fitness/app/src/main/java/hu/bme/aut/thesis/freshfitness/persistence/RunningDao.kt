package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints

@Dao
interface RunningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewRun(run: RunEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCheckpointsForRun(checkpoints: List<RunCheckpointEntity>)

    @Transaction
    @Query("SELECT * FROM runs")
    fun getRuns(): List<RunWithCheckpoints>

    @Transaction
    @Query("SELECT * FROM runs WHERE id = :runId")
    fun getCheckpointsForRun(runId: Long): RunWithCheckpoints

    @Query("DELETE FROM runs")
    fun deleteAllRuns()

    @Query("DELETE FROM runs WHERE id = :runId")
    fun deleteRun(runId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewFavouritePlace(place: FavouritePlaceEntity): Long

    @Query("SELECT * FROM favourite_places")
    fun getFavouritePlaces(): List<FavouritePlaceEntity>

    @Query("DELETE FROM favourite_places WHERE id = :placeId")
    fun deletePlace(placeId: String)
}