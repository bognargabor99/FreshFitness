package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.model.ProfileScreenState
import hu.bme.aut.thesis.freshfitness.model.SignUpConfirmationState
import hu.bme.aut.thesis.freshfitness.ui.util.InformationDialog
import hu.bme.aut.thesis.freshfitness.ui.util.InputDialog
import hu.bme.aut.thesis.freshfitness.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory)
) {
    var screenState: ProfileScreenState by rememberSaveable { mutableStateOf(ProfileScreenState.LOGIN) }
    if (viewModel.session?.isValid != true) {
        when (screenState) {
            ProfileScreenState.SIGNUP -> {
                SignUpProfileScreen(viewModel) { screenState = ProfileScreenState.LOGIN }
            }
            ProfileScreenState.LOGIN -> {
                LoginProfileScreen(
                    viewModel = viewModel,
                    onNavigateSignUp = { screenState = ProfileScreenState.SIGNUP },
                    onNavigateForgotPassword = {
                        viewModel.forgotPassword()
                        screenState = ProfileScreenState.FORGOT_PASSWORD
                    }
                )
            }
            ProfileScreenState.FORGOT_PASSWORD -> {
                ForgotPasswordScreen(
                    viewModel = viewModel,
                    onNavigateSignIn = { screenState = ProfileScreenState.LOGIN }
                )
            }
        }
    }
    else {
        LoggedInScreen(
            onLogOut = { viewModel.signOut() }
        )
    }
}

@Composable
fun SignUpProfileScreen(
    viewModel: AuthViewModel,
    onNavigateSignIn: () -> Unit
) {
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
        onNavigateSignIn = onNavigateSignIn,
        onSignUp = {
            viewModel.signUp()
        },
        email = viewModel.email,
        username = viewModel.username,
        password = viewModel.password,
        onEmailChange = { newEmail -> viewModel.updateEmail(newEmail) },
        onUserNameChange = { newUsername -> viewModel.updateUsername(newUsername) },
        onPasswordChange = { newPassword -> viewModel.updatePassword(newPassword) },
        onVerifyExistingAccount = { verificationCode -> viewModel.confirmSignUp(verificationCode) }
    )
}

@Composable
fun LoginProfileScreen(
    viewModel: AuthViewModel,
    onNavigateSignUp: () -> Unit,
    onNavigateForgotPassword: () -> Unit
) {
    if (viewModel.showSignInFailedResult) {
        SignInFailureAlert(
            subTitle = viewModel.signInFailureReason,
            setShowDialog = { viewModel.showSignInFailedResult = it }
        )
    }
    LoginScreen(
        onSignIn = { viewModel.signIn() },
        onNavigateSignUp = onNavigateSignUp,
        username = viewModel.username,
        password = viewModel.password,
        onUserNameChange = { newUsername -> viewModel.updateUsername(newUsername) },
        onPasswordChange = { newPassword -> viewModel.updatePassword(newPassword) },
        onForgotPassword = {
            viewModel.forgotPassword()
            onNavigateForgotPassword()
        }
    )
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
        subTitle = stringResource(R.string.verification_code_sent_confirmation)
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
            imageVector = Icons.Filled.SentimentVeryDissatisfied,
            tint = Color(144, 0, 0),
            contentDescription = null)
    }
}

@Preview
@Composable
fun VerificationAlertPreview() {
    VerificationAlert(setShowDialog = { }, signUpConfirmation = { })
}

@Preview
@Composable
fun VerificationConfirmedAlertPreview() {
    VerificationResultAlert(onDismiss = { }, verificationResult = SignUpConfirmationState.CONFIRMED)
}

@Preview
@Composable
fun VerificationUnconfirmedAlertPreview() {
    VerificationResultAlert(onDismiss = { }, verificationResult = SignUpConfirmationState.UNCONFIRMED)
}

@Preview
@Composable
fun SignInFailureAlertAlertPreview() {
    SignInFailureAlert(setShowDialog = { }, subTitle = "Incorrect username or password.")
}