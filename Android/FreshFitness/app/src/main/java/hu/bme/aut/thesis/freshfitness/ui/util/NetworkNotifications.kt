package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R

@Preview
@Composable
fun NoConnectionNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(red = 0.8f))
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_connection),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
fun BackOnlineNotification() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff88acff))
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.back_online),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}