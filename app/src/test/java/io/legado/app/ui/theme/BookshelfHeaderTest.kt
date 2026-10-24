package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

/** 书架标题形态哨兵:TitleBar 小标题(与发现/我的统一),统计/续读为标题栏下固定内容行 */
class BookshelfHeaderTest {

    @Test
    fun `shared header keeps stats and continue row without display title`() {
        val header = ProjectSources.app("src/main/res/layout/view_bookshelf_header.xml")
        assertTrue(listOf(
            "@+id/tv_shelf_stats", "@+id/continue_reading",
            "@+id/tv_continue_name", "@+id/tv_continue_chapter", "@+id/tv_continue_percent",
        ).all { header.contains(it) })
        assertTrue("Display 大标题已退役", !header.contains("tv_shelf_title"))
        val ext = ProjectSources.repo("shared/src/commonMain/kotlin/io/legado/app/help/book/BookExtensions.kt")
        assertTrue("readProgress 扩展", ext.contains("fun Book.readProgress"))
    }

    @Test
    fun `bookshelf1 uses titlebar with embedded tabs`() {
        val xml = ProjectSources.app("src/main/res/layout/fragment_bookshelf1.xml")
        assertTrue("TitleBar 回归", xml.contains("TitleBar"))
        assertTrue("tabs 回 TitleBar 内嵌", xml.contains("view_tab_layout_min"))
        assertTrue(
            "可收起大标题已退役",
            !xml.contains("CollapsingToolbarLayout") && !xml.contains("AppBarLayout")
        )
        assertTrue("保留行挂接", xml.contains("@+id/shelf_header"))
        val base = ProjectSources.app("src/main/java/io/legado/app/ui/main/bookshelf/BaseBookshelfFragment.kt")
        assertTrue("头部逻辑一处实现", base.contains("fun bindShelfHeader") && base.contains("fun refreshShelfHeader"))
        assertTrue("AppBar 展开闸门已退役", !base.contains("appBarExpanded"))
    }

    @Test
    fun `bookshelf2 uses titlebar with dynamic title`() {
        val xml = ProjectSources.app("src/main/res/layout/fragment_bookshelf2.xml")
        assertTrue("TitleBar 回归", xml.contains("TitleBar"))
        assertTrue(
            "可收起大标题已退役",
            !xml.contains("CollapsingToolbarLayout") && !xml.contains("AppBarLayout")
        )
        assertTrue("保留行挂接", xml.contains("@+id/shelf_header"))
        val frag = ProjectSources.app("src/main/java/io/legado/app/ui/main/bookshelf/style2/BookshelfFragment2.kt")
        assertTrue("动态标题回 TitleBar", frag.contains("titleBar.title"))
    }

    @Test
    fun `tab layout min restored`() {
        val xml = ProjectSources.app("src/main/res/layout/view_tab_layout_min.xml")
        assertTrue(xml.contains("@+id/tab_layout") && xml.contains("TabLayout"))
    }

    @Test
    fun `items carry read progress except group variants`() {
        assertTrue(ProjectSources.app("src/main/res/layout/item_bookshelf_grid.xml").contains("@+id/pb_read_progress"))
        val list = ProjectSources.app("src/main/res/layout/item_bookshelf_list.xml")
        assertTrue(list.contains("@+id/pb_read_progress") && list.contains("@+id/tv_read_percent"))
        assertTrue(!ProjectSources.app("src/main/res/layout/item_bookshelf_grid_group.xml").contains("pb_read_progress"))
        assertTrue(!ProjectSources.app("src/main/res/layout/item_bookshelf_list_group.xml").contains("pb_read_progress"))
    }
}
