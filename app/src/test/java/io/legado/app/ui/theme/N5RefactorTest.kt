package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * N5 Wave C 重构收敛哨兵(行为等价, 验证共享物+去重)。
 *
 * 临时哨兵: 由 N5 各 Wave 的重构提交引入, 用于在重构进行中/刚收尾时挡住旧模式回潮。
 * 按 docs/adr/0004-sentinel-source-tests.md 的约定, 单个测试要么改写成"不变量"形式长期保留,
 * 要么在所押守的重构被后续架构取代时删除——不允许留着断言一个已经消失的实现细节
 * (e7348a607 改详情页转场后忘记删/改哨兵, 曾把 master 弄红)。
 */
class N5RefactorTest {
    @Test
    fun `detail seekbar step buttons share one clamp instead of inlining their own`() {
        val src = ProjectSources.app("src/main/java/io/legado/app/ui/widget/DetailSeekBar.kt")
        // 不变量: 加减钮不得各自内联钳制(两侧边界行为会漂移), 必须走同一个步进方法。
        // 不断言方法名——换成任何实现, 这条都应成立。
        val plus = src.lineSequence().firstOrNull { it.contains("ivSeekPlus.setOnClickListener") }
        val reduce = src.lineSequence().firstOrNull { it.contains("ivSeekReduce.setOnClickListener") }
        assertTrue("DetailSeekBar 应保留加减钮的点击监听", plus != null && reduce != null)
        assertTrue("加钮不得内联自己的钳制", !plus!!.contains("coerceIn"))
        assertTrue("减钮不得内联自己的钳制", !reduce!!.contains("coerceIn"))
        val calls = listOf(plus, reduce).map {
            Regex("""\{\s*(\w+)\(""").find(it!!)?.groupValues?.get(1)
        }
        assertTrue("加减钮应走同一个步进方法", calls[0] != null && calls[0] == calls[1])
        // value 的钳制实现全类只许两份: progress setter 与步进方法
        assertEquals(
            "seekBar value 钳制只应有两份(setter + 步进)",
            2,
            Regex("""coerceIn\(binding\.seekBar\.valueFrom""").findAll(src).count()
        )
    }

    @Test
    fun `edit adapter tag1 listener reads current safety not frozen`() {
        listOf(
            "book/source/edit/BookSourceEditAdapter",
            "rss/source/edit/RssSourceEditAdapter",
        ).forEach {
            val src = ProjectSources.app("src/main/java/io/legado/app/ui/$it.kt")
            // 修复后:attach 回调内重算而非用外层冻结的 isUnsafeText
            assertTrue("$it 应每次 attach 重读安全态",
                src.contains("EditSafety.isCombiningHeavy") &&
                    src.contains("onViewAttachedToWindow"))
        }
    }

    // 封面转场发射的去重哨兵已移至 MotionLayerTest: e7348a607 把详情页转场改成窗口级
    // fade-through 后, 去重靠"移除各发射点代码"达成, 原"共享发射扩展"断言失去对象,
    // 由 MotionLayerTest 的 fade-through 守卫接管, 不在此重复断言同一处源码。

    @Test
    fun `ambient background returns cancellable job`() {
        val ext = ProjectSources.app("src/main/java/io/legado/app/utils/AmbientBackground.kt")
        assertTrue("applyAmbientBackground 应返回 Job", ext.contains("): Job"))
        val info = ProjectSources.app("src/main/java/io/legado/app/ui/book/info/BookInfoActivity.kt")
        assertTrue("BookInfoActivity 应持 job 取消", info.contains("ambientJob"))
    }

    @Test
    fun `readrss uses modern insets not flag_fullscreen`() {
        val src = ProjectSources.app("src/main/java/io/legado/app/ui/rss/read/ReadRssActivity.kt")
        assertTrue("onConfigurationChanged 不应再用 FLAG_FULLSCREEN 切换",
            !src.contains("FLAG_FULLSCREEN") || src.contains("Type.statusBars()"))
    }

    @Test
    fun `book info manage rows are declared once in the shared include`() {
        // 不变量: 五个管理行 id 只许在共享布局里声明一份, 三个入口布局都不得内联
        // (逐字重复会漂移); 同时共享布局必须真的被 include, 否则"无内联"会变成整块丢失。
        val ids = listOf("tv_origin", "tv_change_source", "tv_lasted", "tv_group", "tv_change_group")
        listOf(
            "src/main/res/layout/activity_book_info.xml",
            "src/main/res/layout-land/activity_book_info.xml",
            "src/main/res/layout/item_book_info_header.xml",
        ).forEach { path ->
            val xml = ProjectSources.app(path)
            ids.forEach { id ->
                assertFalse("$path 不得内联声明 @+id/$id(应经共享 include)", xml.contains("@+id/$id"))
            }
        }
        val shared = ProjectSources.app("src/main/res/layout/view_book_info_manage_rows.xml")
        ids.forEach { id ->
            assertTrue("共享布局应声明 @+id/$id", shared.contains("@+id/$id"))
        }
        val header = ProjectSources.app("src/main/res/layout/item_book_info_header.xml")
        assertTrue("头卡应 include 共享管理行", header.contains("""layout="@layout/view_book_info_manage_rows""""))
    }
}
