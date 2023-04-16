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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R
import hu.bme.aut.thesis.freshfitness.ui.util.InputField

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit,
    onNavigateSignUp: () -> Unit,
    username: String,
    onUserNameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader()
        LoginInputFields(
            username = username,
            onUsernameChange = onUserNameChange,
            password = password,
            onPasswordChange = onPasswordChange,
            onDone = onSignIn
        )
        LoginFooter(
            onSignInClick = onSignIn,
            onSignUpClick = onNavigateSignUp,
            onForgotPassword = onForgotPassword
        )
    }
}

@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome_back),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(R.string.sign_in_to_continue),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoginInputFields(
    modifier: Modifier = Modifier,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(modifier = modifier) {
        InputField(
            value = username,
            onValueChange = onUsernameChange,
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.username),
            icon = Icons.Filled.AccountBox,
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.password),
            visualTransFormation = PasswordVisualTransformation(),
            icon = Icons.Filled.Lock,
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyBoardActions = KeyboardActions(onDone = { onDone() })
        )
    }
}

@Composable
fun LoginFooter(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = { },
    onSignUpClick: () -> Unit = { },
    onForgotPassword: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
    ) {
        Button(onClick = onSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.sign_in))
        }
        TextButton(onClick = onSignUpClick) {
            Text(text = stringResource(R.string.dont_have_account))
        }
        TextButton(onClick = onForgotPassword) {
            Text(text = stringResource(R.string.forgot_password))
        }
    }
}

@Preview
@Composable
fun LoginFooterPreview() {
    LoginFooter()
}