package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amplifyframework.ui.authenticator.ui.Authenticator
import hu.bme.aut.thesis.freshfitness.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory)
) {
    Authenticator {
        LoggedInScreen {
            viewModel.signOut()
        }
    }
}