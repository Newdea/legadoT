package io.legado.app.data.entities

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class BookSourcePartHasJsTest {

    private val partSource =
        ProjectSources.app("src/main/java/io/legado/app/data/entities/BookSourcePart.kt")

    @Test
    fun databaseViewExposesHasJs() {
        assertTrue("视图 SQL 应含 hasJs 计算列", partSource.contains("hasJs"))
        assertTrue("hasJs 应按 mainJs 非空白派生", partSource.contains("trim(mainJs)"))
    }
}
