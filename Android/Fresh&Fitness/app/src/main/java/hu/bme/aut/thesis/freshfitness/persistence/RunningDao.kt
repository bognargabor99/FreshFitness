package hu.bme.aut.thesis.freshfitness.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints

@Dao
interface RunningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewRun(run: RunEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewRunCheckpoint(checkpoint: RunCheckpointEntity): Long

    @Query("SELECT * FROM runs")
    suspend fun getRuns(): List<RunEntity>

    @Transaction
    @Query("SELECT * FROM runs WHERE id = :runId")
    suspend fun getCheckpointsForRun(runId: Long): RunWithCheckpoints

    @Query("DELETE FROM runs")
    suspend fun deleteAllRuns()

    @Query("DELETE FROM runs WHERE id = :runId")
    suspend fun deleteRun(runId: Long)
}