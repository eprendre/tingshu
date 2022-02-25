package com.github.eprendre.videosource

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object NiuNiu : TingShu() {
    override fun getSourceId(): String {
        return "8eb0da4061a4448e85c30fd683c13d99"
    }

    override fun getUrl(): String {
        return "http://www.ziliao6.com/tv/"
    }

    override fun getName(): String {
        return "牛牛TV"
    }

    override fun getDesc(): String {
        return "受页面结构限制，此源不支持章节列表的刷新，若有更新请去重新搜索打开。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "http://www.ziliao6.com/tv/?name=${encodedKeywords}"
        val doc = Jsoup.connect(url).config(false).get()

        val list = ArrayList<Book>()

        doc.selectFirst(".alert.alert-info").children()
            .forEach { element ->
                val coverUrl = ""
                if (element.`is`("details")) {
                    val title = element.selectFirst("summary").ownText()
                    val bookUrl = element.select("ul > a").filter { a ->
                        a.text().isNotEmpty()
                    }.joinToString(separator = ",") { a ->
                        return@joinToString a.text() + "&&" + a.absUrl("href")
                    }
                    list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                        this.sourceId = getSourceId()
                    })
                } else if(element.`is`("a")) {
                    val title = element.text()
                    val bookUrl = title + "&&" + element.absUrl("href")
                    list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                        this.sourceId = getSourceId()
                    })
                }
            }
        return Pair(list, 1)
    }

    override fun isCacheable(): Boolean {
        return false
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        return emptyList()
    }

    override fun getCategoryList(url: String): Category {
        return Category(emptyList(), 0, 0, "", "")
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        bookUrl.split(",").forEach {
            val params = it.split("&&")
            val e = Episode(params[0], params[1])
            episodes.add(e)
        }
        return BookDetail(episodes)
    }
}