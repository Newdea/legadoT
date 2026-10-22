package io.legado.app.ui.book.read

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReviewContentProtocolTest {

    @Test
    fun parseProtocol_preservesStringCountsAndReplyTarget() {
        val protocol = ReviewContentProtocol.parse(
            """{"text":"子评论","time":"刚刚","likeCount":"2","replyCount":"3","replyToId":"parent-1"}""",
            "https://example.test/chapter"
        )

        assertNotNull(protocol)
        assertEquals(2, protocol!!.likeCount)
        assertEquals(3, protocol.replyCount)
        assertEquals("parent-1", protocol.replyToId)
    }

    @Test
    fun displayedReplyCountDoesNotHideReportedOrAlreadyLoadedReplies() {
        assertEquals(12, displayedReplyCount(reportedCount = 12, loadedCount = 3))
        assertEquals(5, displayedReplyCount(reportedCount = 2, loadedCount = 5))
        assertEquals(4, displayedReplyCount(reportedCount = null, loadedCount = 4))
    }
}
