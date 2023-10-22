package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.bme.aut.thesis.freshfitness.persistence.model.EquipmentEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouriteExerciseEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouritePlaceEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.MuscleEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.UnitOfMeasureEntity

@Database(
    entities = [
        RunEntity::class,
        RunCheckpointEntity::class,
        FavouritePlaceEntity::class,
        FavouriteExerciseEntity::class,
        MuscleEntity::class,
        UnitOfMeasureEntity::class,
        EquipmentEntity::class
               ],
    version = 2)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun runningDao(): RunningDao
    abstract fun exercisesDao(): FavouriteExercisesDao
}