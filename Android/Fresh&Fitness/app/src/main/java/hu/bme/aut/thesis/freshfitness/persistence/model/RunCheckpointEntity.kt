package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "checkpoints",
    foreignKeys = [
        ForeignKey(
            entity = RunEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("runId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RunCheckpointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var runId: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val height: Double,
    val timestamp: Long
)