package hu.bme.aut.thesis.freshfitness.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(private val context: Context, private val callback: LocationCallback) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun startLocationMonitoring() {
        Log.d("locService", "Starting monitoring location")
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000).build()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    fun stopLocationMonitoring() {
        fusedLocationClient.removeLocationUpdates(callback)
    }
}