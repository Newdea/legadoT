package io.legado.app.ui.login

import io.legado.app.ProjectSources
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceLoginWebViewLayoutTest {

    @Test
    fun `source login text field accepts multiple lines`() {
        val xml = ProjectSources.app("src/main/res/layout/item_login_field.xml")

        assertFalse(
            "Source login text fields must allow newline input",
            xml.contains("android:maxLines=\"1\"")
        )
        assertTrue(
            "Source login text fields must expose a multiline IME enter key",
            xml.contains("android:inputType=\"textMultiLine\"")
        )
    }

    @Test
    fun `web view login fragment paints runtime theme background inside transparent login activity`() {
        val xml = ProjectSources.app("src/main/res/layout/fragment_web_view_login.xml")
        val kt = ProjectSources.app("src/main/java/io/legado/app/ui/login/WebViewLoginFragment.kt")

        assertFalse(
            "WebView login must not hard-code @color/background because user-selected theme colors live in ThemeStore",
            xml.contains("android:background=\"@color/background\"")
        )
        assertTrue(
            "WebView login must paint an opaque runtime theme background so SourceLoginActivity's transparent theme cannot reveal the previous page toolbar underneath",
            kt.contains("binding.root.setBackgroundColor(requireContext().backgroundColor)")
        )
    }

}
