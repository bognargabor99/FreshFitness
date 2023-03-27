package hu.bme.aut.thesis.freshfitness.aws.cognito

import android.content.Context
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions

class CognitoSettings(private val context: Context) {
    private val userPoolId: String = "eu-central-1_GxTKLPzc1"
    private val clientId: String = "7gvkac478lghc0t96kmuj539bv"
    private val clientSecret: String = "757ju5k7jvpj2ktoea2i1pftjfn24oku9ruv1vhlls2f1an2sfj"
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