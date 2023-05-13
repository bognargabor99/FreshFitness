package hu.bme.aut.thesis.freshfitness.persistence.model

import androidx.room.Embedded
import androidx.room.Relation

data class RunWithCheckpoints(
    @Embedded val run: RunEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "runId"
    )
    val checkpoints: List<RunCheckpointEntity>
)