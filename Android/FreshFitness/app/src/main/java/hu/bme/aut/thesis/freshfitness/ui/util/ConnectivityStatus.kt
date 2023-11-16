package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import hu.bme.aut.thesis.freshfitness.model.ConnectionState

@Composable
fun ConnectivityStatus(
    availableContent: @Composable () -> Unit,
    unAvailableContent: @Composable () -> Unit
) {
    // This will cause re-composition on every network state change
    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available

    if (isConnected) {
        availableContent()
    } else {
        unAvailableContent()
    }
}