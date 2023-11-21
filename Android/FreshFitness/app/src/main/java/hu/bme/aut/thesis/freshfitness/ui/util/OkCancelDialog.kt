package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.bme.aut.thesis.freshfitness.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OkCancelDialog(
    title: String,
    subTitle: String,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Spacer(Modifier.height(6.dp))
                Text(subTitle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Button(onClick = onOk, colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}