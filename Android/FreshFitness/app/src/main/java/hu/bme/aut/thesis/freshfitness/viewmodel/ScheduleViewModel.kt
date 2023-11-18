package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.UnitOfMeasure
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.repository.WorkoutsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {
    private var workoutsRepository = WorkoutsRepository(FreshFitnessApplication.runningDatabase.workoutsDao())

    var exercises = mutableListOf<Exercise>()
    private var units = mutableListOf<UnitOfMeasure>()
    private var muscles = mutableListOf<MuscleGroup>()
    private var equipments = mutableListOf<Equipment>()

    var savedWorkouts = mutableListOf<Workout>()

    var savedWorkoutsFetched by mutableStateOf(false)
    var hasDataToShow by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    // network availability
    var networkAvailable by mutableStateOf(true)
    private var wasNetworkUnavailableBefore by mutableStateOf(false)
    var showBackOnline by mutableStateOf(false)

    fun initScreen() {
        getSavedWorkouts()
        fetchMuscleGroups()
        fetchExercises()
        fetchEquipments()
        fetchUnits()
    }

    fun getSavedWorkouts() {
        savedWorkoutsFetched = false
        viewModelScope.launch {
            savedWorkouts.clear()
            savedWorkouts.addAll(workoutsRepository.getWorkouts())
            savedWorkoutsFetched = true
            updateDataAvailability()
        }
    }

    private fun fetchExercises() {
        ApiService.getExercises({
            this.exercises.clear()
            this.exercises = it.toMutableList()
            updateDataAvailability()
        })
    }

    private fun fetchMuscleGroups() {
        ApiService.getMuscleGroups({
            this.muscles.clear()
            this.muscles = it.toMutableList()
            updateDataAvailability()
        })
    }

    private fun fetchEquipments() {
        ApiService.getEquipments({
            this.equipments.clear()
            this.equipments = it.toMutableList()
            updateDataAvailability()
        })
    }

    private fun fetchUnits() {
        ApiService.getUnits({
            this.units.clear()
            this.units = it.toMutableList()
            updateDataAvailability()
        })
    }

    fun deleteSavedWorkout(workout: Workout, context: Context) {
        val eventId = workout.calendarEventId
        if (eventId != -1) {
            try {
                Log.d("fresh_fitness_workout_delete", "Deleting calendar event with id $eventId")
                context.contentResolver.delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID+"=${eventId}", null)
                Log.d("fresh_fitness_workout_delete", "Deleted calendar event with id $eventId")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModelScope.launch {
            workoutsRepository.deleteWorkout(workout)
        }.invokeOnCompletion {
            getSavedWorkouts()
        }
    }

    private fun updateDataAvailability() {
        if (this.muscles.isNotEmpty() && savedWorkoutsFetched) {
            connectWorkoutMuscles()
            savedWorkoutsFetched = true
        }

        val allData = listOf(this.exercises, this.units, this.equipments, this.muscles)
        val availableData = savedWorkoutsFetched && allData.all { it.isNotEmpty() }
        this.hasDataToShow =
            if (availableData) {
                connectExercises()
                isLoading = false
                true
            } else false
    }

    private fun connectWorkoutMuscles() {
        val connectMuscleData: (Workout) -> Unit = {
            it.targetMuscle = this.muscles.singleOrNull { m -> m.id == it.muscleId }
        }
        this.savedWorkouts.forEach(connectMuscleData)
    }

    private fun connectExercises() {
        this.exercises.sortBy { it.name }

        val connectExerciseData: (Exercise) -> Unit = { ex ->
            ex.equipment = this.equipments.firstOrNull { eq -> eq.id == ex.equipmentId }
            ex.alternateEquipment = this.equipments.firstOrNull { eq -> eq.id == ex.alternateEquipmentId }
            ex.unit = this.units.firstOrNull { unitOfMeasure -> unitOfMeasure.id == ex.unitId }
            ex.muscleGroup = this.muscles.firstOrNull { group -> group.id == ex.muscleGroupId }
        }
        this.exercises.forEach { connectExerciseData(it) }

        val connectWorkoutExerciseData: (Workout) -> Unit = {
            it.targetMuscle = this.muscles.singleOrNull { m -> m.id == it.muscleId }
            it.warmupExercises.forEach { we -> we.exercise = this.exercises.singleOrNull { e -> e.id == we.exerciseId } }
            it.exercises.forEach { we -> we.exercise = this.exercises.singleOrNull { e -> e.id == we.exerciseId } }
        }

        this.savedWorkouts.forEach(connectWorkoutExerciseData)
    }

    fun onNetworkAvailable() {
        Log.d("network_connectivity", "Internet available")
        this.networkAvailable = true
        if (this.wasNetworkUnavailableBefore) {
            this.showBackOnline = true
            viewModelScope.launch(Dispatchers.IO) {
                delay(3000)
                this@ScheduleViewModel.showBackOnline = false
                this@ScheduleViewModel.wasNetworkUnavailableBefore = false
            }
        }
    }

    fun onNetworkUnavailable() {
        Log.d("network_connectivity", "Internet not available")
        this.networkAvailable = false
        this.wasNetworkUnavailableBefore = true
    }
}