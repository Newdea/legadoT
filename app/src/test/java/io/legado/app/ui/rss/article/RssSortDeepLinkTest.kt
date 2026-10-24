package io.legado.app.ui.rss.article

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * `java.open("sort", url, title, origin)` 深链到订阅源指定分类的输入契约:
 * url/title 先经 [rssSortUrl] 组合成 sortUrl, 再由 [parseSortTabs] 解析成分类。
 */
class RssSortDeepLinkTest {

    @Test
    fun blankUrlFallsBackToFullSortList() {
        assertNull(rssSortUrl(null, "标题"))
        assertNull(rssSortUrl("", "标题"))
        assertNull(rssSortUrl("  ", "标题"))
        assertNull(parseSortTabs(null))
        assertNull(parseSortTabs("  "))
    }

    @Test
    fun urlWithoutTitleKeepsUnnamedSingleTab() {
        val url = "https://a.test/list"
        assertEquals(url, rssSortUrl(url, null))
        assertEquals(url, rssSortUrl(url, " "))
        assertEquals(listOf("" to url), parseSortTabs(url))
    }

    @Test
    fun titleWrapsUrlIntoTabName() {
        val sortUrl = rssSortUrl("https://a.test/list", "📖晋江书单")!!
        assertEquals(listOf("📖晋江书单" to "https://a.test/list"), parseSortTabs(sortUrl))
    }

    @Test
    fun jsonObjectPassesThroughForMultipleTabs() {
        val json = """{"书单一":"https://a.test/1","书单二":"https://a.test/2"}"""
        // url 本身是 JSON 时原样使用, title 不参与包装
        assertEquals(json, rssSortUrl(json, "被忽略的标题"))
        assertEquals(
            listOf("书单一" to "https://a.test/1", "书单二" to "https://a.test/2"),
            parseSortTabs(json)
        )
    }

    @Test
    fun malformedJsonFallsBackToPlainLink() {
        // isJsonObject 只判断首尾, 解析失败必须按普通链接兜底而不是抛异常
        assertEquals(listOf("" to "{oops}"), parseSortTabs("{oops}"))
        assertEquals(listOf("" to "{oops}"), parseSortTabs(rssSortUrl("{oops}", "标题")))
    }
}
