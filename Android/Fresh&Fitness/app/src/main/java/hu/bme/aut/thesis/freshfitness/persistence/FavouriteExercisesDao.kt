package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.bme.aut.thesis.freshfitness.persistence.model.EquipmentEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.FavouriteExerciseEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.MuscleEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.UnitOfMeasureEntity

@Dao
interface FavouriteExercisesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUnitOfMeasure(unit: UnitOfMeasureEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertEquipment(equipment: EquipmentEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMuscle(muscle: MuscleEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertExercise(exercise: FavouriteExerciseEntity)

    @Query("SELECT * FROM favourite_exercises")
    fun getFavouriteExercises(): List<FavouriteExerciseEntity>

    @Query("SELECT * FROM units")
    fun getUnitsOfMeasure(): List<UnitOfMeasureEntity>

    @Query("SELECT * FROM equipments")
    fun getEquipments(): List<EquipmentEntity>

    @Query("SELECT * FROM muscles")
    fun getMuscles(): List<MuscleEntity>

    @Delete
    fun deleteExercise(exercise: FavouriteExerciseEntity)
}