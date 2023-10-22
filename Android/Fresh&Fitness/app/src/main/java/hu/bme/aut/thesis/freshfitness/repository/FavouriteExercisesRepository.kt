package hu.bme.aut.thesis.freshfitness.repository

import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.UnitOfMeasure
import hu.bme.aut.thesis.freshfitness.persistence.FavouriteExercisesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavouriteExercisesRepository(private val exercisesDao: FavouriteExercisesDao) {
    suspend fun getAllUnits(): List<UnitOfMeasure> =
        withContext(Dispatchers.IO) {
            return@withContext exercisesDao.getUnitsOfMeasure().map { UnitOfMeasure.fromUnitOfMeasureEntity(it) }
        }

    suspend fun getAllEquipments(): List<Equipment> =
        withContext(Dispatchers.IO) {
            return@withContext exercisesDao.getEquipments().map { Equipment.fromEquipmentEntity(it) }
        }

    suspend fun getAllMuscles(): List<MuscleGroup> =
        withContext(Dispatchers.IO) {
            return@withContext exercisesDao.getMuscles().map { MuscleGroup.fromMuscleEntity(it) }
        }

    suspend fun getAllExercises(): List<Exercise> =
        withContext(Dispatchers.IO) {
            return@withContext exercisesDao.getFavouriteExercises().map { Exercise.fromFavouriteExerciseEntity(it) }
        }

    suspend fun insertUnit(unit: UnitOfMeasure) {
        val entity = unit.toUnitOfMeasureEntity()
        withContext(Dispatchers.IO) {
            val units = exercisesDao.getUnitsOfMeasure()
            if (!units.any { it.id == entity.id })
                exercisesDao.insertUnitOfMeasure(entity)
        }
    }

    suspend fun insertEquipment(equipment: Equipment) {
        val entity = equipment.toEquipmentEntity()
        withContext(Dispatchers.IO) {
            val equipments = exercisesDao.getEquipments()
            if (!equipments.any { it.id == entity.id })
                exercisesDao.insertEquipment(entity)
        }
    }

    suspend fun insertMuscle(muscleGroup: MuscleGroup) {
        val entity = muscleGroup.toMuscleEntity()
        withContext(Dispatchers.IO) {
            val muscles = exercisesDao.getMuscles()
            if (!muscles.any { it.id == entity.id })
                exercisesDao.insertMuscle(entity)
        }
    }

    suspend fun insertExercise(exercise: Exercise) {
        val entity = exercise.toFavouriteExerciseEntity()
        withContext(Dispatchers.IO) {
            exercisesDao.insertExercise(entity)
        }
    }

    suspend fun deleteExercise(exercise: Exercise) {
        withContext(Dispatchers.IO) {
            exercisesDao.deleteExercise(exercise.toFavouriteExerciseEntity())
        }
    }
}