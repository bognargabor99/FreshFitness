package hu.bme.aut.thesis.freshfitness.aws.cognito

import android.content.Context
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import hu.bme.aut.thesis.freshfitness.BuildConfig

class CognitoSettings(private val context: Context) {
    private val userPoolId: String = BuildConfig.COGNITO_USERPOOL_ID
    private val clientId: String = BuildConfig.COGNITO_CLIENT_ID
    private val clientSecret: String = BuildConfig.COGNITO_CLIENT_SECRET
    private val cognitoRegion: Regions = Regions.EU_CENTRAL_1

    fun getUserPool() : CognitoUserPool {
        return CognitoUserPool(
            this.context,
            this.userPoolId,
            this.clientId,
            this.clientSecret,
            this.cognitoRegion
        )
    }
}