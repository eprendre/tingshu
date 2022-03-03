package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlJsoupExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object VBus : TingShu() {
    override fun getSourceId(): String {
        return "405d26b44ad24b25a450ede64bac682f"
    }

    override fun getUrl(): String {
        return "https://www.vbus.cc/"
    }

    override fun getName(): String {
        return "声音巴士"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n一个无障碍交流平台，一群热爱声音的人。"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    /**
     * 没有搜索
     */
    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlJsoupExtractor.setUp { doc ->
            doc.selectFirst("audio").absUrl("src")
        }
        return AudioUrlJsoupExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu = CategoryMenu(
                "声音巴士", listOf(
                CategoryTab("推荐", "https://www.vbus.cc/recommend/1"),
                CategoryTab("最新", "https://www.vbus.cc/new/1"),
                CategoryTab("最热", "https://www.vbus.cc/hot/1"))
        )
        return listOf(menu)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(false).get()
        val pages = doc.select(".page-item > a")
        val nextPage = pages.firstOrNull { it.text().contains("下一页") }
        var nextUrl = ""
        if (nextPage != null) {
            nextUrl = nextPage.absUrl("href")
        }
        val currentPage = url.split("/").last().toInt()
        val totalPage = if (nextPage == null) {
            currentPage
        } else {
            currentPage + 1
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".program-list > li")
        elementList.forEach { element ->
            val a = element.selectFirst("h3 > a")
            val bookUrl = a.absUrl("href")
            val coverUrl = ""
            val title = a.text()
            val programMeta = element.selectFirst(".program-meta")
            val status = programMeta.select("span").last().text()
            val artist = programMeta.select("span").first().text()
            val author = programMeta.select("> a").joinToString(separator = ",") { it.text() }
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            episodes.add(Episode("音频", bookUrl))
        }
        return BookDetail(episodes)
    }
}