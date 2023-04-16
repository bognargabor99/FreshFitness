package hu.bme.aut.thesis.freshfitness.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onSignUp: () -> Unit,
    onNavigateSignIn: () -> Unit,
    username: String,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onVerifyExistingAccount: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterHeader()
        RegisterInputFields(
            email = email,
            onEmailChange = onEmailChange,
            username = username,
            onUsernameChange = onUserNameChange,
            password = password,
            onPasswordChange = onPasswordChange,
            onDone = onSignUp
        )
        RegisterFooter(
            onSignUpClick = onSignUp,
            onSignInClick = onNavigateSignIn,
            onVerifyExistingAccount = onVerifyExistingAccount
        )
    }
}

@Composable
fun RegisterHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(R.string.sign_up_to_continue),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RegisterInputFields(
    modifier: Modifier = Modifier,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
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
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.email_address),
            icon = Icons.Filled.Email,
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
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
fun RegisterFooter(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = { },
    onSignUpClick: () -> Unit = { },
    onVerifyExistingAccount: (String) -> Unit = { }
) {
    var showVerificationBox by rememberSaveable { mutableStateOf(false) }

    if (showVerificationBox) {
        VerificationAlert(
            setShowDialog = { showVerificationBox = it },
            signUpConfirmation = onVerifyExistingAccount
        )
    }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
    ) {
        Button(onClick = onSignUpClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.sign_up))
        }
        TextButton(onClick = onSignInClick) {
            Text(text = stringResource(R.string.already_have_account_login_here))
        }
        TextButton(onClick = { showVerificationBox = true }) {
            Text(text = stringResource(R.string.verify_existing_account))
        }
    }
}