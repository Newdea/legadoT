package io.legado.app

import java.io.File

/**
 * JVM 单测读取本项目源码/资源的统一入口。
 *
 * ## 为什么需要它
 * 架构哨兵/迁移锚/资源规范一类测试要读源码文本, 这些读取曾经散落在 58 个测试文件里:
 * 34 个文件各自私有实现了同一个 `readProjectFile()`(其中十余个逐字复制
 * `listOf(File(path), File("app/$path"))` 的兜底), 且大多以 `.orEmpty()` 收尾——
 * 路径拼错时拿到空串, 报出来的是 "should contain X" 而不是 "文件不存在", 排查全靠猜。
 * 同一个文件还会被重复读取。
 *
 * ## 工作目录
 * JVM 单测的 cwd 取决于驱动方式: Gradle CLI/Android Studio 下通常是 app 模块根,
 * 从仓库根驱动时是仓库根。所以 app 内路径两个候选都试, 调用方不需要关心自己在哪种模式下跑。
 *
 * ## 约定
 * - app 模块内(源码/res/assets)用 [app], 路径相对 app 模块根, 如 `"src/main/res/layout/a.xml"`
 * - 仓库内其它位置(shared/modules/gradle)用 [repo], 路径相对仓库根, 如 `"shared/src/commonMain/..."`
 * - 读不到文件时抛异常并带上 cwd 与候选路径, 不要让断言去猜
 *
 * @see docs/adr/0004-sentinel-source-tests.md
 */
object ProjectSources {

    private val contentCache = HashMap<String, String>()

    /** 读 app 模块内的源码/资源, [path] 相对 app 模块根。 */
    fun app(path: String): String = contentCache.getOrPut("app|$path") {
        appFile(path).readText()
    }

    /** 读仓库内其它位置, [path] 相对仓库根(shared/modules/gradle 等)。 */
    fun repo(path: String): String = contentCache.getOrPut("repo|$path") {
        repoFile(path).readText()
    }

    /**
     * 取 app 模块内文件的 [File] 句柄, 供需要 File 的场景用(DocumentBuilder 解析、
     * walkTopDown 遍历目录等)。目录同样适用。
     */
    fun appFile(path: String): File =
        firstExisting(File(path), File("app", path), exists = true)
            ?: error(notFound("app 模块", path))

    /** 取仓库内文件的 [File] 句柄, [path] 相对仓库根。 */
    fun repoFile(path: String): File =
        firstExisting(File("..", path), File(path), exists = true)
            ?: error(notFound("仓库", path))

    /** app 模块内文件是否存在, 用于"应已删除"类断言。 */
    fun existsApp(path: String): Boolean =
        firstExisting(File(path), File("app", path)) != null

    /** 仓库内文件是否存在, 用于"应已删除"类断言。 */
    fun existsRepo(path: String): Boolean =
        firstExisting(File("..", path), File(path)) != null

    private fun firstExisting(vararg candidates: File, exists: Boolean = false): File? =
        candidates.firstOrNull { if (exists) it.exists() else it.isFile }

    private fun notFound(scope: String, path: String): String =
        "读不到$scope文件: $path (cwd=${File("").absolutePath}, 已试相对/绝对两种工作目录)"
}
