package io.legado.app.ui.main.explore

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class ExploreModeCompatibilityTest {

    @Test
    fun `legacy mode is a real discover page selected by the preference`() {
        val key = ProjectSources.app("src/main/java/io/legado/app/constant/PreferKey.kt")
        val config = ProjectSources.app("src/main/java/io/legado/app/help/config/AppConfig.kt")
        val settings = ProjectSources.app("src/main/res/xml/pref_config_other.xml")
        val main = ProjectSources.app("src/main/java/io/legado/app/ui/main/MainActivity.kt")
        val legacy = ProjectSources.app("src/main/java/io/legado/app/ui/main/explore/LegacyExploreFragment.kt")
        val layout = ProjectSources.app("src/main/res/layout/fragment_explore_legacy.xml")
        val row = ProjectSources.app("src/main/res/layout/item_find_book.xml")

        assertTrue(key.contains("const val legacyExplore = \"legacyExplore\""))
        assertTrue(config.contains("getPrefBoolean(PreferKey.legacyExplore, false)"))
        assertTrue(settings.contains("android:key=\"legacyExplore\""))
        assertTrue(settings.contains("android:defaultValue=\"false\""))
        assertTrue(main.contains("if (AppConfig.legacyExplore) idLegacyExplore else idExplore"))
        assertTrue(main.contains("idLegacyExplore -> LegacyExploreFragment(position)"))
        assertTrue(legacy.contains("appDb.bookSourceDao.flowExplore()"))
        assertTrue(legacy.contains("source.exploreKinds()"))
        assertTrue(layout.contains("@layout/view_search"))
        assertTrue(row.contains("@+id/flexbox"))
    }

}
