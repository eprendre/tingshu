package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.getCookie
import com.github.eprendre.tingshu.extensions.getDesktopUA
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLEncoder

object YouShengXiaoShuoBa : TingShu(), CoverUrlExtraHeaders, AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "c242639e0cea4abeade8b9729f03ba0b"
    }

    override fun getUrl(): String {
        return "http://m.ysxs8.vip"
    }

    override fun getName(): String {
        return "有声小说吧"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n搜索失效"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val url = "http://m.ysxs8.vip/search.asp?searchword=$encodedKeywords&page=$page"
        val doc = Jsoup.connect(url).config().get()

        val totalPage = doc.selectFirst(".page").ownText().split("/")[1].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            val title = element.selectFirst(".bookname").text()
            val bookId = element.attr("bookid")
            val bookUrl = "http://m.ysxs8.vip/downlist/$bookId.html"
            val (author, artist) = element.selectFirst(".author").let {
                val array = it.text().split(" ")
                Pair(array[1].replace("播音:", "作者: "), array[0].replace("作者：", "播音: "))
            }
            val status = element.selectFirst(".update").text()
            val intro = element.selectFirst(".intro_line").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config().get()
            val list = doc.select("#playlist > ul > li > a").map {
                Episode(it.text(), it.attr("abs:href"))
            }
            episodes.addAll(list)
        }
        return BookDetail(episodes)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(
            script = "document.getElementById(\"xplayer\").contentDocument.getElementById(\"viframe\").contentDocument.getElementsByTagName(\"audio\")[0].src") { str ->
            return@setUp str.replace("'", "").replace("\"", "")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("网络玄幻", "http://m.ysxs8.vip/downlist/r52_1.html"),
                CategoryTab("探险盗墓", "http://m.ysxs8.vip/downlist/r45_1.html"),
                CategoryTab("恐怖悬疑", "http://m.ysxs8.vip/downlist/r17_1.html"),
                CategoryTab("评书下载", "http://m.ysxs8.vip/downlist/r3_1.html"),
                CategoryTab("历史军事", "http://m.ysxs8.vip/downlist/r15_1.html"),
                CategoryTab("传统武侠", "http://m.ysxs8.vip/downlist/r12_1.html"),
                CategoryTab("都市言情", "http://m.ysxs8.vip/downlist/r13_1.html"),
                CategoryTab("官场刑侦", "http://m.ysxs8.vip/downlist/r14_1.html"),
                CategoryTab("人物传记", "http://m.ysxs8.vip/downlist/r16_1.html")
            )
        )
        val menu2 = CategoryMenu(
            "其它", listOf(
                CategoryTab("相声戏曲", "http://m.ysxs8.vip/downlist/r7_1.html"),
                CategoryTab("管理营销", "http://m.ysxs8.vip/downlist/r6_1.html"),
                CategoryTab("广播剧", "http://m.ysxs8.vip/downlist/r18_1.html"),
                CategoryTab("百家讲坛", "http://m.ysxs8.vip/downlist/r32_1.html"),
                CategoryTab("外语读物", "http://m.ysxs8.vip/downlist/r35_1.html"),
                CategoryTab("儿童读物", "http://m.ysxs8.vip/downlist/r4_1.html"),
                CategoryTab("明朝那些事儿", "http://m.ysxs8.vip/downlist/r36_1.html"),
                CategoryTab("有声文学", "http://m.ysxs8.vip/downlist/r41_1.html"),
                CategoryTab("职场商战", "http://m.ysxs8.vip/downlist/r81_1.html")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val pageElement = doc.selectFirst(".page")
        val nextUrl = pageElement.selectFirst(".next").absUrl("href")

        val (currentPage, totalPage) = pageElement.ownText().replace("次", "")
            .replace("：", "").replace("页", "").trim()
            .split("/").let {
            Pair(it[0].toInt(), it[1].toInt())
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".book-ol > .book-subli > a")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("img").attr("data-original")
            val title = element.selectFirst(".book-title").text()
            val bookUrl = element.absUrl("href")
            val (author, artist) = element.select(".book-submeta").last().text().trim().let {
                val array = it.split("播音：")
                Pair(array[0].replace("作者：", "").trim(), array[1].trim())
            }
            val status = element.selectFirst(".book-submeta").text()
            val intro = element.selectFirst(".book-desc").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun coverHeaders(coverUrl: String, headers: MutableMap<String, String>): Boolean {
        if(coverUrl.contains("ysxs8.vip")) {
            headers["Host"] = URL(coverUrl).host
            headers["User-Agent"] = getDesktopUA()
            return true
        }
        return false
    }

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("ysxs8")) {//判断一下，因为一个网站可能会爬取多家资源
            hashMap["Cookie"] = getCookie("http://m.ysxs8.vip/") ?: ""
        }
        return hashMap
    }

}