package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputField(
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