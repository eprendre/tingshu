package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import io.reactivex.Completable
import org.jsoup.Jsoup
import java.net.URLEncoder

object JuTingWang : TingShu() {
    override fun getSourceId(): String {
        return "7d8edaa3b3e44485a2b8a8151cf5d84a"
    }

    override fun getUrl(): String {
        return "https://www.yousxs.com"
    }

    override fun getName(): String {
        return "聚听网"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n网站已无法打开，请禁用此源。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url =
            "https://www.yousxs.com/classify.html?novelName=$encodedKeywords&pageNum=$page"
        val doc = Jsoup.connect(url).config(true).get()

        val pages = doc.selectFirst(".pagination > li:last-child").text()
        val totalPage =
            Regex("^.+部有声小说总共(\\d+)页.+$").find(pages)?.groupValues?.get(1)?.toInt() ?: 1

        val list = ArrayList<Book>()
        val elementList = doc.select(".panel-body")[1].select("ul > li")
        elementList.forEach { element ->
            val bookUrl = element.selectFirst("a").absUrl("href")
            val coverUrl = ""
            val title = element.selectFirst("a").text().split("】")[1]
            val intro = ""
            val status = element.selectFirst("span").text()
            list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, totalPage)
    }

    fun fetchBookInfo(book: Book): Completable {
        return Completable.fromCallable {
            val doc = Jsoup.connect(book.bookUrl).config(true).get()
            val panelBody = doc.selectFirst(".panel-body")
            val coverUrl = panelBody.selectFirst("img").absUrl("src")
            val infos = panelBody.selectFirst(".row > div:last-child").children()
            val author = infos[1].text()
            val artist = infos[2].text()
            val intro = infos[3].text()
            book.coverUrl = coverUrl
            book.artist = artist
            book.author = author
            book.intro = intro
            return@fromCallable null
        }
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        val doc = Jsoup.connect(bookUrl).config(true).get()
        val panelBody = doc.selectFirst(".panel-body")
        val coverUrl = panelBody.selectFirst("img").absUrl("src")
        val infos = panelBody.selectFirst(".row > div:last-child").children()
        val author = infos[1].text()
        val artist = infos[2].text()
        val intro = infos[3].text()

        val panelBodyList = doc.select(".panel-body")
        episodes.addAll(panelBodyList[1].select(".row > div > a").map {
            Episode(it.text(), it.attr("abs:href"))
        })
        return BookDetail(episodes, intro, coverUrl = coverUrl, artist = artist, author = author)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(true, script = "(function() { return (ap.audio.currentSrc); })();") { src ->
            return@setUp src.replace("\"", "")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("网络玄幻", "https://www.yousxs.com/classify.html?clsid=1&pageNum=1"),
                CategoryTab("科幻小说", "https://www.yousxs.com/classify.html?clsid=2&pageNum=1"),
                CategoryTab("恐怖悬疑", "https://www.yousxs.com/classify.html?clsid=3&pageNum=1"),
                CategoryTab("武侠小说", "https://www.yousxs.com/classify.html?clsid=4&pageNum=1"),
                CategoryTab("都市言情", "https://www.yousxs.com/classify.html?clsid=5&pageNum=1"),
                CategoryTab("刑侦推理", "https://www.yousxs.com/classify.html?clsid=6&pageNum=1"),
                CategoryTab("历史军事", "https://www.yousxs.com/classify.html?clsid=7&pageNum=1"),
                CategoryTab("童话寓言", "https://www.yousxs.com/classify.html?clsid=8&pageNum=1"),
                CategoryTab("官场商战", "https://www.yousxs.com/classify.html?clsid=9&pageNum=1"),
                CategoryTab("人物纪实", "https://www.yousxs.com/classify.html?clsid=10&pageNum=1")
            )
        )
        val menu2 = CategoryMenu(
            "评书", listOf(
                CategoryTab("单田芳", "https://www.yousxs.com/classify.html?clsid=24&pageNum=1"),
                CategoryTab("关勇超", "https://www.yousxs.com/classify.html?clsid=25&pageNum=1"),
                CategoryTab("张少佐", "https://www.yousxs.com/classify.html?clsid=26&pageNum=1"),
                CategoryTab("田战义", "https://www.yousxs.com/classify.html?clsid=27&pageNum=1"),
                CategoryTab("田连元", "https://www.yousxs.com/classify.html?clsid=28&pageNum=1"),
                CategoryTab("袁阔成", "https://www.yousxs.com/classify.html?clsid=29&pageNum=1"),
                CategoryTab("连丽如", "https://www.yousxs.com/classify.html?clsid=30&pageNum=1"),
                CategoryTab("马长辉", "https://www.yousxs.com/classify.html?clsid=31&pageNum=1"),
                CategoryTab("孙一", "https://www.yousxs.com/classify.html?clsid=32&pageNum=1"),
                CategoryTab("粤语评书", "https://www.yousxs.com/classify.html?clsid=22&pageNum=1"),
                CategoryTab("其他评书", "https://www.yousxs.com/classify.html?clsid=16&pageNum=1")
            )
        )
        val menu3 = CategoryMenu(
            "其他", listOf(
                CategoryTab("百家讲坛", "https://www.yousxs.com/classify.html?clsid=11&pageNum=1"),
                CategoryTab("亲子教育", "https://www.yousxs.com/classify.html?clsid=12&pageNum=1"),
                CategoryTab("商业财经", "https://www.yousxs.com/classify.html?clsid=13&pageNum=1"),
                CategoryTab("地方戏曲", "https://www.yousxs.com/classify.html?clsid=14&pageNum=1"),
                CategoryTab("健康养生", "https://www.yousxs.com/classify.html?clsid=15&pageNum=1"),
                CategoryTab("广播剧", "https://www.yousxs.com/classify.html?clsid=23&pageNum=1"),
                CategoryTab("教育培训", "https://www.yousxs.com/classify.html?clsid=17&pageNum=1"),
                CategoryTab("时尚生活", "https://www.yousxs.com/classify.html?clsid=18&pageNum=1"),
                CategoryTab("有声文学", "https://www.yousxs.com/classify.html?clsid=19&pageNum=1"),
                CategoryTab("相声小品", "https://www.yousxs.com/classify.html?clsid=20&pageNum=1"),
                CategoryTab("综艺娱乐", "https://www.yousxs.com/classify.html?clsid=21&pageNum=1"),
                CategoryTab("脱口秀", "https://www.yousxs.com/classify.html?clsid=34&pageNum=1")
            )
        )
        return listOf(menu1, menu2, menu3)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(true).get()
        val nextUrl = doc.selectFirst(".pagination > li:nth-last-child(2) > a").absUrl("href")
        val currentPage = doc.selectFirst(".pagination > li.active").text().toInt()
        val totalPageInfo = doc.selectFirst(".pagination > li:last-child").text()
        val totalPage = Regex("^.+部有声小说总共(\\d+)页.+$").find(totalPageInfo)?.groupValues?.get(1)?.toInt() ?: 1

        val list = ArrayList<Book>()
        val elementList = doc.select(".panel-body")[1].select("ul > li")
        elementList.forEach { element ->
            val bookUrl = element.selectFirst("a").absUrl("href")
            val coverUrl = ""
            val title = element.selectFirst("a").text().split("】")[1]
            val intro = ""
            val status = element.selectFirst("span").text()
            list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

}