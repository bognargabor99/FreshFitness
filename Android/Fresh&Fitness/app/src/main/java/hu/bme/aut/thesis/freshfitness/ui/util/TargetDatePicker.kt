package hu.bme.aut.thesis.freshfitness.ui.util

import android.annotation.SuppressLint
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDatePicker(
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()
    state.setSelection(SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)?.time?.plus((1000 * 60 * 60)))
    val dateValidator: (Long) -> Boolean = {
        Date(Date().time - 1000 * 60 * 60 * 24).before(Date(it))
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    if (state.selectedDateMillis != null){
                        state.selectedDateMillis?.let{
                            onSelectDate(simpleDateFormat.format(Date(it)))
                        }
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    ) {
        DatePicker(state = state, dateValidator = dateValidator)
    }
}

@Preview(showBackground = true)
@Composable
fun TargetDatePickerPreview() {
    TargetDatePicker(selectedDate = "2023-11-17", onSelectDate = { }, onDismiss = { })
}