package hu.bme.aut.thesis.freshfitness.amplify

import android.util.Log
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.core.Amplify

object AuthService {
    fun fetchAuthSession(onSuccess: (AuthSession) -> Unit, onError: () -> Unit = { }) {
        Amplify.Auth.fetchAuthSession(
            { onSuccess(it) },
            {
                Log.e("social_feed_fetch_auth_session", "Failed to fetch auth session", it)
                onError()
            }
        )
    }

    fun signOut() {
        Amplify.Auth.signOut {  }
    }
}