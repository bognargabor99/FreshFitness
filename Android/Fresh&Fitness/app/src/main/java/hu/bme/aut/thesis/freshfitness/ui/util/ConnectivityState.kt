package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import hu.bme.aut.thesis.freshfitness.currentConnectivityState
import hu.bme.aut.thesis.freshfitness.model.ConnectionState
import hu.bme.aut.thesis.freshfitness.observeConnectivityAsFlow

@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collect { value = it }
    }
}