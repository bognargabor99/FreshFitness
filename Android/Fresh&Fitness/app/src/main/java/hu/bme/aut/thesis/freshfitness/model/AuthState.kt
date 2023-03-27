package hu.bme.aut.thesis.freshfitness.model

data class AuthState(
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val imageUrl: String = ""
)