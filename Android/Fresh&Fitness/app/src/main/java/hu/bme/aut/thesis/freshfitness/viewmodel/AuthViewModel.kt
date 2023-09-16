package hu.bme.aut.thesis.freshfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import hu.bme.aut.thesis.freshfitness.decodeJWT
import org.json.JSONObject

class AuthViewModel : ViewModel() {
    var isAdmin by mutableStateOf(false)
        private set

    fun setIsAdmin(): Boolean {
        Amplify.Auth.fetchAuthSession(
            {
                val session = (it as AWSCognitoAuthSession)
                if (session.accessToken == null)
                    isAdmin = false
                else {
                    val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                    val jsonObject = JSONObject(jwt)
                    val groups = jsonObject.getJSONArray("cognito:groups")
                    for (i in 0 until groups.length()) {
                        if (groups.getString(i) == "Admins") {
                            isAdmin = true
                        }
                    }
                    Log.i("AmplifyQuickstart", "Auth session = $it")
                }
            },
            { error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error) }
        )
        return true
    }

    fun signOut() {
        Amplify.Auth.signOut {  }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel ()
            }
        }
    }
}