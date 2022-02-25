package com.github.eprendre.videosource

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewSniffExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object NanGua : TingShu() {
    override fun getSourceId(): String {
        return "d3121473ac094dc7b16158c2824a11d7"
    }

    override fun getUrl(): String {
        return "http://www.nangua55.com"
    }

    override fun getName(): String {
        return "南瓜影视"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "http://www.nangua55.com/search/"

        val doc = Jsoup.connect(url).config(true)
            .headers(mapOf("Referer" to "http://www.nangua55.com/search/"))
            .data("wd", keywords)
            .post()
        val currentPage = 1
        val totalPage = 1

        val list = ArrayList<Book>()
        val elementList = doc.select("#content > div")
        elementList.forEach { item ->
            val a = item.selectFirst(".video-pic")
            val coverUrl = a.attr("data-original")
            val bookUrl = a.absUrl("href")
            val title = a.attr("title")
            val author = ""
            val artist = ""
            val status = item.select(".info > li")[2].ownText()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, 1)
    }

    override fun isCacheable(): Boolean {
        return false
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlWebViewSniffExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "电视剧", listOf(
                CategoryTab("国产剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-14-picm-1-p-1"),
                CategoryTab("韩国剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-17-picm-1-p-1"),
                CategoryTab("欧美剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-19-picm-1-p-1"),
                CategoryTab("日本剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-18-picm-1-p-1"),
                CategoryTab("香港剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-15-picm-1-p-1"),
                CategoryTab("台湾剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-16-picm-1-p-1"),
                CategoryTab("海外剧", "http://www.nangua55.com/index.php?s=home-vod-type-id-20-picm-1-p-1")
            )
        )
        val menu2 = CategoryMenu(
            "电影", listOf(
                CategoryTab("动作片", "http://www.nangua55.com/index.php?s=home-vod-type-id-5-picm-1-p-1"),
                CategoryTab("喜剧片", "http://www.nangua55.com/index.php?s=home-vod-type-id-6-picm-1-p-1"),
                CategoryTab("爱情片", "http://www.nangua55.com/index.php?s=home-vod-type-id-7-picm-1-p-1"),
                CategoryTab("科幻片", "http://www.nangua55.com/index.php?s=home-vod-type-id-8-picm-1-p-1"),
                CategoryTab("恐怖片", "http://www.nangua55.com/index.php?s=home-vod-type-id-9-picm-1-p-1"),
                CategoryTab("剧情片", "http://www.nangua55.com/index.php?s=home-vod-type-id-10-picm-1-p-1"),
                CategoryTab("战争片", "http://www.nangua55.com/index.php?s=home-vod-type-id-11-picm-1-p-1")
            )
        )
        val menu3 = CategoryMenu(
            "其它", listOf(
                CategoryTab("动漫", "http://www.nangua55.com/index.php?s=home-vod-type-id-3-area--year--letter--order--picm-1-p-1"),
                CategoryTab("综艺", "http://www.nangua55.com/index.php?s=home-vod-type-id-4-area--year--letter--order--picm-1-p-1"),
                CategoryTab("微电影", "http://www.nangua55.com/index.php?s=home-vod-type-id-30-area--year--letter--order--picm-1-p-1")
            )
        )

        return listOf(menu1, menu2, menu3)
    }

    override fun getCategoryList(url: String): Category {
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).config(true)
            .headers(mapOf("Referer" to "http://www.nangua55.com")).get()
        val pages = doc.selectFirst("#long-page > ul > .visible-xs").text().split("/")
        val currentPage = pages[0].toInt()
        val totalPage = pages[1].toInt()
        val nextUrl = doc.select("#long-page > ul > li").first { it.text() == "下一页" }
            .selectFirst("a").absUrl("href")

        val elementList = doc.select("#content > li")
        elementList.forEach { item ->
            val a = item.selectFirst(".video-pic")
            val coverUrl = a.attr("data-original")
            val bookUrl = a.absUrl("href")
            val title = item.selectFirst(".title").text()
            val author = item.selectFirst(".subtitle").text()
            val artist = "评分: " + a.selectFirst(".score").text()
            val status = a.selectFirst(".note").text()
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
            val doc = Jsoup.connect(bookUrl).config(true).get()
            val tabs = doc.select("#playTab > li")
            val playlists = doc.select(".playlist > ul")
            tabs.forEachIndexed { index, tab ->
                val tabTitle = tab.text()
                val list = playlists[index].select("li > a").map { element ->
                    val title = "$tabTitle - ${element.text()}"
                    val url = element.absUrl("href")
                    Episode(title, url)
                }.asReversed()
                episodes.addAll(list)
            }

        }
        return BookDetail(episodes)
    }
}