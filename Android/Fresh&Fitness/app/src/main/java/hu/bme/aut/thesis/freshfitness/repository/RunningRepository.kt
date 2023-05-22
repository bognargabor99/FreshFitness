package hu.bme.aut.thesis.freshfitness.repository

import hu.bme.aut.thesis.freshfitness.persistence.FreshFitnessDao
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunEntity
import hu.bme.aut.thesis.freshfitness.persistence.model.RunWithCheckpoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class RunningRepository(private val dao: FreshFitnessDao) {

    fun getRunning(runId: Long): RunWithCheckpoints {
        var ret: RunWithCheckpoints? = null
        val task = thread { ret = dao.getCheckpointsForRun(runId) }
        task.join()
        return ret!!
    }

    fun insertNewRunning(runLocations: List<RunCheckpointEntity>) {
        val run = RunEntity(
            startTime = runLocations.first().timestamp,
            endTime = runLocations.last().timestamp
        )
        val runId = dao.insertNewRun(run)
        runLocations.forEach { checkPoint ->
            checkPoint.runId = runId
        }
        dao.insertCheckpointsForRun(runLocations)
    }

    suspend fun getRunEntities(): List<RunWithCheckpoints> =
        withContext(Dispatchers.IO) {
            dao.getRuns()
        }


    suspend fun delete(runId: Long) = withContext(Dispatchers.IO) {
        dao.deleteRun(runId)
    }


    fun deleteAll() {
        dao.deleteAllRuns()
    }
}