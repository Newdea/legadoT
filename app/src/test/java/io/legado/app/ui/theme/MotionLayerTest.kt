package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

/** N2 动效层哨兵:MotionTokens 单门约定(eink+系统减少动画)与依赖显式化 */
class MotionLayerTest {

    @Test
    fun `motion tokens exist with dual gate`() {
        val src = ProjectSources.app("src/main/java/io/legado/app/help/motion/MotionTokens.kt")
        assertTrue(src.contains("isEInkMode"))
        assertTrue(src.contains("areAnimatorsEnabled()"))
        assertTrue(src.contains("resolveThemeSpringForce"))
    }

    @Test
    fun `dynamicanimation is an explicit dependency`() {
        val toml = ProjectSources.repo("gradle/libs.versions.toml")
        assertTrue("直接使用的传递依赖必须显式声明", toml.contains("dynamicanimation"))
    }

    @Test
    fun `press spring wired to read menu action button`() {
        val helper = ProjectSources.app("src/main/java/io/legado/app/help/motion/PressSpringEffect.kt")
        assertTrue(helper.contains("MotionTokens.enabled"))
        assertTrue("不得吞事件,保 click 语义", helper.contains("return@setOnTouchListener false") || helper.contains("false // 不消费"))
        val btn = ProjectSources.app("src/main/java/io/legado/app/ui/widget/ReadMenuActionButton.kt")
        assertTrue(btn.contains("PressSpringEffect.attach"))
        assertTrue(ProjectSources.existsApp("src/main/java/io/legado/app/help/motion/ShapeMorph.kt"))
    }

    @Test
    fun `first screen stagger is gated and uses shared anim resources`() {
        assertTrue(ProjectSources.existsApp("src/main/res/anim/motion_layout_stagger.xml"))
        listOf(
            "src/main/java/io/legado/app/ui/main/bookshelf/style1/books/BooksFragment.kt",
            "src/main/java/io/legado/app/ui/main/bookshelf/style2/BookshelfFragment2.kt",
            "src/main/java/io/legado/app/ui/book/search/SearchActivity.kt",
        ).forEach { p ->
            val src = ProjectSources.app(p)
            assertTrue("$p 应门控接入 stagger", src.contains("motion_layout_stagger") && src.contains("MotionTokens.enabled"))
        }
    }

    @Test
    fun `detail enter transition is window level fade through`() {
        val info = ProjectSources.app("src/main/java/io/legado/app/ui/book/info/BookInfoActivity.kt")
        // e7348a607: 详情页转场由共享元素容器变换改为整窗 fade-through, 与共享元素彻底解耦,
        // 对所有进入路径(书架/搜索/裸跳转)统一生效, 与 ReadBookActivity 的 fade 同语系。
        assertTrue("详情页应挂窗口级 fade-through", info.contains("MaterialFadeThrough"))
        assertTrue("应配进入转场", info.contains("enterTransition"))
        assertTrue("应配返回转场", info.contains("returnTransition"))
        // 去重靠"移除"达成: 三个发射点不再各自构造转场, 共享发射扩展也随之删除。
        listOf(
            "src/main/java/io/legado/app/ui/main/bookshelf/style1/books/BooksFragment.kt",
            "src/main/java/io/legado/app/ui/main/bookshelf/style2/BookshelfFragment2.kt",
            "src/main/java/io/legado/app/ui/book/search/SearchActivity.kt",
        ).forEach { p ->
            val src = ProjectSources.app(p)
            assertTrue("$p 不应再各自做场景变换发射", !src.contains("makeSceneTransitionAnimation"))
            assertTrue("$p 不应再各自构造容器变换", !src.contains("MaterialContainerTransform"))
        }
        val ext = ProjectSources.app("src/main/java/io/legado/app/utils/ActivityExtensions.kt")
        assertTrue("共享发射扩展已随解耦移除", !ext.contains("fun Activity.startBookInfoTransition"))
    }

    @Test
    fun `reader launch carries gated fade options`() {
        val info = ProjectSources.app("src/main/java/io/legado/app/ui/book/info/BookInfoActivity.kt")
        assertTrue(info.contains("makeCustomAnimation"))
        assertTrue("launch 必须带 options 第二实参", info.contains("options,"))
        assertTrue(!info.contains("readBookResult.launch(intent)"))
    }
}
