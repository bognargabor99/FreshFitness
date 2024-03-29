package hu.bme.aut.thesis.freshfitness.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import hu.bme.aut.thesis.freshfitness.FreshFitnessActivity
import hu.bme.aut.thesis.freshfitness.FreshFitnessApplication
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.location.LocationHelper
import hu.bme.aut.thesis.freshfitness.persistence.model.RunCheckpointEntity
import hu.bme.aut.thesis.freshfitness.repository.RunningRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

class TrackRunningService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val checkpoints = mutableListOf<RunCheckpointEntity>()
    private lateinit var repository: RunningRepository
    private var locationHelper: LocationHelper? = null

    var lastKnownLocation: Location? = null
        private set

    override fun onCreate() {
        super.onCreate()

        repository = RunningRepository(FreshFitnessApplication.runningDatabase.runningDao())
        Log.d("trackRunningService", "Opened database, created repository")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action.equals(STOP_FILTER)) {
            Log.d("trackRunningService", "Stopping tracking running.")
            locationHelper?.stopLocationMonitoring()
            this.stopSelf()
        }

        startForeground(NOTIFICATION_ID, createNotificationWithChannel())
        _isRunning.update { true }
        if (locationHelper == null) {
            val helper = LocationHelper(applicationContext, TrackRunningServiceCallback())
            helper.startLocationMonitoring()
            locationHelper = helper
        }

        return START_STICKY
    }

    private fun createNotificationWithChannel(): Notification {
        createNotificationChannel()
        return createNotification()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, FreshFitnessActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        val contentIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, TrackRunningService::class.java)
        stopIntent.action = STOP_FILTER

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(resources.getQuantityString(R.plurals.notification_text, checkpoints.size, checkpoints.size))
            .setSmallIcon(R.drawable.directions_run)
            .setContentIntent(contentIntent)
            .addAction(0, getString(R.string.stop), stopPendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        if (checkpoints.size > 1) {
            scope.launch {
                repository.insertNewRunning(checkpoints)
            }.invokeOnCompletion {
                Log.d("trackRunningService", "Inserted new running")
            }
        }
        _isRunning.update { false }

        super.onDestroy()
    }

    companion object {
        private const val STOP_FILTER = "STOP_FRESH_FITNESS_MONITORING"
        private const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "TrackRunningServiceChannel"
        private var _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    }

    inner class TrackRunningServiceCallback : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach {
                if (checkpoints.isEmpty() || lastKnownLocation!!.distanceTo(it) < 1000f) {
                    val checkpoint = RunCheckpointEntity(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        height = it.altitude,
                        timestamp = it.time
                    )
                    checkpoints.add(checkpoint)
                    lastKnownLocation = it
                }
            }
            updateNotification()

            Log.d("trackRunningService", "Added new location to this running route")
            Log.d("trackRunningService", "lat: ${round(checkpoints.last().latitude * 100.0) / 100.0}, lng: ${round(checkpoints.last().longitude * 100.0) / 100.0}")
        }

        private fun updateNotification() {
            val manager = getSystemService(NotificationManager::class.java)
            manager.notify(NOTIFICATION_ID, createNotification())
        }
    }
}