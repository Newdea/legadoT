package io.legado.app.ui.theme

import io.legado.app.ProjectSources
import org.junit.Assert.assertTrue
import org.junit.Test

/** 预测式返回哨兵:manifest 开关在+BaseActivity 不得有压制预测动画的无条件兜底回调 */
class PredictiveBackTest {

    @Test
    fun `manifest opts in and base activity has no blanket callback`() {
        val manifest = ProjectSources.app("src/main/AndroidManifest.xml")
        assertTrue(manifest.contains("android:enableOnBackInvokedCallback=\"true\""))
        val base = ProjectSources.app("src/main/java/io/legado/app/base/BaseActivity.kt")
        assertTrue(
            "BaseActivity 的无条件 finish 兜底会让所有普通页失去系统预测动画",
            !base.contains("onBackPressedDispatcher.addCallback")
        )
    }
}
