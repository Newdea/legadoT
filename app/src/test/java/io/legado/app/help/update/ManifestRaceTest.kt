package io.legado.app.help.update

import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ManifestRaceTest {

    @Test
    fun manifestUrls_includeAllConfiguredWorkerDomains() {
        assertEquals(
            listOf(
                "https://skybook.qzz.io/legado-download/update.json",
                "https://skybook1.top/legado-download/update.json",
                "https://skybook.de5.net/legado-download/update.json"
            ),
            AppUpdateGitHub.manifestUrls
        )
    }

    @Test
    fun firstAvailableManifest_returnsFirstUsableAndCancelsRemainingRequests() = runBlocking {
        val unavailableFinished = CompletableDeferred<Unit>()
        val requestsStarted = CompletableDeferred<Unit>()
        val startedCount = AtomicInteger()
        var slowCancelled = false
        val updateInfo = AppUpdate.UpdateInfo(
            tagName = "3.26.101920",
            updateLog = "notes",
            downloadUrl = "https://example.test/app.apk",
            fileName = "app.apk"
        )

        val result = firstAvailableManifest(listOf("unavailable", "winner", "slow")) { url ->
            if (startedCount.incrementAndGet() == 3) requestsStarted.complete(Unit)
            requestsStarted.await()
            when (url) {
                "unavailable" -> {
                    unavailableFinished.complete(Unit)
                    throw java.io.IOException("temporary network failure")
                }
                "winner" -> {
                    unavailableFinished.await()
                    UpdateManifestResult.HasUpdate(updateInfo)
                }
                else -> {
                    try {
                        awaitCancellation()
                    } finally {
                        slowCancelled = true
                    }
                }
            }
        }

        assertEquals(UpdateManifestResult.HasUpdate(updateInfo), result)
        assertTrue(slowCancelled)
    }

    @Test
    fun firstAvailableManifest_acceptsNoUpdateAsAValidResult() = runBlocking {
        val result = firstAvailableManifest(listOf("worker")) {
            UpdateManifestResult.NoUpdate
        }

        assertEquals(UpdateManifestResult.NoUpdate, result)
    }

    @Test
    fun firstAvailableManifest_returnsUnavailableWhenEveryRequestFails() = runBlocking {
        val result = firstAvailableManifest(listOf("first", "second")) { url ->
            UpdateManifestResult.Unavailable("$url failed")
        }

        assertTrue(result is UpdateManifestResult.Unavailable)
    }

    @Test
    fun firstAvailableManifest_treatsRequestTimeoutAsUnavailable() = runBlocking {
        val result = firstAvailableManifest(listOf("timed-out")) {
            throw CancellationException("request timed out")
        }

        assertTrue(result is UpdateManifestResult.Unavailable)
    }

    @Test
    fun firstAvailableManifest_propagatesParentCancellation() = runBlocking {
        val requestStarted = CompletableDeferred<Unit>()
        val request = launch {
            firstAvailableManifest(listOf("pending")) {
                requestStarted.complete(Unit)
                awaitCancellation()
            }
        }

        requestStarted.await()
        request.cancelAndJoin()
        assertTrue(request.isCancelled)
    }

    @Test
    fun firstAvailableManifest_handlesNoConfiguredUrls() = runBlocking {
        val result = firstAvailableManifest(emptyList()) {
            error("fetch should not run")
        }

        assertTrue(result is UpdateManifestResult.Unavailable)
    }
}
