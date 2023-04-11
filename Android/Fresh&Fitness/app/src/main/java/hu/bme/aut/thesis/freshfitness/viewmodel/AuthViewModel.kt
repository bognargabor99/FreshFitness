package hu.bme.aut.thesis.freshfitness.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amazonaws.mobileconnectors.cognitoidentityprovider.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import hu.bme.aut.thesis.freshfitness.BuildConfig
import hu.bme.aut.thesis.freshfitness.model.AuthState
import hu.bme.aut.thesis.freshfitness.model.SignUpConfirmationState

class AuthViewModel(val context: Context) : ViewModel() {
    var authData by mutableStateOf(AuthState())

    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var showVerificationBox by mutableStateOf(false)
    var showVerificationResultBox by mutableStateOf(SignUpConfirmationState.UNKNOWN)

    var showSignInFailedResult by mutableStateOf(false)
    var signInFailureReason: String = ""

    private var user: CognitoUser? = null
    var session: CognitoUserSession? by mutableStateOf(null)

    init {
        val userPool = CognitoUserPool(
            context,
            BuildConfig.COGNITO_USERPOOL_ID,
            BuildConfig.COGNITO_CLIENT_ID,
            BuildConfig.COGNITO_CLIENT_SECRET,
            Regions.EU_CENTRAL_1
        )
        this.user = userPool.user
    }

    fun signUp() {
        val userPool = CognitoUserPool(
            context,
            BuildConfig.COGNITO_USERPOOL_ID,
            BuildConfig.COGNITO_CLIENT_ID,
            BuildConfig.COGNITO_CLIENT_SECRET,
            Regions.EU_CENTRAL_1
        )
        val userAttributes = CognitoUserAttributes()
        userAttributes.addAttribute("email", email)
        userPool.signUpInBackground(username, password, userAttributes, null, signupCallback)
        this.email = ""
        this.username = ""
        this.password = ""
    }

    fun conformSignUp(verificationCode: String) {
        this.user?.confirmSignUpInBackground(verificationCode, false, confirmationCallback)
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updateUsername(username: String) {
        this.username = username
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun signIn() {
        this.authData = AuthState(this.username, this.password)
        this.user?.getSessionInBackground(authenticationHandler)
    }

    fun forgotPassword() {
        TODO("Forgot password in the future")
    }

    fun signOut() {
        this.user?.signOut()
        this.session = null
        this.authData = AuthState()
    }

    private var signupCallback: SignUpHandler = object : SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            Log.d("signup", "Successfully signed up user: ${user?.userId} to ${user?.userPoolId}")
            if (signUpResult?.isUserConfirmed != true) {
                this@AuthViewModel.user = user
                this@AuthViewModel.showVerificationBox = true
            }
        }

        override fun onFailure(exception: Exception?) {
            Log.d("signup", exception?.localizedMessage.toString())
        }
    }

    // Call back handler for confirmSignUp API
    private var confirmationCallback: GenericHandler = object : GenericHandler {
        override fun onSuccess() {
            Log.d("confirm", "Confirmation succeeded for ${user?.userId}")
            this@AuthViewModel.showVerificationResultBox = SignUpConfirmationState.CONFIRMED
        }

        override fun onFailure(exception: Exception) {
            this@AuthViewModel.showVerificationResultBox = SignUpConfirmationState.UNCONFIRMED
        }
    }

    private var authenticationHandler: AuthenticationHandler = object : AuthenticationHandler {
        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            this@AuthViewModel.session = userSession
            this@AuthViewModel.password = ""
        }

        override fun getAuthenticationDetails(
            authenticationContinuation: AuthenticationContinuation?,
            userId: String?
        ) {
            val authenticationDetails = AuthenticationDetails(
                if (!userId.isNullOrBlank()) userId else this@AuthViewModel.username,
                this@AuthViewModel.authData.password,
                null
            )
            authenticationContinuation?.setAuthenticationDetails(authenticationDetails)
            authenticationContinuation?.continueTask()
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            continuation?.continueTask()
        }

        override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            continuation?.continueTask()
        }

        override fun onFailure(exception: java.lang.Exception?) {
            this@AuthViewModel.showSignInFailedResult = true
            if (exception is NotAuthorizedException) {
                this@AuthViewModel.signInFailureReason = if (exception.errorMessage != null) exception.errorMessage else "Something went wrong."
            }
            else
                this@AuthViewModel.signInFailureReason = "Something went wrong."
        }

    }

    // TODO: Implement this
    private var forgotPasswordHandler: ForgotPasswordHandler = object : ForgotPasswordHandler {
        override fun onSuccess() {
            TODO("Not yet implemented")
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation?) {
            TODO("Not yet implemented")
        }

        override fun onFailure(exception: java.lang.Exception?) {
            TODO("Not yet implemented")
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel (context = this[APPLICATION_KEY] as Context)
            }
        }
    }
}