package com.github.eprendre.videosource

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewSniffExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object JiuZhou : TingShu() {
    override fun getSourceId(): String {
        return "be8f23528aba4682b0252b7fd61ad0d6"
    }

    override fun getUrl(): String {
        return "http://www.unss.net/"
    }

    override fun getName(): String {
        return "九州影视"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "http://www.unss.net/vodsearch/${encodedKeywords}----------${page}---.html"
        val doc = Jsoup.connect(url).config(false).get()

        var currentPage = 1
        var totalPage = 1
        val pages = doc.selectFirst(".stui-page > .visible-xs")?.text() ?: ""
        if (pages.isNotEmpty()) {
            pages.split("/").let {
                currentPage = it[0].toInt()
                totalPage = it[1].toInt()
            }
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".stui-vodlist__media > li")
        elementList.forEach { item ->
            val thumb = item.selectFirst(".thumb > .v-thumb")
            val coverUrl = thumb.attr("data-original")
            val bookUrl = thumb.absUrl("href")
            val title = thumb.attr("title")
            val status = thumb.selectFirst(".text-right").text()
            val pList = item.select(".detail > p")
            val author = pList[0].text()
            val artist = pList[1].text()
            val desc = pList[2].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = desc
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, totalPage)
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "分类", listOf(
                CategoryTab("电影", "http://www.unss.net/vodtype/1.html"),
                CategoryTab("电视剧", "http://www.unss.net/vodtype/2.html"),
                CategoryTab("综艺", "http://www.unss.net/vodtype/3.html"),
                CategoryTab("动漫", "http://www.unss.net/vodtype/4.html")
            )
        )
        return listOf(menu1)
    }


    override fun isCacheable(): Boolean {
        return false
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlWebViewSniffExtractor
    }

    override fun getCategoryList(url: String): Category {
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).config(false).get()

        var currentPage = 1
        var totalPage = 1
        var nextUrl = ""
        val pages = doc.selectFirst(".stui-page > .visible-xs")?.text() ?: ""
        if (pages.isNotEmpty()) {
            pages.split("/").let {
                currentPage = it[0].toInt()
                totalPage = it[1].toInt()
            }
            nextUrl = doc.select(".stui-page > li").first { it.text() == "下一页" }
                .selectFirst("a").absUrl("href")
        }

        val elementList = doc.select(".stui-vodlist > li > div")
        elementList.forEach { item ->
            val thumb = item.selectFirst(".stui-vodlist__thumb")
            val coverUrl = thumb.attr("data-original")
            val bookUrl = thumb.absUrl("href")
            val title = thumb.attr("title")
            val status = thumb.selectFirst(".text-right").text()
            val author = ""
            val artist = ""
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
            val doc = Jsoup.connect(bookUrl).config(false).get()
            val tabs = doc.select(".nav-tabs > li")
            val playlists = doc.select(".tab-content > div")

            tabs.forEachIndexed { index, tab ->
                val tabTitle = tab.text()
                val list = playlists[index].select("ul > li > a").map { element ->
                    val title = "$tabTitle - ${element.text()}"
                    val url = element.absUrl("href")
                    Episode(title, url)
                }
                episodes.addAll(list)
            }
        }
        return BookDetail(episodes)
    }
}