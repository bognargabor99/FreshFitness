package hu.bme.aut.thesis.freshfitness.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import hu.bme.aut.thesis.freshfitness.amplify.AuthService
import hu.bme.aut.thesis.freshfitness.decodeJWT
import org.json.JSONObject

class AuthViewModel : ViewModel() {
    var isAdmin by mutableStateOf(false)
        private set

    fun setIsAdmin(): Boolean {
        AuthService.fetchAuthSession(
            {
                isAdmin = false
                val session = (it as AWSCognitoAuthSession)
                if (session.accessToken != null) {
                    val jwt = decodeJWT(session.accessToken!!.split(".").getOrElse(1) { "" })
                    isAdmin = false
                    val jsonObject = JSONObject(jwt)
                    if (jsonObject.has("cognito:groups")) {
                        val groups = jsonObject.getJSONArray("cognito:groups")
                        for (i in 0 until groups.length()) {
                            if (groups.getString(i) == "Admins") {
                                isAdmin = true
                            }
                        }
                    }
                    Log.i("fresh_fitness_profile", "Auth session = $it")
                }
            },
            {
                Log.e("fresh_fitness_profile", "Failed to fetch auth session")
            }
        )
        return true
    }

    fun signOut() {
        AuthService.signOut()
    }
}