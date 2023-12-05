package hu.bme.aut.thesis.freshfitness.util

import hu.bme.aut.thesis.freshfitness.model.workout.Equipment
import hu.bme.aut.thesis.freshfitness.model.workout.MuscleGroup

data class MusclesAndEquipments(
    var muscles: MutableList<MuscleGroup>,
    var equipments: MutableList<Equipment>
)