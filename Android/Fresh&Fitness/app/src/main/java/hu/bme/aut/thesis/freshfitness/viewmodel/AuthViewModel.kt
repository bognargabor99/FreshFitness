package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.core.Amplify

class AuthViewModel(val context: Context) : ViewModel() {
    fun signOut() {
        Amplify.Auth.signOut {  }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel (context = this[APPLICATION_KEY] as Context)
            }
        }
    }
}