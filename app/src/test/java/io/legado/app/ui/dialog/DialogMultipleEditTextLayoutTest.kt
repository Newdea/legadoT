package io.legado.app.ui.dialog

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

class DialogMultipleEditTextLayoutTest {

    @Test
    fun `multiple edit text dialog fields have readable touch size padding and spacing`() {
        val xml = ProjectSources.app("src/main/res/layout/dialog_multiple_edit_text.xml")

        assertTrue(xml.contains("android:layout_marginTop=\"12dp\""))
        assertTrue(xml.contains("android:minHeight=\"48dp\""))
        assertTrue(xml.contains("android:paddingStart=\"12dp\""))
        assertTrue(xml.contains("android:paddingEnd=\"12dp\""))
        assertTrue(xml.contains("android:paddingTop=\"6dp\""))
        assertTrue(xml.contains("android:paddingBottom=\"6dp\""))
        assertTrue(xml.contains("android:singleLine=\"true\""))
    }

}
