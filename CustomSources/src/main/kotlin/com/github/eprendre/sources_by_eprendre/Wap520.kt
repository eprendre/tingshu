package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object Wap520 : TingShu() {
    override fun getSourceId(): String {
        return "2f7c45c8fd1141e9a433e062afbd6f5a"
    }

    override fun getUrl(): String {
        return "http://wap.fushu520.com/"
    }

    override fun getName(): String {
        return "520听书网v2"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n另一个520听书网, 虽同名应该不是同一个。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "http://wap.fushu520.com/search.html"
        val requestParams = mapOf("searchword" to keywords, "searchtype" to "novelname")

        val doc = Jsoup.connect(url)
            .config(false)
            .headers(mapOf("Referer" to "http://wap.fushu520.com/"))
            .data(requestParams)
            .post()

        val list = ArrayList<Book>()

        val elementList = doc.select(".book-ol > .book-li")
        elementList.forEach { item ->
            var coverUrl = item.selectFirst(".book-cover").attr("data-original")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://wap.fushu520.com$coverUrl"
            }
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst(".book-title").text().trim()
            val a = item.selectFirst(".book-meta").text().trim().split(" ")
            var author = ""
            var artist = ""
            if (a.isNotEmpty()) {
                author = a[0]
                if (a.size > 1) {
                    artist = a[1]
                }
            }
            val intro = item.selectFirst(".book-desc").text().trim()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(isDeskTop = false, script = "document.getElementById('play').contentDocument.getElementById('jp_audio_0').getAttribute('src')") {
            return@setUp it.replace("\"", "")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("玄幻有声", "http://wap.fushu520.com/sort/xuanhuan.html"),
                CategoryTab("相声小品", "http://wap.fushu520.com/sort/xiangsheng.html"),
                CategoryTab("综艺娱乐", "http://wap.fushu520.com/sort/yule.html"),
                CategoryTab("长篇评书", "http://wap.fushu520.com/sort/pingshu.html"),
                CategoryTab("都市有声", "http://wap.fushu520.com/sort/dushi.html"),
                CategoryTab("百家讲坛", "http://wap.fushu520.com/sort/bjjt.html"),
                CategoryTab("军事有声", "http://wap.fushu520.com/sort/junshi.html"),
                CategoryTab("儿童故事", "http://wap.fushu520.com/sort/ertong.html"),
                CategoryTab("网游竞技", "http://wap.fushu520.com/sort/jingji.html"),
                CategoryTab("科幻", "http://wap.fushu520.com/sort/kehuan.html"),
                CategoryTab("灵异有声", "http://wap.fushu520.com/sort/lingyi.html"),
                CategoryTab("职场有声", "http://wap.fushu520.com/sort/tongren.html"),
                CategoryTab("女生", "http://wap.fushu520.com/sort/nvsheng.html"),
                CategoryTab("其他有声", "http://wap.fushu520.com/sort/qita.html")
            )
        )

        val menu2 = CategoryMenu(
            "长篇评书", listOf(
                CategoryTab("单田芳", "http://wap.fushu520.com/boyin/119"),
                CategoryTab("田连元", "http://wap.fushu520.com/boyin/131"),
                CategoryTab("袁阔成", "http://wap.fushu520.com/boyin/123"),
                CategoryTab("连丽如", "http://wap.fushu520.com/boyin/103"),
                CategoryTab("张少佐", "http://wap.fushu520.com/boyin/120"),
                CategoryTab("孙一", "http://wap.fushu520.com/boyin/101"),
                CategoryTab("田战义", "http://wap.fushu520.com/boyin/121"),
                CategoryTab("粤语评书", "http://wap.fushu520.com/boyin/130"),
                CategoryTab("其他评书", "http://wap.fushu520.com/boyin/122")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url)
            .config(false)
            .headers(mapOf("Referer" to "http://wap.fushu520.com/"))
            .get()

        val nextUrl = doc.selectFirst(".paging > .next").absUrl("href")
        val totalPage = Regex(".+\\/(\\d+)\\.html").find(nextUrl)!!.groupValues[1].toInt()
        val currentPage = if (url == nextUrl) {
            totalPage
        } else {
            totalPage - 1
        }

        val list = ArrayList<Book>()

        val elementList = doc.select(".book-ol > .book-li")
        elementList.forEach { item ->
            var coverUrl = item.selectFirst(".book-cover").attr("data-original")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://wap.fushu520.com$coverUrl"
            }
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst(".book-title").text().trim()
            val a = item.selectFirst(".book-meta").text().trim().split(" ")
            var author = ""
            var artist = ""
            if (a.isNotEmpty()) {
                author = a[0]
                if (a.size > 1) {
                    artist = a[1]
                }
            }
            val intro = item.selectFirst(".book-desc").text().trim()
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
            val l = doc.select("#playlist > ul > li > a").map {
                val title = it.text()
                val url = it.absUrl("href")
                Episode(title, url)
            }
            episodes.addAll(l)
        }
        return BookDetail(episodes)
    }
}