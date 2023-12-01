package hu.bme.aut.thesis.freshfitness.ui.util

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.round

@RunWith(Parameterized::class)
class UploadStateAlertTests(private val text: String, private val completed: Double) {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun uploadStateAlertTest() {
        composeTestRule.setContent {
            UploadStateAlert(text = text, fractionCompleted = completed)
        }

        composeTestRule.onAllNodes(isRoot()).printToLog("currentLabelExists")

        composeTestRule
            .onNodeWithText(text)
            .assertExists()

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(completed.toFloat(), 0f..1f)))

        composeTestRule
            .onNodeWithText("${round(completed*100).toInt()}% completed")
            .assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun generateParameters(): Collection<Array<Any>> {
            return listOf(
                arrayOf("Please wait...", 0.0),
                arrayOf("Processing file...", 0.5),
                arrayOf("Uploading file...", 1.0),
            )
        }
    }
}