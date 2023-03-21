package hu.bme.aut.thesis.freshfitness.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader()
        LoginInputFields(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it }
        )
        LoginFooter(
            onSignInClick = onSignIn
        )
    }
}

@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Welcome",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Sign in to continue",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoginInputFields(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
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
            keyBoardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    visualTransFormation: VisualTransformation = VisualTransformation.None,
    icon: ImageVector,
    keyBoardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyBoardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        visualTransformation = visualTransFormation,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyBoardActions
    )
}

@Composable
fun LoginFooter(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = { },
    onSignUpClick: () -> Unit = { }
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
    }
}