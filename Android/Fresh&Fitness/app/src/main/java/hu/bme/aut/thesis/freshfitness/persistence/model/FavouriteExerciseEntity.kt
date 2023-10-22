package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourite_exercises",
    foreignKeys = [
        ForeignKey(
            entity = UnitOfMeasureEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("unitId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MuscleEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("muscleGroupId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EquipmentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("equipmentId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavouriteExerciseEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val details: String,
    val media: String,
    var muscleGroupId: Int? = null,
    val difficulty: String,
    var unitId: Int? = null,
    var equipmentId: Int? = null,
    var alternateEquipment: Int? = null,
    val intermediateLimit: Int,
    val advancedLimit: Int
)