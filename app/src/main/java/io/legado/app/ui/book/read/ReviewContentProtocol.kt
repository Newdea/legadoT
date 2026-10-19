package io.legado.app.ui.book.read

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import io.legado.app.utils.NetworkUtils

internal data class ReviewContentProtocol(
    val text: String?,
    val imageUrl: String?,
    val audioUrl: String?,
    val time: String?,
    val likeCount: Int?,
    val replyCount: Int?
) {
    companion object {
        fun parse(raw: String?, baseUrl: String): ReviewContentProtocol? {
            val value = raw?.trim().orEmpty()
            if (value.isEmpty() || !value.startsWith("{") || !value.endsWith("}")) return null
            val obj = runCatching { JsonParser.parseString(value).asJsonObject }.getOrNull() ?: return null
            val text = obj.stringValue("text")
            val imageUrl = obj.stringValue("img")?.let { NetworkUtils.getAbsoluteURL(baseUrl, it) }
            val audioUrl = obj.stringValue("audio")?.let { NetworkUtils.getAbsoluteURL(baseUrl, it) }
            val time = obj.stringValue("time")
            val likeCount = obj.get("likeCount").toProtocolInt()
            val replyCount = obj.get("replyCount").toProtocolInt()
            if (text == null && imageUrl == null && audioUrl == null &&
                time == null && likeCount == null && replyCount == null
            ) return null
            return ReviewContentProtocol(text, imageUrl, audioUrl, time, likeCount, replyCount)
        }

        private fun com.google.gson.JsonObject.stringValue(key: String): String? =
            get(key)?.takeUnless { it.isJsonNull }?.let { runCatching { it.asString.trim() }.getOrNull() }
                ?.ifEmpty { null }

        private fun JsonElement?.toProtocolInt(): Int? {
            if (this == null || this is JsonNull) return null
            val raw = runCatching { asString.trim() }.getOrNull() ?: return null
            return raw.toIntOrNull() ?: raw.toDoubleOrNull()?.toInt()
        }
    }
}

internal fun displayedReplyCount(reportedCount: Int?, loadedCount: Int): Int? =
    maxOf(reportedCount ?: 0, loadedCount).takeIf { it > 0 }
