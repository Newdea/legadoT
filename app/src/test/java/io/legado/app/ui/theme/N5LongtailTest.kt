package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertFalse
import org.junit.Test

/** N5 长尾收官 Wave A 清尸哨兵 */
class N5LongtailTest {
    @Test
    fun `vertical seekbar family retired`() {
        assertFalse("VerticalSeekBar.kt 应删除",
            ProjectSources.existsApp("src/main/java/io/legado/app/ui/widget/seekbar/VerticalSeekBar.kt"))
        assertFalse("VerticalSeekBarWrapper.kt 应删除",
            ProjectSources.existsApp("src/main/java/io/legado/app/ui/widget/seekbar/VerticalSeekBarWrapper.kt"))
        val attrs = ProjectSources.app("src/main/res/values/attrs.xml")
        assertFalse("VerticalSeekBar styleable 应删除", attrs.contains("name=\"VerticalSeekBar\""))
    }

    @Test
    fun `orphan layout and dead adapter methods removed`() {
        assertFalse("view_refresh_recycler.xml 孤儿应删除",
            ProjectSources.existsApp("src/main/res/layout/view_refresh_recycler.xml"))
        val adapter = ProjectSources.app("src/main/java/io/legado/app/base/adapter/DiffRecyclerAdapter.kt")
        assertFalse("setItem(position,item) 死方法应删除", adapter.contains("fun setItem(position: Int, item: ITEM)"))
        assertFalse("updateItem(item) 死方法应删除", adapter.contains("fun updateItem(item: ITEM)"))
        org.junit.Assert.assertTrue("updateItems(区间) 有活调用者须保留", adapter.contains("fun updateItems("))
    }

    @Test
    fun `motion tokens annotated and chapter adapter offset fixed`() {
        val motion = ProjectSources.app("src/main/java/io/legado/app/help/motion/MotionTokens.kt")
        org.junit.Assert.assertTrue("Spring.attr 应加 @param:AttrRes", motion.contains("@param:AttrRes"))
        org.junit.Assert.assertTrue("Spring.defStyle 应加 @param:StyleRes", motion.contains("@param:StyleRes"))
        val toc = ProjectSources.app("src/main/java/io/legado/app/ui/book/toc/ChapterListAdapter.kt")
        assertFalse("upDisplayTitles 不应裸用 notifyItemChanged(i, true)",
            toc.contains("notifyItemChanged(i, true)"))
    }

    @Test
    fun `template-repainted zombie card declaration removed, active kept`() {
        val openUrl = ProjectSources.app("src/main/res/layout/dialog_open_url_confirm.xml")
        assertFalse("open_url_confirm 根节点僵尸 shape_card_view 应删(模板重涂)",
            openUrl.contains("@drawable/shape_card_view"))
        // 474cf69a6: 关于页已试点迁 Compose, XML 只剩 TitleBar + ComposeView 外壳,
        // 卡片视觉改由 AboutScreen 的 Surface 承载, 断言目标随之迁移。
        val aboutXml = ProjectSources.app("src/main/res/layout/activity_about.xml")
        org.junit.Assert.assertTrue("关于页应挂 ComposeView", aboutXml.contains("ComposeView"))
        val aboutCompose = ProjectSources.repo("shared/src/commonMain/kotlin/io/legado/app/shared/about/AboutScreen.kt")
        org.junit.Assert.assertTrue("关于页 Compose 应保留圆角卡片视觉",
            aboutCompose.contains("Surface") && aboutCompose.contains("RoundedCornerShape"))
    }
}
