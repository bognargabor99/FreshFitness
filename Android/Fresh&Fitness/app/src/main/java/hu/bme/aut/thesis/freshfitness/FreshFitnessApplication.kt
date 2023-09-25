package hu.bme.aut.thesis.freshfitness

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import hu.bme.aut.thesis.freshfitness.persistence.RunningDatabase

class FreshFitnessApplication : Application() {

    companion object {
        lateinit var runningDatabase: RunningDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("amplify_init_config", "Amplify was successfully configured")
        } catch (e: AmplifyException) {
            Log.e("amplify_init_config", " Could not configure Amplify: ${e.message}")
        }
        runningDatabase = Room.databaseBuilder(
            applicationContext,
            RunningDatabase::class.java,
            "runningDatabase"
        ).fallbackToDestructiveMigration().build()
    }
}