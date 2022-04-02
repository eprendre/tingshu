package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object MaLaTingShu : TingShu() {
    override fun getSourceId(): String {
        return "0e2f8065189e417f9b200d8e37ee9d91"
    }

    override fun getUrl(): String {
        return "https://m.malatingshu.com"
    }

    override fun getName(): String {
        return "麻辣听书"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n网站已关闭，请禁用此源"
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "https://m.malatingshu.com/vod-search-wd-$encodedKeywords-p-$page.html"
        val doc = Jsoup.connect(url).config().get()

        val totalPage = if (doc.getElementById("ff-next") != null) {
            page + 1
        } else {
            page
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".container > .vod-item-img > li")
        elementList.forEach { element ->
            val bookUrl = element.selectFirst("h2 > a").absUrl("href")
            val coverUrl = element.selectFirst(".image > a > img").attr("data-original")
            val title = element.selectFirst("h2 > a").text()
            val author = "作者: ${element.selectFirst("h4 > a").text()}"
            list.add(Book(coverUrl, bookUrl, title, author, "").apply {
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config().get()
        val episodes = doc.select(".ff-playurl > li[data-id] > a").map {
            Episode(it.text(), it.attr("abs:href"))
        }

        val intro = doc.selectFirst(".vod-nav-content").text()
        val artist = "主播: " + doc.select(".dl-horizontal > dd")[1].text()

        return BookDetail(episodes, intro, artist)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(
            script = "(function() { return ('<html>'+document.getElementById(\"xplayer\").contentDocument.documentElement.innerHTML+'</html>'); })();"
        ) { str ->
            val doc = Jsoup.parse(str)
            val audioElement = doc.selectFirst("audio")
            return@setUp audioElement?.attr("src")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("玄幻", "https://m.malatingshu.com/list-select-id-1-type-%E7%8E%84%E5%B9%BB-area--year--star--state--order-addtime.html"),
                CategoryTab("穿越", "https://m.malatingshu.com/list-select-id-1-type-%E7%A9%BF%E8%B6%8A-area--year--star--state--order-addtime.html"),
                CategoryTab("恐怖", "https://m.malatingshu.com/list-select-id-1-type-%E6%81%90%E6%80%96-area--year--star--state--order-addtime.html"),
                CategoryTab("都市", "https://m.malatingshu.com/list-select-id-1-type-%E9%83%BD%E5%B8%82-area--year--star--state--order-addtime.html"),
                CategoryTab("言情", "https://m.malatingshu.com/list-select-id-1-type-%E8%A8%80%E6%83%85-area--year--star--state--order-addtime.html"),
                CategoryTab("科幻", "https://m.malatingshu.com/list-select-id-1-type-%E7%A7%91%E5%B9%BB-area--year--star--state--order-addtime.html"),
                CategoryTab("武侠", "https://m.malatingshu.com/list-select-id-1-type-%E6%AD%A6%E4%BE%A0-area--year--star--state--order-addtime.html"),
                CategoryTab("推理", "https://m.malatingshu.com/list-select-id-1-type-%E6%8E%A8%E7%90%86-area--year--star--state--order-addtime.html"),
                CategoryTab("历史", "https://m.malatingshu.com/list-select-id-1-type-%E5%8E%86%E5%8F%B2-area--year--star--state--order-addtime.html"),
                CategoryTab("军事", "https://m.malatingshu.com/list-select-id-1-type-%E5%86%9B%E4%BA%8B-area--year--star--state--order-addtime.html"),
                CategoryTab("网游", "https://m.malatingshu.com/list-select-id-1-type-%E7%BD%91%E6%B8%B8-area--year--star--state--order-addtime.html"),
                CategoryTab("经典", "https://m.malatingshu.com/list-select-id-1-type-%E7%BB%8F%E5%85%B8-area--year--star--state--order-addtime.html"),
                CategoryTab("粤语", "https://m.malatingshu.com/list-select-id-1-type-%E7%B2%A4%E8%AF%AD-area--year--star--state--order-addtime.html"),
                CategoryTab("广播剧", "https://m.malatingshu.com/list-select-id-1-type-%E5%B9%BF%E6%92%AD%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("养生", "https://m.malatingshu.com/list-select-id-1-type-%E5%85%BB%E7%94%9F-area--year--star--state--order-addtime.html")
            )
        )
        val menu2 = CategoryMenu(
            "评书", listOf(
                CategoryTab("评书", "https://m.malatingshu.com/list-select-id-2-type-%E8%AF%84%E4%B9%A6-area--year--star--state--order-addtime.html"),
                CategoryTab("粤语评书", "https://m.malatingshu.com/list-select-id-2-type-%E7%B2%A4%E8%AF%AD%E8%AF%84%E4%B9%A6-area--year--star--state--order-addtime.html")

            )
        )
        val menu3 = CategoryMenu(
            "相声小品", listOf(
                CategoryTab("相声", "https://m.malatingshu.com/list-select-id-3-type-%E7%9B%B8%E5%A3%B0-area--year--star--state--order-addtime.html"),
                CategoryTab("小品", "https://m.malatingshu.com/list-select-id-3-type-%E5%B0%8F%E5%93%81-area--year--star--state--order-addtime.html")
            )
        )
        val menu4 = CategoryMenu(
            "戏曲", listOf(
                CategoryTab("京剧", "https://m.malatingshu.com/list-select-id-4-type-%E4%BA%AC%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("评剧", "https://m.malatingshu.com/list-select-id-4-type-%E8%AF%84%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("越剧", "https://m.malatingshu.com/list-select-id-4-type-%E8%B6%8A%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("黄梅戏", "https://m.malatingshu.com/list-select-id-4-type-%E9%BB%84%E6%A2%85%E6%88%8F-area--year--star--state--order-addtime.html"),
                CategoryTab("豫剧", "https://m.malatingshu.com/list-select-id-4-type-%E8%B1%AB%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("川剧", "https://m.malatingshu.com/list-select-id-4-type-%E5%B7%9D%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("晋剧", "https://m.malatingshu.com/list-select-id-4-type-%E6%99%8B%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("昆曲", "https://m.malatingshu.com/list-select-id-4-type-%E6%98%86%E6%9B%B2-area--year--star--state--order-addtime.html"),
                CategoryTab("沪剧", "https://m.malatingshu.com/list-select-id-4-type-%E6%B2%AA%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("潮剧", "https://m.malatingshu.com/list-select-id-4-type-%E6%BD%AE%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("河北梆子", "https://m.malatingshu.com/list-select-id-4-type-%E6%B2%B3%E5%8C%97%E6%A2%86%E5%AD%90-area--year--star--state--order-addtime.html"),
                CategoryTab("曲剧", "https://m.malatingshu.com/list-select-id-4-type-%E6%9B%B2%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("河南坠子", "https://m.malatingshu.com/list-select-id-4-type-%E6%B2%B3%E5%8D%97%E5%9D%A0%E5%AD%90-area--year--star--state--order-addtime.html"),
                CategoryTab("蒲剧", "https://m.malatingshu.com/list-select-id-4-type-%E8%92%B2%E5%89%A7-area--year--star--state--order-addtime.html"),
                CategoryTab("二人转", "https://m.malatingshu.com/list-select-id-4-type-%E4%BA%8C%E4%BA%BA%E8%BD%AC-area--year--star--state--order-addtime.html")
            )
        )
        val menu5 = CategoryMenu(
            "其它", listOf(
                CategoryTab("儿童", "https://m.malatingshu.com/list-select-id-6-type--area--year--star--state--order-addtime.html"),
                CategoryTab("笑话", "https://m.malatingshu.com/list-select-id-5-type--area--year--star--state--order-addtime.html")
            )
        )
        return listOf(menu1, menu2, menu3, menu4, menu5)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val nextUrl = doc.getElementById("ff-next")?.absUrl("href") ?: ""
        val currentPage = Regex(".+-p-(\\d+).html").find(url)?.groupValues?.get(1)?.toInt() ?: 1
        val totalPage = if (nextUrl.isEmpty()) {
            currentPage
        } else {
            currentPage + 1
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".container > .vod-item-img > li")
        elementList.forEach { element ->
            val bookUrl = element.selectFirst("h2 > a").absUrl("href")
            val coverUrl = element.selectFirst(".image > a > img").attr("data-original")
            val title = element.selectFirst("h2 > a").text()
            val author = "作者: ${element.selectFirst("h4 > a").text()}"
            list.add(Book(coverUrl, bookUrl, title, author, "").apply {
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

}