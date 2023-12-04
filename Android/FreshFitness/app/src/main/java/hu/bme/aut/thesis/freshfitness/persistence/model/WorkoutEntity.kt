package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val awsId: Int,
    val muscleId: Int,
    val difficulty: String,
    val owner: String,
    val sets: Int,
    val equipmentTypes: String,
    val date: String,
    val savedToDate: String,
    val calendarEventId: Int
)