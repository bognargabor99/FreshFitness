package hu.bme.aut.thesis.freshfitness.ui.util

data class ExerciseFilterChangers(
    val onNameFilter: (String) -> Unit,
    val clearNameFilter: () -> Unit,
    val onApplyNewFilters: (difficulty: String, muscle: String, equipment: String) -> Unit
)