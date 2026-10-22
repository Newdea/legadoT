package io.legado.app.ui.book.read

import io.legado.app.help.nostr.ReviewDetailItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewReplyThreadTest {

    @Test
    fun nestsRepliesUnderTheirReferencedComment() {
        val root = reviewItem("root", "Root")
        val first = reviewItem("first", "First", replyToId = "root")
        val nested = reviewItem("nested", "Nested", replyToId = "first")

        val result = nestReviewReplies(root.id!!, listOf(first, nested))

        assertEquals(listOf("first"), result.map { it.id })
        assertEquals(listOf("nested"), result.single().replies.map { it.id })
    }

    @Test
    fun keepsRepliesWithUnknownParentVisibleAtRoot() {
        val root = reviewItem("root", "Root")
        val reply = reviewItem("reply", "Reply", replyToId = "not-loaded")

        val result = nestReviewReplies(root.id!!, listOf(reply))

        assertEquals(listOf("reply"), result.map { it.id })
        assertEquals("not-loaded", result.single().replyToId)
        assertNull(replyTargetName(result.single(), root))
    }

    @Test
    fun directRepliesToRootRemainTopLevel() {
        val root = reviewItem("root", "Root")
        val first = reviewItem("first", "First", replyToId = "root")
        val second = reviewItem("second", "Second", replyToId = "root")

        val result = nestReviewReplies(root.id, listOf(first, second))

        assertEquals(listOf("first", "second"), result.map { it.id })
    }

    @Test
    fun cyclicRepliesRemainVisibleAtRoot() {
        val first = reviewItem("first", "First", replyToId = "second")
        val second = reviewItem("second", "Second", replyToId = "first")

        val result = nestReviewReplies("root", listOf(first, second))

        assertEquals(listOf("first", "second"), result.map { it.id })
        assertTrue(result.all { it.replies.isEmpty() })
    }

    @Test
    fun resolvesReplyTargetFromActualParent() {
        val root = reviewItem("root", "Root")
        val parent = reviewItem("parent", "Parent", replyToId = "root")
        val child = reviewItem("child", "Child", replyToId = "parent")

        assertEquals("Parent", replyTargetName(child, parent))
        assertEquals("Root", replyTargetName(parent, root))
        assertNull(replyTargetName(child, root))
    }

    private fun reviewItem(
        id: String,
        name: String,
        replyToId: String? = null,
        replies: List<ReviewDetailItem> = emptyList()
    ) = ReviewDetailItem(
        id = id,
        avatar = null,
        name = name,
        badges = emptyList(),
        content = name,
        imageUrl = null,
        audioUrl = null,
        time = null,
        likeCount = null,
        replyCount = replies.size,
        replies = replies,
        replyToId = replyToId
    )
}
