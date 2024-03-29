package hu.bme.aut.thesis.freshfitness.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import hu.bme.aut.thesis.freshfitness.repository.FavouriteExercisesRepository
import hu.bme.aut.thesis.freshfitness.ui.util.ExerciseFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseBankViewModel : ViewModel() {
    private var exercisesRepository = FavouriteExercisesRepository(FreshFitnessApplication.runningDatabase.exercisesDao())

    var exercises = mutableListOf<Exercise>()
    var equipments = mutableListOf<Equipment>()
    private var units = mutableListOf<UnitOfMeasure>()
    var muscleGroups = mutableListOf<MuscleGroup>()

    private var favouritesFetched by mutableStateOf(false)
    private var savedUnits = mutableListOf<UnitOfMeasure>()
    private var savedMuscles = mutableListOf<MuscleGroup>()
    private var savedEquipments = mutableListOf<Equipment>()

    var favouriteExercises = mutableStateListOf<Exercise>()
    var filteredExercises = mutableStateMapOf<String, List<Exercise>>()
    private var difficulties = mutableStateListOf<String>()

    private var _filters = MutableStateFlow(ExerciseFilters())
    val filters: StateFlow<ExerciseFilters> = _filters.asStateFlow()

    // Determining which kind of screen to show
    var isLoading by mutableStateOf(true)
    var hasDataToShow by mutableStateOf(false)

    fun fetchData() {
        this.getFavouriteExercises()
        this.fetchExercises()
        this.fetchEquipments()
        this.fetchMuscleGroups()
        this.fetchUnits()
    }

    private fun applyFilters() {
        val tempFilteredExercises = exercises.filter {
            it.difficulty.lowercase().contains(this._filters.value.difficulty.lowercase()) &&
            it.name.lowercase().contains(this._filters.value.name.lowercase()) &&
            (
                    (if (it.equipment != null) it.equipment!!.name.lowercase().contains(this._filters.value.equipment.lowercase()) else false) ||
                    (if (it.alternateEquipment != null) it.alternateEquipment!!.name.lowercase().contains(this._filters.value.equipment.lowercase()) else false)
            ) &&
            if (it.muscleGroup != null) it.muscleGroup!!.name.lowercase().contains(this._filters.value.muscle.lowercase()) else false

        }
        filteredExercises.clear()

        tempFilteredExercises.groupBy { it.name[0].uppercase() }.forEach { char, exercises ->
            filteredExercises[char] = exercises
        }
    }

    fun saveNameFilter(newNameFilter: String) {
        this._filters.update { currentFilters ->
            currentFilters.copy(name = newNameFilter)
        }
        applyFilters()
    }

    fun saveOtherFilters(newDifficultyFilter: String, newMuscleFilter: String, newEquipmentFilter: String) {
        this._filters.update { currentFilters ->
            currentFilters.copy(
                difficulty = newDifficultyFilter,
                muscle = newMuscleFilter,
                equipment = newEquipmentFilter
            )
        }

        applyFilters()
    }

    fun clearNameFilter() {
        this._filters.update { currentFilters ->
            currentFilters.copy(name = "")
        }
        applyFilters()
    }

    fun heartExercise(exercise: Exercise) {
        viewModelScope.launch {
            if (!favouriteExercises.any { it.id == exercise.id }) {
                exercise.unit?.let { exercisesRepository.insertUnit(it) }
                exercise.equipment?.let { exercisesRepository.insertEquipment(it) }
                exercise.alternateEquipment?.let { exercisesRepository.insertEquipment(it) }
                exercise.muscleGroup?.let { exercisesRepository.insertMuscle(it) }
                exercisesRepository.insertExercise(exercise)
            } else {
                exercisesRepository.deleteExercise(exercise)
            }
        }.invokeOnCompletion {
            this.getFavouriteExercises()
        }
    }

    private fun getFavouriteExercises() {
        viewModelScope.launch {
            val units = exercisesRepository.getAllUnits()
            savedUnits.clear()
            savedUnits.addAll(units)
            val muscles = exercisesRepository.getAllMuscles()
            savedMuscles.clear()
            savedMuscles.addAll(muscles)
            val equipments = exercisesRepository.getAllEquipments()
            savedEquipments.clear()
            savedEquipments.addAll(equipments)
            val favourites = exercisesRepository.getAllExercises()
            favouriteExercises.clear()
            this@ExerciseBankViewModel.favouriteExercises.addAll(favourites)
        }.invokeOnCompletion {
            favouritesFetched = true
            updateExerciseBankAvailability()
        }
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
        val availableData = allData.all { it.isNotEmpty() } && favouritesFetched
        this.hasDataToShow =
            if (availableData) {
                connectExercises()
                isLoading = false
                true
            } else false
    }

    private fun connectExercises() {
        this.exercises.sortBy { it.name }

        val connectData: (Exercise) -> Unit = { ex ->
            ex.equipment = this.equipments.firstOrNull { eq -> eq.id == ex.equipmentId }
            ex.alternateEquipment = this.equipments.firstOrNull { eq -> eq.id == ex.alternateEquipmentId }
            ex.unit = this.units.firstOrNull { eq -> eq.id == ex.unitId }
            ex.muscleGroup = this.muscleGroups.firstOrNull { eq -> eq.id == ex.muscleGroupId }
        }
        this.exercises.forEach { connectData(it) }
        this.favouriteExercises.forEach { connectData(it) }
        this.difficulties.clear()
        this.difficulties.addAll(this.exercises.map { it.difficulty }.distinct())
        applyFilters()
    }
}