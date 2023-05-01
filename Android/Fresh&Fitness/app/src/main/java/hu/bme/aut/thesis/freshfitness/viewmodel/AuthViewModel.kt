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
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException
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
    var verificationCode by mutableStateOf("")
        private set

    var showVerificationBox by mutableStateOf(false)
    var showVerificationResultBox by mutableStateOf(SignUpConfirmationState.UNKNOWN)

    var showSignInFailedResult by mutableStateOf(false)
    var signInFailureReason: String = ""

    private var user: CognitoUser? = null
    var session: CognitoUserSession? by mutableStateOf(null)

    private lateinit var forgotPasswordContinuation: ForgotPasswordContinuation

    init {
        this.user = getUserPool().user
    }

    fun signUp() {
        val userPool = getUserPool()
        val userAttributes = CognitoUserAttributes()
        userAttributes.addAttribute("email", email)
        userPool.signUpInBackground(username, password, userAttributes, null, signupCallback)
        this.email = ""
        this.username = ""
        this.password = ""
    }

    fun confirmSignUp(verificationCode: String) {
        setUserFromPool()
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

    fun updateVerificationCode(newCode: String) {
        this.verificationCode = newCode
    }

    fun signIn() {
        this.authData = AuthState(this.username, this.password)
        this.user?.getSessionInBackground(authenticationHandler)
    }

    fun forgotPassword() {
        setUserFromPool()
        this.user?.forgotPasswordInBackground(forgotPasswordHandler)
    }

    fun continueForgotPasswordFlow() {
        this.forgotPasswordContinuation.setPassword(this.password)
        this.forgotPasswordContinuation.setVerificationCode(this.verificationCode)
        this.password = ""
        this.email = ""
        this.username = ""
        this.forgotPasswordContinuation.continueTask()
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
            when (exception) {
                is NotAuthorizedException -> {
                    this@AuthViewModel.signInFailureReason = exception.errorMessage
                }
                is UserNotConfirmedException -> {
                    this@AuthViewModel.signInFailureReason = exception.errorMessage
                }
                else -> this@AuthViewModel.signInFailureReason = "Something went wrong."
            }
        }

    }

    private var forgotPasswordHandler: ForgotPasswordHandler = object : ForgotPasswordHandler {
        override fun onSuccess() {
            Log.d("forgot_password", "Successfully changed password of ${user?.userId}")
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation) {
            Log.d("forgot_password", "Changing password")
            this@AuthViewModel.forgotPasswordContinuation = continuation
        }

        override fun onFailure(exception: java.lang.Exception?) {
            Log.d("forgot_password", exception?.localizedMessage.toString())
        }
    }

    private fun getUserPool(): CognitoUserPool = CognitoUserPool(
        context,
        BuildConfig.COGNITO_USERPOOL_ID,
        BuildConfig.COGNITO_CLIENT_ID,
        BuildConfig.COGNITO_CLIENT_SECRET,
        Regions.EU_CENTRAL_1
    )

    private fun setUserFromPool() {
        if (this.user?.userId == null)
            this.user = this.getUserPool().getUser(this.username)
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel (context = this[APPLICATION_KEY] as Context)
            }
        }
    }
}