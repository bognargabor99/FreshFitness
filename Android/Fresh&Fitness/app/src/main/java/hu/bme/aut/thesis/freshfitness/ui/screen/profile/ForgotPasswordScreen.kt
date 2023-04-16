package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.InputField
import hu.bme.aut.thesis.freshfitness.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ForgotPasswordHeader()
        ForgotPasswordInputFields(
            newPassword = viewModel.password,
            onNewPasswordChange = { viewModel.updatePassword(it) },
            verificationCode = viewModel.verificationCode,
            onVerificationCodeChange = { viewModel.updateVerificationCode(it) },
            onDone = { viewModel.continueForgotPasswordFlow() }
        )
        ForgotPasswordFooter(
            onResetPassword = { viewModel.continueForgotPasswordFlow() },
            onSignInClick = onNavigateSignIn
        )
    }
}

@Composable
fun ForgotPasswordHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.password_reset),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(R.string.verification_code_sent_forgot),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ForgotPasswordInputFields(
    modifier: Modifier = Modifier,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(modifier = modifier) {
        InputField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.password),
            visualTransFormation = PasswordVisualTransformation(),
            icon = Icons.Filled.Lock,
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = verificationCode,
            onValueChange = onVerificationCodeChange,
            label = stringResource(R.string.verification_code),
            placeholder = stringResource(R.string.verification_code),
            visualTransFormation = PasswordVisualTransformation(),
            icon = Icons.Filled.VpnKey,
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyBoardActions = KeyboardActions(onDone = { onDone() })
        )
    }
}

@Composable
fun ForgotPasswordFooter(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = { },
    onResetPassword: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
    ) {
        Button(onClick = onResetPassword, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.reset_password))
        }
        TextButton(onClick = onSignInClick) {
            Text(text = stringResource(R.string.go_back))
        }
    }
}