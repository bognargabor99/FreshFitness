package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muscles")
data class MuscleEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val imageKey: String,
)