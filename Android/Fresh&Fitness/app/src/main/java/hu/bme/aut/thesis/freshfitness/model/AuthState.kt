package hu.bme.aut.thesis.freshfitness.model

data class AuthState(
    val username: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false
)