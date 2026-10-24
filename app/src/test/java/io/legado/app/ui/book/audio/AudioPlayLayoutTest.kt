package io.legado.app.ui.book.audio

import io.legado.app.ProjectSources
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioPlayLayoutTest {

    @Test
    fun `audio play pause button icon tints from scheme onPrimary`() {
        // N3b:FAB 改 MaterialButton filled,图标走 colorOnPrimary,写死黑白退役
        val layoutXml = ProjectSources.app("src/main/res/layout/activity_audio_play.xml")

        assertTrue(layoutXml.contains("app:iconTint=\"?attr/colorOnPrimary\""))
        assertFalse(layoutXml.contains("@color/md_black_1000"))
        assertFalse(layoutXml.contains("@color/md_white_1000"))
    }

    @Test
    fun `audio speed menu offers presets and speed buttons have distinct descriptions`() {
        val menuXml = ProjectSources.app("src/main/res/menu/audio_play.xml")
        val activityKt = ProjectSources.app("src/main/java/io/legado/app/ui/book/audio/AudioPlayActivity.kt")

        assertTrue(menuXml.contains("@+id/menu_audio_speed"))
        assertTrue(menuXml.contains("@string/audio_play_speed"))
        assertTrue(activityKt.contains("AudioPlaySpeed.presets"))
        assertTrue(activityKt.contains("presets.getOrNull(index)?.let(AudioPlay::setSpeed)"))
        assertTrue(activityKt.contains("R.string.fast_forward"))
        assertTrue(activityKt.contains("R.string.fast_rewind"))
    }

    @Test
    fun `playback speed remains legible in the inline control`() {
        val layoutXml = ProjectSources.app("src/main/res/layout/activity_audio_play.xml")

        assertTrue(layoutXml.contains("android:id=\"@+id/tv_playback_speed\""))
        assertTrue(layoutXml.contains("android:textSize=\"16sp\""))
    }

    @Test
    fun `play and pause vector resources match their names`() {
        val playXml = ProjectSources.app("src/main/res/drawable/ic_play_24dp.xml")
        val pauseXml = ProjectSources.app("src/main/res/drawable/ic_pause_24dp.xml")

        assertTrue(playXml.contains("android:pathData=\"M8,5v14l11,-7z\""))
        assertTrue(pauseXml.contains("android:pathData=\"M6,19h4V5H6zM14,5v14h4V5z\""))
    }

    @Test
    fun `audio play pause floating action button stays circular to match loading indicator`() {
        val layoutXml = ProjectSources.app("src/main/res/layout/activity_audio_play.xml")
        val stylesXml = ProjectSources.app("src/main/res/values/styles.xml")

        assertTrue(
            layoutXml.contains("app:shapeAppearanceOverlay=\"@style/ShapeAppearanceLegadoAudioPlayFab\"")
        )
        assertTrue(stylesXml.contains("<style name=\"ShapeAppearanceLegadoAudioPlayFab\""))
        assertTrue(stylesXml.contains("<item name=\"cornerFamily\">rounded</item>"))
        assertTrue(stylesXml.contains("<item name=\"cornerSize\">50%</item>"))
    }

}
