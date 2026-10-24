package io.legado.app.service

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadAloudFollowPositionRegressionTest {

    @Test
    fun contentLoadFinishPreservesAttachedSpeechNavigationWhenSpeechServiceIsRunning() {
        val readBookKt = ProjectSources.app("src/main/java/io/legado/app/model/ReadBook.kt")

        assertTrue(
            readBookKt.contains("syncReadAloudFollow = BaseReadAloudService.shouldSyncSpeechNavigation()")
        )
        assertTrue(readBookKt.contains("private fun curPageChanged("))
        assertTrue(readBookKt.contains("pageChanged: Boolean = false,"))
        assertTrue(readBookKt.contains("syncReadAloudFollow: Boolean = false"))
    }

    @Test
    fun speechSyncGatingPreservesManualNavigationDetachAndDetachedVisiblePageBehavior() {
        val readBookKt = ProjectSources.app("src/main/java/io/legado/app/model/ReadBook.kt")
        val serviceKt = ProjectSources.app("src/main/java/io/legado/app/service/BaseReadAloudService.kt")

        assertTrue(readBookKt.contains("if (BaseReadAloudService.isRun && it.isCompleted)"))
        assertTrue(readBookKt.contains("if (!syncReadAloudFollow)"))
        assertTrue(readBookKt.contains("curPageChanged(syncReadAloudFollow = syncReadAloudFollow)"))
        assertTrue(serviceKt.contains("fun shouldSyncSpeechNavigation(): Boolean"))
        assertTrue(serviceKt.contains("return speechFollowState.shouldSyncSpeechNavigation()"))
    }

    @Test
    fun mediaChapterSkipUsesReadAloudServiceCommandsWhileTtsIsRunning() {
        val receiverKt = ProjectSources.app("src/main/java/io/legado/app/receiver/MediaButtonReceiver.kt")
        val readAloudKt = ProjectSources.app("src/main/java/io/legado/app/model/ReadAloud.kt")

        assertTrue(receiverKt.contains("ReadAloud.prevChapter(context)"))
        assertTrue(receiverKt.contains("ReadAloud.nextChapter(context)"))
        assertTrue(readAloudKt.contains("fun prevChapter(context: Context)"))
        assertTrue(readAloudKt.contains("intent.action = IntentAction.prev"))
        assertTrue(readAloudKt.contains("fun nextChapter(context: Context)"))
        assertTrue(readAloudKt.contains("intent.action = IntentAction.next"))
    }

}
