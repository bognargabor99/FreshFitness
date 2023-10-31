package hu.bme.aut.thesis.freshfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import hu.bme.aut.thesis.freshfitness.amplify.ApiService
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
import hu.bme.aut.thesis.freshfitness.decodeJWT
import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.Exercise
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup
import hu.bme.aut.thesis.freshfitness.model.workout.UnitOfMeasure
import hu.bme.aut.thesis.freshfitness.model.workout.Workout
import hu.bme.aut.thesis.freshfitness.model.workout.WorkoutExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class WorkoutPlanViewModel : ViewModel() {
    // For fetching user's workouts
    var isLoggedIn by mutableStateOf(false)
    var userName by mutableStateOf("")

    val userWorkouts = mutableStateListOf<Workout>()
    val communityWorkouts = mutableStateListOf<Workout>()

    var exercises = mutableListOf<Exercise>()
    var equipments = mutableListOf<Equipment>()
    private var units = mutableListOf<UnitOfMeasure>()
    var muscleGroups = mutableListOf<MuscleGroup>()

    // Determining which kind of screen to show
    var isLoading by mutableStateOf(true)
    var hasDataToShow by mutableStateOf(false)

    var networkAvailable by mutableStateOf(true)
    private var wasNetworkUnavailableBefore by mutableStateOf(false)
    var showBackOnline by mutableStateOf(false)

    fun initScreen() {
        AuthService.fetchAuthSession(onSuccess = {
            if (it.isSignedIn) {
                this.isLoggedIn = true
                val session = (it as AWSCognitoAuthSession)
                val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                val jsonObject = JSONObject(jwt)
                this.userName = jsonObject.getString("username")
            } else {
                this.isLoggedIn = false
                this.userName = ""
            }
            resetScreen()
        }, onError = {
            resetScreen()
        })
    }

    private fun resetScreen() {
        fetchExercises()
        fetchEquipments()
        fetchUnits()
        fetchMuscleGroups()
        this.communityWorkouts.clear()
        this.userWorkouts.clear()
        fetchWorkouts(if (this.isLoggedIn) this.userName else "community") {
            this.communityWorkouts.addAll(it.filter { w -> w.owner == "community" })
            this.userWorkouts.addAll(it.filter { w -> w.owner != "community" })
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
            this.muscleGroups.clear()
            this.muscleGroups = it.toMutableList()
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

    private fun fetchWorkouts(owner: String, onSuccess: (List<Workout>) -> Unit) {
        ApiService.getWorkouts(owner, onSuccess = { workouts ->
            ApiService.getExercisesForWorkout(owner,
                onSuccess = { wExercises ->
                    workouts.forEach {
                        it.exercises.clear()
                        it.exercises.addAll(wExercises.filter { we -> we.workoutId == it.id && !we.isWarmup()})
                        it.warmupExercises.clear()
                        it.warmupExercises.addAll(wExercises.filter { we -> we.workoutId == it.id && we.isWarmup()})
                    }
                    onSuccess(workouts)
                })
        })
    }

    fun createWorkout(workout: Workout, exercises: List<WorkoutExercise>) {
        ApiService.postWorkout(workout,
            onSuccess = { w ->
                if (w.owner == "community")
                    communityWorkouts.add(w)
                else
                    userWorkouts.add(w)
                exercises.forEach { e -> e.workoutId = w.id }
                ApiService.postWorkoutExercises(exercises,
                    onSuccess = {
                        val wOut: Workout = if (w.owner == "community")
                            communityWorkouts.singleOrNull { _w -> _w.id == w.id }!!
                        else
                            userWorkouts.singleOrNull { _w -> _w.id == w.id }!!
                        wOut.exercises.addAll(it.filter { workoutExercise -> !workoutExercise.isWarmup() })
                        wOut.warmupExercises.addAll(it.filter { workoutExercise -> workoutExercise.isWarmup() })
                    })
            })
    }

    private fun updateDataAvailability() {
        val allData = listOf(this.exercises, this.units, this.equipments, this.muscleGroups, communityWorkouts + userWorkouts)
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

        val connectExerciseData: (Exercise) -> Unit = { ex ->
            ex.equipment = this.equipments.firstOrNull { eq -> eq.id == ex.equipmentId }
            ex.alternateEquipment = this.equipments.firstOrNull { eq -> eq.id == ex.alternateEquipmentId }
            ex.unit = this.units.firstOrNull { unitOfMeasure -> unitOfMeasure.id == ex.unitId }
            ex.muscleGroup = this.muscleGroups.firstOrNull { group -> group.id == ex.muscleGroupId }
        }
        this.exercises.forEach { connectExerciseData(it) }

        val connectWorkoutExerciseData: (Workout) -> Unit = {
            it.targetMuscle = this.muscleGroups.singleOrNull { m -> m.id == it.muscleId }
            it.warmupExercises.forEach { we -> we.exercise = this.exercises.singleOrNull { e -> e.id == we.exerciseId } }
            it.exercises.forEach { we -> we.exercise = this.exercises.singleOrNull { e -> e.id == we.exerciseId } }
        }

        this.communityWorkouts.forEach(connectWorkoutExerciseData)
        this.userWorkouts.forEach(connectWorkoutExerciseData)
    }

    fun onNetworkAvailable() {
        Log.d("network_connectivity", "Internet available")
        this.networkAvailable = true
        if (this.wasNetworkUnavailableBefore) {
            this.showBackOnline = true
            viewModelScope.launch(Dispatchers.IO) {
                delay(3000)
                this@WorkoutPlanViewModel.showBackOnline = false
                this@WorkoutPlanViewModel.wasNetworkUnavailableBefore = false
            }
        }
    }

    fun onNetworkUnavailable() {
        Log.d("network_connectivity", "Internet not available")
        this.networkAvailable = false
        this.wasNetworkUnavailableBefore = true
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WorkoutPlanViewModel(/*context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context*/)
            }
        }
    }
}