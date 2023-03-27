package hu.bme.aut.thesis.freshfitness.ui.screen.auth

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AuthenticationScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
) {
    var isSignUp by rememberSaveable { mutableStateOf(true) }
    if (isSignUp)
        RegisterScreen(onSignUp = onSignUp, onSignIn = { isSignUp = false })
    else
        LoginScreen(onSignIn = onSignIn, onSignUp = { isSignUp = true })
}