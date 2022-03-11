package com.github.eprendre.videosource

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlJsoupExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object YingHuaCD : TingShu(){
    override fun getSourceId(): String {
        return "815245280b854e0cb5dbdfef020f0fd2"
    }

    override fun getUrl(): String {
        return "http://www.yinghuacd.com/"
    }

    override fun getName(): String {
        return "樱花动漫"
    }

    override fun getDesc(): String {
        return "樱花动漫拥有上万集高清晰画质的在线动漫，观看完全免费、无须注册、高速播放、更新及时的专业在线樱花动漫站，我们致力为所有动漫迷们提供最好看的动漫。"
    }

    override fun isCacheable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = if (page == 1) {
            "http://www.yinghuacd.com/search/${encodedKeywords}"
        } else {
            "http://www.yinghuacd.com/search/${encodedKeywords}/?page=${page}"
        }
        val doc = Jsoup.connect(url).config(true).get()

        val currentPage = 1
        val totalPage = doc.selectFirst("#lastn")?.text()?.toInt() ?: 1

        val list = ArrayList<Book>()
        val elementList = doc.select(".lpic > ul > li")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst("a > img").attr("src")
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst("h2 > a").text()
            val status = item.selectFirst("span").text()
            val desc = item.selectFirst("p").text()
            val author = ""
            val artist = ""
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
                this.status = status
                this.intro = desc
            })
        }
        return Pair(list, totalPage)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlJsoupExtractor.setUp(true) { doc ->
            return@setUp doc.selectFirst("#playbox").attr("data-vid").split("$")[0]
        }
        return AudioUrlJsoupExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "分类", listOf(
                CategoryTab("新番", "http://www.yinghuacd.com/2022/"),
                CategoryTab("日本漫", "http://www.yinghuacd.com/japan/"),
                CategoryTab("国产动漫", "http://www.yinghuacd.com/china/"),
                CategoryTab("美国动漫", "http://www.yinghuacd.com/american/"),
                CategoryTab("剧场版", "http://www.yinghuacd.com/37/")
            )
        )
        return listOf(menu1)

    }

    override fun getCategoryList(url: String): Category {
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).config(true).get()

        var currentPage = 1
        val totalPage = doc.selectFirst("#lastn")?.text()?.toInt() ?: 1
        var nextUrl = ""
        if (totalPage > 1) {
            nextUrl = doc.select(".pages > .a1").last()?.absUrl("href") ?: ""
            currentPage = doc.selectFirst(".pages > span")?.text()?.toInt() ?: 1
        }

        val elementList = doc.select(".lpic > ul > li")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst("a > img").attr("src")
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst("h2 > a").text()
            val status = item.selectFirst("span").text()
            val desc = item.selectFirst("p").text()
            val author = ""
            val artist = ""
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
                this.status = status
                this.intro = desc
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(true).get()
            val elements = doc.select(".movurl > ul > li > a")
            elements.forEach { element ->
                val title = element.text()
                val url = element.absUrl("href")
                episodes.add(Episode(title, url))
            }
            episodes.reverse()
        }
        return BookDetail(episodes)
    }
}