package io.legado.app.ui.book.source.edit

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class JsSourceEditLoginMenuTest {

    @Test
    fun loginMenuWiredToSourceLogin() {
        val menu = ProjectSources.app("src/main/res/menu/js_source_edit.xml")
        assertTrue("JS 源编辑器菜单应含登录项", menu.contains("menu_login"))
        val activity = ProjectSources.app(
            "src/main/java/io/legado/app/ui/book/source/edit/JsSourceEditActivity.kt"
        )
        assertTrue("登录分支应存在", activity.contains("R.id.menu_login"))
        assertTrue("登录应先落库再跳转(登录页读库内数据)", activity.contains("SourceLoginActivity"))
        assertTrue("无登录配置应有提示", activity.contains("js_source_no_login"))
    }

    @Test
    fun noLoginToastHasBothLocales() {
        val en = ProjectSources.app("src/main/res/values/strings.xml")
        val zh = ProjectSources.app("src/main/res/values-zh/strings.xml")
        assertTrue(en.contains("js_source_no_login"))
        assertTrue(zh.contains("js_source_no_login"))
    }
}
