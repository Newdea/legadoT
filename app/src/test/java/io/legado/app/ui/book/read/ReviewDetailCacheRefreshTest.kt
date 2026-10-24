package io.legado.app.ui.book.read

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewDetailCacheRefreshTest {

    @Test
    fun manualRefreshBypassesCacheAndFreshFirstPageReplacesStaleRemoteItems() {
        val dialog = ProjectSources.app("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")

        assertTrue("manual refresh should skip the cached snapshot", dialog.contains("allowCache = false"))
        assertTrue("fresh first-page responses should replace only refreshed backends", dialog.contains("replaceRefreshedDetailItems(items, result)"))
        assertTrue("the current merged snapshot should replace stale cache contents", dialog.contains("cacheCurrentDetailItems()"))
        assertTrue("a transient load failure should leave the current cache untouched", dialog.contains("if (result == null)"))
    }

    @Test
    fun cachedNostrProfilesAreAppliedBeforeRefreshingAndAllMissingAvatarsKeepTheirSlot() {
        val dialog = ProjectSources.app("src/main/java/io/legado/app/ui/book/read/ReviewDetailDialog.kt")
        val applyCached = dialog.indexOf("if (applyCachedProfiles()) renderUiItems()")
        val fetchProfiles = dialog.indexOf("NostrProfileStore.sync(authors)")

        assertTrue("cached profiles should be applied before network profile sync", applyCached >= 0 && fetchProfiles > applyCached)
        assertTrue("missing avatars from every source should show a person placeholder", dialog.contains("ImageLoader.load(context, R.drawable.ic_review_avatar_placeholder)"))
        assertTrue("missing-avatar branch should not hide the avatar view", dialog.contains("else -> {\n                    binding.ivAvatar.scaleType = ImageView.ScaleType.CENTER_INSIDE"))
        assertTrue("failed avatar URLs should fall back to the same placeholder", dialog.contains(".error(R.drawable.ic_review_avatar_placeholder)"))
        assertTrue("the placeholder should be a visible dedicated asset", ProjectSources.existsApp("src/main/res/drawable/ic_review_avatar_placeholder.xml"))
    }

    @Test
    fun emptyCacheWriteRemovesOldPersistentEntry() {
        val cache = ProjectSources.app("src/main/java/io/legado/app/help/nostr/NostrReviewDetailCache.kt")
        assertTrue("empty cache snapshots should remove stale disk entries", cache.contains("if (snapshot.isEmpty())"))
        assertTrue("cache clear should delete its persistent entry", cache.contains("diskCache.remove(key)"))
    }

}
