package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val muscleId: Int,
    val difficulty: String,
    val owner: String,
    val sets: Int,
    val equipmentTypes: String,
    val date: String
)