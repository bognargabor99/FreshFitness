package hu.bme.aut.thesis.freshfitness.model

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}
