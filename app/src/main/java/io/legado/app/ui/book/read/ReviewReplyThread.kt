package io.legado.app.ui.book.read

import io.legado.app.help.nostr.ReviewDetailItem

/** Reparents a flat reply list using explicit parent IDs; unresolved and cyclic items stay visible at root. */
internal fun nestReviewReplies(
    rootId: String?,
    replies: List<ReviewDetailItem>
): List<ReviewDetailItem> {
    if (replies.isEmpty()) return replies
    val byId = replies.mapNotNull { item -> item.id?.let { it to item } }.toMap()
    val parentById = replies.mapNotNull { item ->
        item.id?.let { it to item.replyToId?.takeIf(String::isNotBlank) }
    }.toMap()
    val children = HashMap<String, MutableList<ReviewDetailItem>>()
    val roots = ArrayList<ReviewDetailItem>()

    replies.forEach { item ->
        val id = item.id
        val parentId = item.replyToId?.takeIf(String::isNotBlank)
        val nestUnder = parentId?.takeIf { candidate ->
            id != null && candidate != id && candidate != rootId && candidate in byId &&
                !createsCycle(id, candidate, parentById)
        }
        if (nestUnder != null) {
            children.getOrPut(nestUnder) { ArrayList() }.add(item)
        } else {
            roots.add(item)
        }
    }

    val visited = HashSet<String>()
    fun attach(item: ReviewDetailItem): ReviewDetailItem {
        val id = item.id ?: return item
        if (!visited.add(id)) return item.copy(replies = emptyList())
        val nested = children[id].orEmpty().map(::attach)
        return item.copy(replies = item.replies + nested)
    }
    return roots.map(::attach)
}

private fun createsCycle(
    childId: String,
    parentId: String,
    parentById: Map<String, String?>
): Boolean {
    val visited = HashSet<String>()
    var current: String? = parentId
    while (current != null && visited.add(current)) {
        if (current == childId) return true
        current = parentById[current]
    }
    return current != null
}

internal fun replyTargetName(
    reply: ReviewDetailItem,
    parent: ReviewDetailItem?
): String? {
    val parentId = parent?.id?.takeIf(String::isNotBlank) ?: return null
    if (reply.replyToId != parentId) return null
    return parent.name?.takeIf(String::isNotBlank)
}
