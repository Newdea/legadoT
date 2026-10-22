package io.legado.app.ui.book.read

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReviewSortPreferenceTest {

    @Test
    fun sortSwitchScrollsToTopAfterTheNewOrderIsApplied() {
        val dialog = readProjectFile("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")
        val adapter = readProjectFile("src/main/java/io/legado/app/base/adapter/RecyclerAdapter.kt")

        assertTrue("sort switch should request a top reset", dialog.contains("renderUiItems(scrollToTop = true)"))
        assertTrue("scroll reset should run after the recycler view applies its update", dialog.contains("binding.recyclerView.scrollToPosition(0)"))
        assertTrue("adapter should notify the caller after asynchronous list updates", adapter.contains("onUpdated?.invoke()"))
        assertTrue("stale diff results must not replace a newer list", adapter.contains("if (generation != diffGeneration) return@post"))
        assertTrue("the old review anchor restoration should be removed", !dialog.contains("ReviewScrollAnchor"))
    }

    @Test
    fun hotSortUsesLikeCountBeforeReplyCountAndTime() {
        val dialog = readProjectFile("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")

        assertTrue("hot sort should order by the current comment like count", dialog.contains("it.likeCount ?: 0"))
        assertTrue("hot sort should use reply count only as a tie breaker", dialog.contains("it.replyCount ?: it.replies.size"))
        assertTrue("hot sort should use time as the final tie breaker", dialog.contains("it.createdAt ?: Long.MIN_VALUE"))
        assertTrue("hot sort should not interleave the newest comments", !dialog.contains("SORT_BATCH_SIZE"))
        assertTrue("hot sort should not use subtree score", !dialog.contains("subtreeScore"))
    }

    @Test
    fun reviewSortModeIsLoadedAndWrittenThroughDefaultPreferences() {
        val source = readProjectFile("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")

        assertTrue(
            "review dialog should restore the saved sort mode",
            source.contains("getPrefInt(PREF_REVIEW_SORT_MODE)")
        )
        assertTrue(
            "review dialog should persist sort mode changes",
            source.contains("context?.putPrefInt(PREF_REVIEW_SORT_MODE, value)")
        )
        assertTrue(
            "invalid or missing values should fall back to hot sorting",
            source.contains("takeIf { it == SORT_NEW } ?: SORT_HOT")
        )
    }

    private fun readProjectFile(path: String): String {
        val candidates = listOf(File(path), File("app/$path"))
        return candidates.firstOrNull { it.isFile }?.readText().orEmpty()
    }
}
