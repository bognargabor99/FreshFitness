package hu.bme.aut.thesis.freshfitness.ui.util

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.bme.aut.thesis.freshfitness.R
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDatePicker(
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    blockedDates: List<String> = listOf(),
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()
    val simpleDateFormat by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd")) }
    state.setSelection(simpleDateFormat.parse(selectedDate)?.time?.plus((1000 * 60 * 60)))
    val dateValidator: (Long) -> Boolean = { checkedDate ->
        Date(Date().time - 1000 * 60 * 60 * 24).before(Date(checkedDate)) &&
        !blockedDates.any { blockedDate ->
            blockedDate.take(10) == simpleDateFormat.format(Date(checkedDate))
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (state.selectedDateMillis != null){
                        state.selectedDateMillis?.let{
                            onSelectDate(simpleDateFormat.format(Date(it)))
                        }
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(id = R.string.ok).capitalize(Locale.current))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel).capitalize(Locale.current))
            }
        }
    ) {
        DatePicker(state = state, dateValidator = dateValidator)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetTimePicker(
    onSelectTime: (hour: Int, minute: Int) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {}
) {
    val state = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                TimePicker(
                    state = state,
                    modifier = Modifier.padding(16.dp)
                )
                Row {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel).capitalize(Locale.current))
                    }
                    TextButton(onClick = {
                        onSelectTime(state.hour, state.minute)
                    }) {
                        Text(text = stringResource(id = R.string.ok).capitalize(Locale.current))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TargetDatePickerPreview() {
    TargetDatePicker(selectedDate = "2023-11-17", blockedDates = listOf(), onSelectDate = { }, onDismiss = { })
}

@Preview(showBackground = true)
@Composable
fun TargetTimePickerPreview() {
    TargetTimePicker()
}