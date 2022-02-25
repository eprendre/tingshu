package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtraHeaders
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlJsoupExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

object HuanTingWang : TingShu(), AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "63bbe587eae94cedb15e80d2c8689805"
    }

    override fun getUrl(): String {
        return "http://m.ting89.com"
    }

    override fun getName(): String {
        return "幻听网"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun getDesc(): String {
        return "推荐指数:5星 ⭐⭐⭐⭐⭐\n网站已关闭，此源无法使用。"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val url = "http://m.ting89.com/search.asp?searchword=$encodedKeywords&page=$page"
        val doc = Jsoup.connect(url).config().get()

        val totalPage = doc.selectFirst(".page").ownText().split("/")[1].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap .bookbox")
        elementList.forEach { element ->
            var coverUrl = element.selectFirst(".bookimg img").attr("orgsrc")
            if (coverUrl.startsWith("//")) {
                coverUrl = "http:$coverUrl"
            }
            val bookUrl = "${getUrl()}/book/?${element.attr("bookid")}.html"
            val bookInfo = element.selectFirst(".bookinfo")
            val title = bookInfo.selectFirst(".bookname").text()
            val (author, artist) = bookInfo.selectFirst(".author").text().split(" ").let {
                Pair(it[0], it[1])
            }
            val intro = bookInfo.selectFirst(".intro_line").text()
            val status = bookInfo.selectFirst(".update").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config().get()
        val episodes = doc.select("#playlist li a").map {
            Episode(it.text(), it.attr("abs:href"))
        }
        val intro = doc.selectFirst(".book_intro").text()

        return BookDetail(episodes, intro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlJsoupExtractor.setUp { doc ->
            val result = doc.getElementsByTag("script")
                .first { !it.hasAttr("src") && !it.hasAttr("type") }
                .html()
                .let {
                    Regex("datas=\\(\"(.*)\"\\.split")
                        .find(it)?.groupValues?.get(1)
                }
            if (result == null) {
                return@setUp ""
            } else {
                val list = URLDecoder.decode(result, "gb2312").split("&")
                return@setUp list[0]
            }
        }
        return AudioUrlJsoupExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "小说", listOf(
                CategoryTab("玄幻玄幻", "http://m.ting89.com/booklist/?1.html"),
                CategoryTab("武侠仙侠", "http://m.ting89.com/booklist/?2.html"),
                CategoryTab("科幻世界", "http://m.ting89.com/booklist/?5.html"),
                CategoryTab("网络游戏", "http://m.ting89.com/booklist/?11.html"),
                CategoryTab("现代都市", "http://m.ting89.com/booklist/?3.html"),
                CategoryTab("女生言情", "http://m.ting89.com/booklist/?4.html"),
                CategoryTab("女生穿越", "http://m.ting89.com/booklist/?38.html"),
                CategoryTab("推理悬念", "http://m.ting89.com/booklist/?6.html"),
                CategoryTab("恐怖故事", "http://m.ting89.com/booklist/?7.html"),
                CategoryTab("悬疑惊悚", "http://m.ting89.com/booklist/?8.html")
            )
        )

        val menu2 = CategoryMenu(
            "其它", listOf(
                CategoryTab("历史传记", "http://m.ting89.com/booklist/?9.html"),
                CategoryTab("铁血军魂", "http://m.ting89.com/booklist/?10.html"),
                CategoryTab("经典传记", "http://m.ting89.com/booklist/?35.html"),
                CategoryTab("百家讲坛", "http://m.ting89.com/booklist/?36.html"),
                CategoryTab("粤语", "http://m.ting89.com/booklist/?40.html"),
                CategoryTab("儿童故事", "http://m.ting89.com/booklist/?16.html"),
                CategoryTab("相声", "http://m.ting89.com/booklist/?34.html"),
                CategoryTab("评书", "http://m.ting89.com/booklist/?13.html")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val nextUrl = doc.select(".page a").firstOrNull { it.text().contains("下页") }?.attr("abs:href") ?: ""
        val pages = doc.selectFirst(".page").ownText().let { text ->
            Regex("(\\d+)/(\\d+)").find(text)!!.groupValues
        }
        val currentPage = pages[1].toInt()
        val totalPage = pages[2].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap .bookbox")
        elementList.forEach { element ->
            var coverUrl = element.selectFirst(".bookimg img").attr("orgsrc")
            if (coverUrl.startsWith("//")) {
                coverUrl = "http:$coverUrl"
            }
            val bookUrl = "${getUrl()}/book/?${element.attr("bookid")}.html"
            val bookInfo = element.selectFirst(".bookinfo")
            val title = bookInfo.selectFirst(".bookname").text()
            val (author, artist) = bookInfo.selectFirst(".author").text().split(" ").let {
                Pair(it[0], it[1])
            }
            val intro = bookInfo.selectFirst(".intro_line").text()
            val status = bookInfo.selectFirst(".update").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("ting89.com")) {//判断一下，因为一个网站可能会爬取多家资源
            hashMap["Referer"] = "http://m.ting89.com/"
        }
        return hashMap
    }
}