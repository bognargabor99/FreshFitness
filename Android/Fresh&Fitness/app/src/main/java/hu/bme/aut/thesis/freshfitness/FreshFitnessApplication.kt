package hu.bme.aut.thesis.freshfitness

import android.app.Application
import androidx.room.Room
import hu.bme.aut.thesis.freshfitness.persistence.RunningDatabase

class FreshFitnessApplication : Application() {

    companion object {
        lateinit var runningDatabase: RunningDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        runningDatabase = Room.databaseBuilder(
            applicationContext,
            RunningDatabase::class.java,
            "runningDatabase"
        ).fallbackToDestructiveMigration().build()
    }
}