package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun NoConnectionNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(red = 0.5f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_connection),
            color = Color.White
        )
    }
}

@Composable
fun BackOnlineNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green.copy(green = 0.5f)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.back_online),
            color = Color.White
        )
    }
}