package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.SignUpConfirmationState
import hu.bme.aut.thesis.freshfitness.ui.util.InformationDialog
import hu.bme.aut.thesis.freshfitness.ui.util.InputDialog
import hu.bme.aut.thesis.freshfitness.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory)
) {
    var isSignUp by rememberSaveable { mutableStateOf(false) }
    if (viewModel.session?.isValid != true) {
        if (isSignUp) {
            if (viewModel.showVerificationBox) {
                VerificationAlert(
                    setShowDialog = { viewModel.showVerificationBox = it },
                    signUpConfirmation = { viewModel.confirmSignUp(it) }
                )
            }
            if (viewModel.showVerificationResultBox != SignUpConfirmationState.UNKNOWN) {
                VerificationResultAlert(
                    onDismiss = { viewModel.showVerificationResultBox = SignUpConfirmationState.UNKNOWN },
                    verificationResult = viewModel.showVerificationResultBox
                )
            }
            RegisterScreen(
                onNavigateSignIn = { isSignUp = false },
                onSignUp = {
                    viewModel.signUp()
                },
                email = viewModel.email,
                username = viewModel.username,
                password = viewModel.password,
                onEmailChange = { newEmail -> viewModel.updateEmail(newEmail) },
                onUserNameChange = { newUsername -> viewModel.updateUsername(newUsername) },
                onPasswordChange = { newPassword -> viewModel.updatePassword(newPassword) }
            )
        }
        else {
            if (viewModel.showSignInFailedResult) {
                SignInFailureAlert(
                    subTitle = viewModel.signInFailureReason,
                    setShowDialog = { viewModel.showSignInFailedResult = it }
                )
            }
            LoginScreen(
                onSignIn = { viewModel.signIn() },
                onNavigateSignUp = { isSignUp = true },
                username = viewModel.username,
                password = viewModel.password,
                onUserNameChange = { newUsername -> viewModel.updateUsername(newUsername) },
                onPasswordChange = { newPassword -> viewModel.updatePassword(newPassword) }
            )
        }
    }
    else {
        LoggedInScreen(
            onLogOut = { viewModel.signOut() }
        )
    }
}

@Composable
fun VerificationAlert(
    setShowDialog: (Boolean) -> Unit,
    signUpConfirmation: (String) -> Unit
) {
    InputDialog(
        value = "",
        setShowDialog = setShowDialog,
        title = stringResource(R.string.verify_email_address),
        placeholder = stringResource(R.string.enter_verification_code),
        subTitle = stringResource(R.string.verification_code_sent)
    ) { verificationCode ->
        signUpConfirmation(verificationCode)
    }
}

@Composable
fun VerificationResultAlert(
    onDismiss: (Boolean) -> Unit,
    verificationResult: SignUpConfirmationState
) {
    InformationDialog(
        setShowDialog = onDismiss,
        title = stringResource(if (verificationResult == SignUpConfirmationState.CONFIRMED) R.string.account_activation_success else R.string.account_activation_failed)
    ) {
        Icon(
            modifier = Modifier.size(50.dp),
            imageVector = if (verificationResult == SignUpConfirmationState.CONFIRMED) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            tint = if (verificationResult == SignUpConfirmationState.CONFIRMED) Color(0, 144, 0) else Color(144, 0, 0),
            contentDescription = null)
    }
}

@Composable
fun SignInFailureAlert(
    subTitle: String,
    setShowDialog: (Boolean) -> Unit
) {
    InformationDialog(
        title = stringResource(R.string.could_not_sign_in),
        subTitle = subTitle,
        setShowDialog = setShowDialog
    ) {
        Icon(
            modifier = Modifier.size(50.dp),
            imageVector = Icons.Filled.SentimentDissatisfied,
            tint = Color(144, 0, 0),
            contentDescription = null)
    }
}