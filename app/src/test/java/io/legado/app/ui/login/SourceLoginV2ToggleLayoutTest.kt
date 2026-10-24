package io.legado.app.ui.login

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceLoginV2ToggleLayoutTest {

    @Test
    fun `v2 toggle row uses a Material switch`() {
        val xml = ProjectSources.app("src/main/res/layout/item_login_toggle.xml")

        assertTrue(xml.contains("com.google.android.material.materialswitch.MaterialSwitch"))
        assertTrue(xml.contains("android:id=\"@+id/toggle\""))
    }

}
