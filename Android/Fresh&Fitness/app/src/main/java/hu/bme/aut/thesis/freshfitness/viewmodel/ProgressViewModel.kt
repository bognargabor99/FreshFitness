package hu.bme.aut.thesis.freshfitness.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.UnitOfMeasure
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import hu.bme.aut.thesis.freshfitness.repository.FavouriteExercisesRepository
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {
    private var workoutsRepository = FavouriteExercisesRepository(FreshFitnessApplication.runningDatabase.exercisesDao())

    private var savedExercises = mutableStateListOf<Exercise>()
    private var savedUnits = mutableListOf<UnitOfMeasure>()
    private var savedMuscles = mutableListOf<MuscleGroup>()
    private var savedEquipments = mutableListOf<Equipment>()

    private var savedWorkouts = mutableListOf<Workout>()

    var dataFetched by mutableStateOf(false)

    fun initScreen() {
        getFavouriteExercises()
    }

    private fun getFavouriteExercises() {
        viewModelScope.launch {
            val units = workoutsRepository.getAllUnits()
            savedUnits.clear()
            savedUnits.addAll(units)
            val muscles = workoutsRepository.getAllMuscles()
            savedMuscles.clear()
            savedMuscles.addAll(muscles)
            val equipments = workoutsRepository.getAllEquipments()
            savedEquipments.clear()
            savedEquipments.addAll(equipments)
            val exercises = workoutsRepository.getAllExercises()
            savedExercises.clear()
            savedExercises.addAll(exercises)
//            val workouts = workoutsRepository.getAllWorkouts()
//            savedWorkouts.clear()
//            savedWorkouts.addAll(workouts)
        }.invokeOnCompletion {
            connectData()
            dataFetched = true
        }
    }

    private fun connectData() {
        val connectData: (Exercise) -> Unit = { ex ->
            ex.equipment = this.savedEquipments.firstOrNull { eq -> eq.id == ex.equipmentId }
            ex.alternateEquipment = this.savedEquipments.firstOrNull { eq -> eq.id == ex.alternateEquipmentId }
            ex.unit = this.savedUnits.firstOrNull { u -> u.id == ex.unitId }
            ex.muscleGroup = this.savedMuscles.firstOrNull { m -> m.id == ex.muscleGroupId }
        }

        this.savedExercises.forEach { connectData(it) }

        val addExercise: (WorkoutExercise) -> Unit = { we -> we.exercise = this.savedExercises.single { it.id == we.exerciseId } }

        savedWorkouts.forEach { w ->
            w.warmupExercises.forEach(addExercise)
            w.exercises.forEach(addExercise)
        }
    }
}