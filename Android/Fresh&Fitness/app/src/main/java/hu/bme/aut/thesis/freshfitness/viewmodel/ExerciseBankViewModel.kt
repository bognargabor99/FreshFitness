package hu.bme.aut.thesis.freshfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.UnitOfMeasure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseBankViewModel : ViewModel() {
    var exercises = mutableListOf<Exercise>()
    var equipments = mutableListOf<Equipment>()
    private var units = mutableListOf<UnitOfMeasure>()
    var muscleGroups = mutableListOf<MuscleGroup>()

    var filteredExercises = mutableStateListOf<Exercise>()
    var nameFilter: String by mutableStateOf("")
    var muscleFilter: String by mutableStateOf("")
    var equipmentFilter: String by mutableStateOf("")

    // Determining which kind of screen to show
    var isLoading by mutableStateOf(true)
    var hasDataToShow by mutableStateOf(false)

    // network availability
    var networkAvailable by mutableStateOf(true)
    private var wasNetworkUnavailableBefore by mutableStateOf(false)
    var showBackOnline by mutableStateOf(false)

    fun fetchData() {
        fetchExercises()
        fetchEquipments()
        fetchMuscleGroups()
        fetchUnits()
    }

    private fun applyFilters() {
        val tempFilteredExercises = exercises.filter {
            it.name.lowercase().contains(this.nameFilter.lowercase()) &&
            (
                    (if (it.equipment != null) it.equipment!!.name.lowercase().contains(this.equipmentFilter.lowercase()) else false) ||
                    (if (it.alternateEquipment != null) it.alternateEquipment!!.name.lowercase().contains(this.equipmentFilter.lowercase()) else false)
            ) &&
            if (it.muscleGroup != null) it.muscleGroup!!.name.lowercase().contains(this.muscleFilter.lowercase()) else false

        }
        filteredExercises.clear()
        filteredExercises.addAll(tempFilteredExercises)
    }

    fun saveNameFilter(newNameFilter: String) {
        this.nameFilter = newNameFilter
        applyFilters()
    }

    fun saveOtherFilters(newMuscleFilter: String, newEquipmentFilter: String) {
        this.muscleFilter = newMuscleFilter
        this.equipmentFilter = newEquipmentFilter
        applyFilters()
    }

    fun clearNameFilter() {
        this.nameFilter = ""
        applyFilters()
    }

    private fun fetchExercises() {
        ApiService.getExercises({
            this.exercises.clear()
            this.exercises = it.toMutableList()
            updateExerciseBankAvailability()
        })
    }

    private fun fetchMuscleGroups() {
        ApiService.getMuscleGroups({
            this.muscleGroups.clear()
            this.muscleGroups = it.toMutableList()
            updateExerciseBankAvailability()
        })
    }

    private fun fetchEquipments() {
        ApiService.getEquipments({
            this.equipments.clear()
            this.equipments = it.toMutableList()
            updateExerciseBankAvailability()
        })
    }

    private fun fetchUnits() {
        ApiService.getUnits({
            this.units.clear()
            this.units = it.toMutableList()
            updateExerciseBankAvailability()
        })
    }

    private fun updateExerciseBankAvailability() {
        val allData = listOf(this.exercises, this.units, this.equipments, this.muscleGroups)
        val availableData = allData.all { it.isNotEmpty() }
        this.hasDataToShow =
            if (availableData) {
                connectExercises()
                isLoading = false
                true
            } else false
    }

    private fun connectExercises() {
        this.exercises.sortBy { it.name }
        this.exercises.forEach { ex ->
            ex.equipment = this.equipments.firstOrNull { eq -> eq.id == ex.equipmentId }
            ex.alternateEquipment = this.equipments.firstOrNull { eq -> eq.id == ex.alternateEquipmentId }
            ex.unit = this.units.firstOrNull { eq -> eq.id == ex.unitId }
            ex.muscleGroup = this.muscleGroups.firstOrNull { eq -> eq.id == ex.muscleGroupId }
        }
        applyFilters()
    }

    fun onNetworkAvailable() {
        Log.d("network_connectivity", "Internet available")
        this.networkAvailable = true
        if (this.wasNetworkUnavailableBefore) {
            this.showBackOnline = true
            viewModelScope.launch(Dispatchers.IO) {
                delay(3000)
                this@ExerciseBankViewModel.showBackOnline = false
                this@ExerciseBankViewModel.wasNetworkUnavailableBefore = false
            }
        }
    }

    fun onNetworkUnavailable() {
        Log.d("network_connectivity", "Internet not available")
        this.networkAvailable = false
        this.wasNetworkUnavailableBefore = true
    }
}