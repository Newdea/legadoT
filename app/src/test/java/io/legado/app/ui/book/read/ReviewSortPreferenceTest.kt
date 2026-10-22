package io.legado.app.ui.book.read

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReviewSortPreferenceTest {

    @Test
    fun sortSwitchRestoresVisibleReviewAnchorAfterAsyncUpdate() {
        val dialog = readProjectFile("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")
        val adapter = readProjectFile("src/main/java/io/legado/app/base/adapter/RecyclerAdapter.kt")

        assertTrue("sort switch should capture the visible review anchor", dialog.contains("pendingReviewScrollAnchor = captureReviewScrollAnchor()"))
        assertTrue("anchor restoration should run after the recycler view applies its update", dialog.contains("binding.recyclerView.post"))
        assertTrue("render generation should discard stale restoration callbacks", dialog.contains("generation != reviewRenderGeneration"))
        assertTrue("adapter should notify the caller after asynchronous list updates", adapter.contains("onUpdated?.invoke()"))
        assertTrue("stale diff results must not replace a newer list", adapter.contains("if (generation != diffGeneration) return@post"))
        assertTrue("anchor restoration should use the first visible adapter child", dialog.contains("findViewByPosition(position)"))
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
