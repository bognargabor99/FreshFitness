package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    title: String,
    currentNumber: Int,
    numbers: IntRange,
    onNumberChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.clip(CircleShape),
                onClick = { onNumberChange(currentNumber - 1) }, enabled = numbers.contains(currentNumber - 1)) {
                Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = null)
            }
            Text(text = "$currentNumber", fontSize = 18.sp)
            IconButton(
                modifier = Modifier.clip(CircleShape),
                onClick = { onNumberChange(currentNumber + 1) }, enabled = numbers.contains(currentNumber + 1)) {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberPickerPreviewMin() {
    NumberPicker(title = "Sets", currentNumber = 1, numbers = 1..6, onNumberChange = { })
}

@Preview(showBackground = true)
@Composable
fun NumberPickerPreviewMiddle() {
    NumberPicker(title = "Sets", currentNumber = 3, numbers = 1..6, onNumberChange = { })
}

@Preview(showBackground = true)
@Composable
fun NumberPickerPreviewMax() {
    NumberPicker(title = "Sets", currentNumber = 6, numbers = 1..6, onNumberChange = { })
}