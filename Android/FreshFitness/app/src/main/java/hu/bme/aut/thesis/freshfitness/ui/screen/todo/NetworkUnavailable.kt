package hu.bme.aut.thesis.freshfitness.ui.screen.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.thesis.freshfitness.R

@Composable
fun NetworkUnavailable() {
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize())
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = stringResource(R.string.network_unavailable))
            Text(text = stringResource(R.string.check_connection))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkUnavailablePreview() {
    NetworkUnavailable()
}