package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class WorkoutViewModel(val context: Context) : ViewModel() {

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WorkoutViewModel (context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context)
            }
        }
    }
}