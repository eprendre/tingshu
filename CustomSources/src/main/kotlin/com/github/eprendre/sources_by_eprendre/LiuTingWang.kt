package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewSniffExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object LiuTingWang : TingShu() {
    override fun getSourceId(): String {
        return "3fb16598bb214631807e2000e08c746c"
    }

    override fun getUrl(): String {
        return "http://www.6ting.cn"
    }

    override fun getName(): String {
        return "六听网"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n 一家优质的玄幻武侠有声小说分享网站"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "http://www.6ting.cn/search.php?page=${page}&searchword=${encodedKeywords}"
        val doc = Jsoup.connect(url).config(false).get()

        val pager = doc.select(".pager > li")
        var totalPage = page
        if (pager.size > 0) {
            pager.firstOrNull { it.text().equals("下一页") }?.let {
                totalPage = page + 1
            }
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".list-unstyled > li.ting-col")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("img").absUrl("src")
            val bookUrl = element.selectFirst("h4 > a").absUrl("href")
            val title = element.selectFirst("h4 > a").text()
            val l = element.select("h6 > a")
            val author = l.first().text()
            val artist = l.last().text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, totalPage)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(script = "var scripts = document.querySelector('iframe').contentDocument.scripts; scripts[scripts.length - 1].textContent") { str ->
           return@setUp Regex("url: '(.+)',cover").find(str)?.groupValues?.get(1) ?: ""
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        return listOf(
            CategoryMenu(
                "分类", listOf(
                    CategoryTab("玄幻武侠", "http://www.6ting.cn/booklist/1.html"),
                    CategoryTab("都市言情", "http://www.6ting.cn/booklist/2.html"),
                    CategoryTab("科幻", "http://www.6ting.cn/booklist/3.html"),
                    CategoryTab("刑侦推理", "http://www.6ting.cn/booklist/4.html"),
                    CategoryTab("恐怖惊悚", "http://www.6ting.cn/booklist/5.html"),
                    CategoryTab("历史军事", "http://www.6ting.cn/booklist/7.html"),
                    CategoryTab("通俗", "http://www.6ting.cn/booklist/8.html"),
                    CategoryTab("百家讲坛", "http://www.6ting.cn/booklist/9.html"),
                    CategoryTab("儿童读物", "http://www.6ting.cn/booklist/10.html"),
                    CategoryTab("相声评书", "http://www.6ting.cn/booklist/11.html"),
                    CategoryTab("经典", "http://www.6ting.cn/booklist/12.html"),
                    CategoryTab("其它", "http://www.6ting.cn/booklist/13.html")
                )
            ))
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(false).get()

        val pager = doc.select(".pager > li > a")
        var currentPage = 1
        var totalPage = 1
        var nextUrl = ""
        if (pager.size > 0) {
            pager.firstOrNull { it.text().equals("下一页") }?.let {
                totalPage = currentPage + 1
                nextUrl = it.absUrl("href")
            }
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".list-unstyled > li.ting-col")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("img").absUrl("src")
            val bookUrl = element.selectFirst("h4 > a").absUrl("href")
            val title = element.selectFirst("h4 > a").text()
            val intro = element.selectFirst("h4 > a").attr("title")
            val l = element.select("h6 > a")
            val author = l.first().text()
            val artist = l.last().text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(false).get()
            val playlists = doc.select("#ting-tab-content > div > .play-list")
            playlists.forEach { element ->
                val l = element.select("li > a").map {
                    Episode(it.text(), it.absUrl("href"))
                }
                if (l.size > episodes.size) {
                    episodes.clear()
                    episodes.addAll(l)
                }
            }
        }
        return BookDetail(episodes)
    }
}