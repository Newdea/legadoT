package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Task 9 收敛哨兵：散装横向 SeekBar → Slider（5 布局 + Task 8 的 view_detail_seek_bar）。
 *
 * activity_audio_play.xml 的 SeekBar 已由 N3b 整页重做迁为 Slider（本类下方断言其已去）。
 * view_read_menu.xml 的垂直亮度条采用 VerticalSliderWrapper
 */
class SliderMigrationTest {
    @Test
    fun `scoped layouts carry no raw seekbar`() {
        listOf(
            "view_detail_seek_bar.xml", "dialog_read_aloud.xml", "dialog_read_bg_text.xml",
            "popup_seek_bar.xml", "view_manga_menu.xml", "view_read_menu.xml",
        ).forEach { name ->
            val xml = ProjectSources.app("src/main/res/layout/$name")
            assertTrue("$name 仍有裸 SeekBar", !xml.contains("<SeekBar"))
        }
    }
    @Test
    fun `audio play migrated to slider in n3b`() {
        // N3b 整页重做已落地:进度条由裸 SeekBar 迁至 Slider
        val xml = ProjectSources.app("src/main/res/layout/activity_audio_play.xml")
        assertTrue(!xml.contains("<SeekBar"))
        assertTrue(xml.contains("slider.Slider"))
    }
}
