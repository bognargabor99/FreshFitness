package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunEntity

@Database(entities = [RunEntity::class, RunCheckpointEntity::class, FavouritePlaceEntity::class], version = 1)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun freshFitnessDao(): FreshFitnessDao
}