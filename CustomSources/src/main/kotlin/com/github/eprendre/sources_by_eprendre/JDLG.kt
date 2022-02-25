package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

/**
 * 这个网站结构和一般听书站不一样，所以通过取巧的方式把分类做成书籍。
 */
object JDLG : TingShu() {
    override fun getSourceId(): String {
        return "af69db7d4f284caabbeb51a30e05017e"
    }

    override fun getUrl(): String {
        return "http://www.jdlg.net/"
    }

    override fun getName(): String {
        return "经典老歌"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n打开较慢"
    }

    /**
     * 不做搜索
     */
    override fun isSearchable(): Boolean {
        return false
    }

    /**
     * 搜索返回空
     */
    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(isDeskTop = true) { html ->
            val doc = Jsoup.parse(html)
            return@setUp doc.selectFirst("#jp_audio_0")?.attr("src")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu("分类", listOf(
            CategoryTab("老歌分类", "0"),
            CategoryTab("老歌排行", "1")
        ))
        return listOf(menu1)
    }

    override fun getCategoryList(url: String): Category {
        val type = url.toInt()
        val doc = Jsoup.connect("http://www.jdlg.net/jingdianlaoge500shou/").config(true).get()
        val divs = doc.select("#tipcontent > div > div")

        val list = ArrayList<Book>()
        divs[type].select("ul > li > a").forEach {
            val book = Book("", it.absUrl("href"), it.text(), "", "")
            book.sourceId = getSourceId()
            list.add(book)
        }
        return Category(list, 1, 1, url, "")
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config(true).get()
        val episodes = doc.select("div.rankBox > ol > li, div.songList > ol > li")
            .map {
                val a = it.selectFirst(".singer_title > a, .songName")
                var artist = it.selectFirst(".songer > a")?.text()
                if (artist == null) {
                    artist = it.selectFirst("a:last-child")?.text()
                    if (artist == "加入列表") {
                        artist = null
                    }
                }
                var title = a.text()
                if (artist != null) {
                    title += " - $artist"
                }
                Episode(title, a.absUrl("href"))
            }
        return BookDetail(episodes)
    }
}