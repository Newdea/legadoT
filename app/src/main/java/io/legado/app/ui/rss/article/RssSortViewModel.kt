package io.legado.app.ui.rss.article

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import io.legado.app.base.BaseViewModel
import io.legado.app.data.appDb
import io.legado.app.data.entities.RssArticle
import io.legado.app.data.entities.RssReadRecord
import io.legado.app.data.entities.RssSource
import io.legado.app.help.source.removeSortCache


class RssSortViewModel(application: Application) : BaseViewModel(application) {
    var url: String? = null

    /** 深链指定的分类, 为空时展示订阅源的完整分类列表 */
    var sortUrl: String? = null
    var rssSource: RssSource? = null
    val titleLiveData = MutableLiveData<String>()
    var order = System.currentTimeMillis()
    val isGridLayout get() = rssSource?.articleStyle == 2

    fun initData(intent: Intent, finally: () -> Unit) {
        execute {
            url = intent.getStringExtra("url")
            url?.let { url ->
                rssSource = appDb.rssSourceDao.getByKey(url)
                rssSource?.let {
                    titleLiveData.postValue(it.sourceName)
                } ?: let {
                    rssSource = RssSource(sourceUrl = url)
                }
            }
            sortUrl = intent.getStringExtra("sortUrl")?.takeIf { it.isNotBlank() }
            // 单个分类时 tab 栏会隐藏, 用分类名作标题
            sortTabs()?.takeIf { it.size == 1 && it.first().first.isNotEmpty() }
                ?.let { titleLiveData.postValue(it.first().first) }
        }.onFinally {
            finally()
        }
    }

    /** sortUrl 解析出的分类; 未指定深链时返回 null, 由调用方回退到 source.sortUrls() */
    fun sortTabs(): List<Pair<String, String>>? = parseSortTabs(sortUrl)

    fun switchLayout() {
        rssSource?.let {
            if (it.articleStyle < 2) {
                it.articleStyle += 1
            } else {
                it.articleStyle = 0
            }
            execute {
                appDb.rssSourceDao.update(it)
            }
        }
    }

    fun read(rssArticle: RssArticle) {
        execute {
            val rssReadRecord = RssReadRecord(
                record = rssArticle.link,
                title = rssArticle.title,
                readTime = System.currentTimeMillis()
            )
            appDb.rssReadRecordDao.insertRecord(rssReadRecord)
        }
    }

    fun clearArticles() {
        execute {
            url?.let {
                appDb.rssArticleDao.delete(it)
            }
            order = System.currentTimeMillis()
        }.onSuccess {

        }
    }

    fun clearSortCache(onFinally: () -> Unit) {
        execute {
            rssSource?.removeSortCache()
        }.onFinally {
            onFinally.invoke()
        }
    }

    fun getRecords(): List<RssReadRecord> {
        return appDb.rssReadRecordDao.getRecords()
    }

    fun countRecords() : Int {
        return appDb.rssReadRecordDao.countRecords
    }

    fun deleteAllRecord() {
        execute {
            appDb.rssReadRecordDao.deleteAllRecord()
        }
    }

}