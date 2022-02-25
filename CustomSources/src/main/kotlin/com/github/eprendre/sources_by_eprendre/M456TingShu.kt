package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object M456TingShu :TingShu(){
    override fun getSourceId(): String {
        return "ba2346b90bc64a66bdae51388ade480a"
    }

    override fun getUrl(): String {
        return "http://m.ting456.com/"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐"
    }

    override fun getName(): String {
        return "456听书网"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "http://m.ting456.com/search.php?searchword=${encodedKeywords}&Submit="
        val doc = Jsoup.connect(url).config(false).get()
        val totalPage = 1

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookid = element.attr("bookid")
            var coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            if (coverUrl.startsWith("/")) {
                coverUrl = "https://ting456.com${coverUrl}"
            }
            val bookUrl = "http://m.ting456.com/book/d${bookid}.html"
            val title = element.selectFirst(".bookname").text()
            val author = element.selectFirst(".author > a")?.text() ?: ""
            val artist = ""
            val status = element.selectFirst(".update").text()
            val intro = element.selectFirst(".intro_line").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
                this.intro = intro
                this.status = status
            })
        }

        return Pair(list, totalPage)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(script = "document.getElementById('cciframe').contentDocument.querySelector('iframe[runat=server]').contentDocument.querySelector('#jp_audio_0').src") {
            return@setUp it.replace("\"", "")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("玄幻", "http://m.ting456.com/fenlei/c1.html"),
                CategoryTab("武侠", "http://m.ting456.com/fenlei/c2.html"),
                CategoryTab("穿越", "http://m.ting456.com/fenlei/c3.html"),
                CategoryTab("都市", "http://m.ting456.com/fenlei/c4.html"),
                CategoryTab("言情", "http://m.ting456.com/fenlei/c5.html"),
                CategoryTab("恐怖", "http://m.ting456.com/fenlei/c6.html"),
                CategoryTab("惊悚", "http://m.ting456.com/fenlei/c7.html"),
                CategoryTab("科幻", "http://m.ting456.com/fenlei/c8.html"),
                CategoryTab("网游", "http://m.ting456.com/fenlei/c9.html"),
                CategoryTab("推理", "http://m.ting456.com/fenlei/c10.html"),
                CategoryTab("儿童", "http://m.ting456.com/fenlei/c11.html"),
                CategoryTab("相声", "http://m.ting456.com/fenlei/c12.html"),
                CategoryTab("小品", "http://m.ting456.com/fenlei/c13.html"),
                CategoryTab("历史", "http://m.ting456.com/fenlei/c14.html"),
                CategoryTab("百家讲坛", "http://m.ting456.com/fenlei/c15.html"),
                CategoryTab("经典", "http://m.ting456.com/fenlei/c16.html"),
                CategoryTab("评书", "http://m.ting456.com/fenlei/c18.html"),
                CategoryTab("戏曲", "http://m.ting456.com/fenlei/c19.html"),
                CategoryTab("学习", "http://m.ting456.com/fenlei/c22.html"),
                CategoryTab("职场", "http://m.ting456.com/fenlei/c23.html"),
                CategoryTab("娱乐", "http://m.ting456.com/fenlei/c24.html"),
                CategoryTab("官商", "http://m.ting456.com/fenlei/c25.html"),
                CategoryTab("英语", "http://m.ting456.com/fenlei/c26.html"),
                CategoryTab("微声音", "http://m.ting456.com/fenlei/c17.html"),
                CategoryTab("有声小说", "http://m.ting456.com/fenlei/c29.html"),
                CategoryTab("热门", "http://m.ting456.com/fenlei/c30.html")
            )
        )
        return listOf(menu1)

    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(false).get()
        var totalPage = 1
        var currentPage = 1

        val page = doc.selectFirst(".page")
        page.ownText().replace("页次 ", "").split("/").let {
            currentPage = it[0].toInt()
            totalPage = it[1].toInt()
        }
        val nextUrl = page.selectFirst("a")?.absUrl("href") ?: ""

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookid = element.attr("bookid")
            var coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://m.ting456.com${coverUrl}"
            }
            val bookUrl = "http://m.ting456.com/book/d${bookid}.html"
            val title = element.selectFirst(".bookname").text()
            val author = element.selectFirst(".author > a")?.text() ?: ""
            val artist = ""
            val status = element.selectFirst(".update").text()
            val intro = element.selectFirst(".intro_line").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
                this.intro = intro
                this.status = status
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        var episodes = emptyList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(false).get()
            doc.select("#playlist").forEach { element ->
                val e = element.select("li > a").map {
                    Episode(it.text(), it.absUrl("href"))
                }.toList()
                if (e.size > episodes.size) {
                    episodes = e
                }
            }

        }
        return BookDetail(episodes)
    }
}