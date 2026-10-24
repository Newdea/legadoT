package io.legado.app.help

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextSelectMenuConfigTest {

    @Test
    fun defaultHasExpectedPartition() {
        val c = TextSelectMenuConfig.default()
        assertEquals(listOf("replace", "copy", "bookmark", "highlight", "aloud"), c.bar)
        assertEquals(listOf("dict", "search", "browser", "share", "review"), c.more)
        // 新增键必须同时进 ALL_KEYS 与默认分区, 漏一处老用户升级后就看不到新入口
        assertEquals(TextSelectMenuConfig.ALL_KEYS.toSet(), (c.bar + c.more).toSet())
    }

    @Test
    fun jsonRoundTrip() {
        val c = TextSelectMenuConfig(listOf("copy", "highlight"), listOf("share"))
        assertEquals(c, TextSelectMenuConfig.fromJson(c.toJson()))
    }

    @Test
    fun fromJsonBlankReturnsDefault() {
        assertEquals(TextSelectMenuConfig.default(), TextSelectMenuConfig.fromJson(null))
        assertEquals(TextSelectMenuConfig.default(), TextSelectMenuConfig.fromJson("   "))
    }

    @Test
    fun fromJsonMalformedReturnsDefault() {
        assertEquals(TextSelectMenuConfig.default(), TextSelectMenuConfig.fromJson("{not json"))
    }

    @Test
    fun fromJsonMissingFieldTolerated() {
        val c = TextSelectMenuConfig.fromJson("{\"bar\":[\"copy\"]}")
        assertEquals(listOf("copy"), c.bar)
        assertEquals(emptyList<String>(), c.more)
    }

    @Test
    fun normalizedDropsUnknownKeys() {
        val c = TextSelectMenuConfig(listOf("copy", "bogus"), listOf("share")).normalized()
        assertTrue("bogus" !in c.bar && "bogus" !in c.more)
        assertEquals(listOf("copy"), c.bar)
    }

    @Test
    fun normalizedDedupsAcrossZones() {
        val c = TextSelectMenuConfig(listOf("copy"), listOf("copy", "share")).normalized()
        assertEquals(listOf("copy"), c.bar)
        assertEquals(0, c.more.count { it == "copy" })
    }

    @Test
    fun normalizedAppendsMissingKnownKeysToMore() {
        val c = TextSelectMenuConfig(listOf("copy"), listOf("share")).normalized()
        val all = c.bar + c.more
        assertEquals(TextSelectMenuConfig.ALL_KEYS.toSet(), all.toSet())
        // 用常量推导而非硬编码数字, 以后加键不用再改这里
        assertEquals(TextSelectMenuConfig.ALL_KEYS.size, all.size)
        assertTrue(
            c.more.containsAll(
                listOf(
                    "replace", "bookmark", "highlight", "aloud",
                    "dict", "search", "browser", "review"
                )
            )
        )
    }

    @Test
    fun migrateFromTruePutsAllInBar() {
        val c = TextSelectMenuConfig.migrateFrom(true)
        assertEquals(TextSelectMenuConfig.ALL_KEYS, c.bar)
        assertEquals(emptyList<String>(), c.more)
    }

    @Test
    fun migrateFromFalseReturnsDefault() {
        assertEquals(TextSelectMenuConfig.default(), TextSelectMenuConfig.migrateFrom(false))
    }
}
