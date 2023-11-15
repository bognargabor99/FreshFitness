package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units")
data class UnitOfMeasureEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val type: String,
)