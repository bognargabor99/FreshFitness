package hu.bme.aut.thesis.freshfitness.model.state

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
data class WorkoutPlanState(
    var setCount: Int = 3,
    var difficulty: String = "",
    var muscleGroup: String = "",
    var createWarmup: Boolean = true,
    var equipmentType: String = "",
    var targetDate: String = SimpleDateFormat("yyyy-MM-dd").format(Date())
)