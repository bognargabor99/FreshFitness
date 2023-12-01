package hu.bme.aut.thesis.freshfitness.ui.screen.social

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import hu.bme.aut.thesis.freshfitness.model.social.Post
import org.junit.Rule
import org.junit.Test

class SocialScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val post1 = Post(
        id = 0,
        details = "Test",
        imageLocation = "someLocation",
        createdAt = "2023-11-22T10:19:25",
        username = "gaborbognar123",
        likeCount = 5,
        likes = mutableListOf("andrew_huberman", "jason_todd", "dick_grayson", "emily_monroe", "keanu_reeves"),
        commentCount = 0,
        comments = mutableListOf(),
    )

    private val post2 = Post(
        id = 0,
        details = "Test",
        imageLocation = "",
        createdAt = "2023-11-22T10:19:25",
        username = "gaborbognar123",
        likeCount = 5,
        likes = mutableListOf("andrew_huberman", "jason_todd", "dick_grayson", "emily_monroe", "keanu_reeves"),
        commentCount = 0,
        comments = mutableListOf(),
    )

    private val testUsername = "test_user"

    private fun setUp(p: Post, editEnabled: Boolean) {
        composeTestRule.setContent {
            var post by remember { mutableStateOf(p) }
            val onLike: (Post) -> Unit = { _ ->
                post = if (!post.likes.contains(testUsername)) {
                    post.copy(
                        likes = post.likes.run {
                            add(testUsername)
                            this },
                        likeCount = post.likeCount + 1
                    )
                } else {
                    post.copy(
                        likes = post.likes.run {
                            remove(testUsername)
                            this },
                        likeCount = post.likeCount - 1
                    )
                }
            }
            PostCard(
                modifier = Modifier.semantics(mergeDescendants = false) { },
                post = post,
                userName = testUsername,
                editEnabled = editEnabled,
                onLikePost = onLike
            )
        }
    }

    @Test
    fun headerTest() {
        setUp(p = post1, editEnabled = true)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasTextExactly("gaborbognar123"), useUnmergedTree = true)
            .assertExists()
            .assert(hasAnySibling(hasText("11-22 10:19")))
    }

    @Test
    fun detailsTest() {
        setUp(p = post1, editEnabled = true)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasText("Test", substring = false))
            .assertExists()
    }

    @Test
    fun imageExistsTest() {
        setUp(p = post1, editEnabled = true)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasContentDescription("someLocation", substring = true), useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun imageDoesNotExistTest() {
        setUp(p = post2, editEnabled = true)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasContentDescription("Image from: ", substring = true), useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun likePostEnabledTest() {
        setUp(p = post2, editEnabled = true)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasContentDescription("Like or dislike post"), useUnmergedTree = true)
            .onParent()
            .assertIsEnabled()
            .assert(hasAnySibling(hasText("${post2.likeCount} likes")))
            .performClick()
            .assert(hasAnySibling(hasText("${post2.likeCount + 1} likes")))
            .performClick()
            .assert(hasAnySibling(hasText("${post2.likeCount} likes")))
    }

    @Test
    fun likePostDisabledTest() {
        setUp(p = post2, editEnabled = false)

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        composeTestRule
            .onNode(hasContentDescription("Like or dislike post"), useUnmergedTree = true)
            .onParent()
            .assertIsNotEnabled()
            .assert(hasAnySibling(hasText("${post2.likeCount} likes")))
    }
}