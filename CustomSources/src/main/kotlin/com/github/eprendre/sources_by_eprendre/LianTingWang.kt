package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URLEncoder

object LianTingWang : TingShu() {
    override fun getSourceId(): String {
        return "5caf7568d5f64406822e3a41364a016c"
    }

    override fun getUrl(): String {
        return "https://m.ting55.com"
    }

    override fun getName(): String {
        return "恋听网"
    }

    override fun getDesc(): String {
        return "推荐指数:5星 ⭐⭐⭐⭐⭐\n部分书籍是收费内容，本 app 不支持播放。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "https://m.ting55.com/search/$encodedKeywords/page/$page"
        val doc = Jsoup.connect(url).config(false).get()

        val cpage = doc.selectFirst(".cpage")
        var totalPage = 1
        if (cpage != null && cpage.childrenSize() > 0) {
            totalPage = cpage.selectFirst("span").text().replace("页次 ", "").split("/")[1].toInt()
        }

        val list = ArrayList<Book>()
        try {
            val elementList = doc.select(".slist > a")
            elementList.forEach { element ->
                val coverUrl = element.selectFirst("dl > dt > img").absUrl("src")
                val bookUrl = element.absUrl("href")
                val infos = element.selectFirst("dl > dd").children()
                val title = infos[0].text()
                val author = infos[1].text()
                val artist = infos[2].text()
                val status = infos[3].text()
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.status = status
                    this.sourceId = getSourceId()
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config(false).get()

        val episodes = doc.select(".plist > a").map {
            Episode(it.text(), it.absUrl("href")).apply {
                this.isFree = it.hasClass("f")
            }
        }
        val intro = doc.selectFirst(".intro").text()
        return BookDetail(episodes, intro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(isDeskTop = false) { str ->
            val doc = Jsoup.parse(str)
            return@setUp doc.selectFirst("audio")?.attr("src")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("推荐", "https://m.ting55.com/tuijian"),
                CategoryTab("玄幻", "https://m.ting55.com/category/1"),
                CategoryTab("武侠", "https://m.ting55.com/category/2"),
                CategoryTab("都市", "https://m.ting55.com/category/3"),
                CategoryTab("言情", "https://m.ting55.com/category/4"),
                CategoryTab("穿越", "https://m.ting55.com/category/5"),
                CategoryTab("科幻", "https://m.ting55.com/category/6"),
                CategoryTab("推理", "https://m.ting55.com/category/7"),
                CategoryTab("恐怖", "https://m.ting55.com/category/8"),
                CategoryTab("惊悚", "https://m.ting55.com/category/9")
            )
        )
        val menu2 = CategoryMenu(
            "其它", listOf(
                CategoryTab("历史", "https://m.ting55.com/category/10"),
                CategoryTab("经典", "https://m.ting55.com/category/11"),
                CategoryTab("相声", "https://m.ting55.com/category/12"),
                CategoryTab("评书", "https://m.ting55.com/category/14"),
                CategoryTab("百家讲坛", "https://m.ting55.com/category/13")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(false).get()
        val cpage = doc.selectFirst(".cpage")
        var totalPage = 1
        var currentPage = 1
        var nextUrl = ""
        if (cpage != null) {
            val pages = cpage.selectFirst("span").text().replace("页次 ", "").split("/")
            currentPage = pages[0].toInt()
            totalPage = pages[1].toInt()
            cpage.select("a").firstOrNull { it.text() == "下一页" }?.let {
                nextUrl = it.absUrl("href")
            }
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".clist > a")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("dl > dt > img").absUrl("src")
            val bookUrl = element.absUrl("href")
            val infos = element.selectFirst("dl > dd").children()
            val title = infos[0].text()
            val author = infos[1].text()
            val artist = infos[2].text()
            val status = infos[3].text()

            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }

}