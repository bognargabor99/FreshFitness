package hu.bme.aut.thesis.freshfitness.viewmodel

import androidx.lifecycle.ViewModel
import hu.bme.aut.thesis.freshfitness.model.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()
}