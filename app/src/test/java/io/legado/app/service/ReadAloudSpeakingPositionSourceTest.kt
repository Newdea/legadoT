package io.legado.app.service

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadAloudSpeakingPositionSourceTest {

    @Test
    fun `service records live spoken char position through upTtsProgress`() {
        val serviceKt = ProjectSources.app("src/main/java/io/legado/app/service/BaseReadAloudService.kt")

        // The service is the durable source of truth for the current spoken char position,
        // so a recreated Activity can jump back even before observing a TTS_PROGRESS event.
        assertTrue(serviceKt.contains("var readAloudChapterStart"))
        assertTrue(serviceKt.contains("fun upTtsProgress(progress: Int)"))
        assertTrue(serviceKt.contains("readAloudChapterStart = progress"))
    }

    @Test
    fun `read aloud facade exposes spoken char position`() {
        val readAloudKt = ProjectSources.app("src/main/java/io/legado/app/model/ReadAloud.kt")

        assertTrue(readAloudKt.contains("val readAloudChapterStart"))
        assertTrue(readAloudKt.contains("BaseReadAloudService.readAloudChapterStart"))
    }

    @Test
    fun `back to speaking position prefers observer cache and falls back to service position`() {
        val activityKt = ProjectSources.app("src/main/java/io/legado/app/ui/book/read/ReadBookActivity.kt")
            .replace(Regex("\\s+"), " ")

        // Lock the precedence: observer cache first, service live position as fallback.
        // Flipping the operands (service value primary) must fail this test.
        assertTrue(
            activityKt.contains(
                "lastReadAloudChapterStart.takeIf { it >= 0 } ?: ReadAloud.readAloudChapterStart"
            )
        )
    }

}
