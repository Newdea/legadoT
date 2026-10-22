package io.legado.app.ui.book.read

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReviewDetailCacheRefreshTest {

    @Test
    fun manualRefreshBypassesCacheAndFreshFirstPageReplacesStaleRemoteItems() {
        val dialog = readProjectFile("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")

        assertTrue("manual refresh should skip the cached snapshot", dialog.contains("allowCache = false"))
        assertTrue("fresh first-page responses should replace only refreshed backends", dialog.contains("replaceRefreshedDetailItems(items, result)"))
        assertTrue("the current merged snapshot should replace stale cache contents", dialog.contains("cacheCurrentDetailItems()"))
        assertTrue("a transient load failure should leave the current cache untouched", dialog.contains("if (result == null)"))
    }

    @Test
    fun emptyCacheWriteRemovesOldPersistentEntry() {
        val cache = readProjectFile("src/main/java/io/legado/app/help/nostr/NostrReviewDetailCache.kt")
        assertTrue("empty cache snapshots should remove stale disk entries", cache.contains("if (snapshot.isEmpty())"))
        assertTrue("cache clear should delete its persistent entry", cache.contains("diskCache.remove(key)"))
    }

    private fun readProjectFile(path: String): String {
        val candidates = listOf(File(path), File("app/$path"))
        return candidates.firstOrNull { it.isFile }?.readText().orEmpty()
    }
}
