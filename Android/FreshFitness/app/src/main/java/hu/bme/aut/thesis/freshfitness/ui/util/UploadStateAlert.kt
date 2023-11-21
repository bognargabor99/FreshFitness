package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadStateAlert(text: String, fractionCompleted: Double, onDismiss: () -> Unit) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = fractionCompleted.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("${round(fractionCompleted*100.0).toInt()}% completed", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadStateAlertPreview() {
    UploadStateAlert(text = "Processing file...", fractionCompleted = 0.5) {

    }
}