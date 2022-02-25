package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.getDesktopUA
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder
import kotlin.collections.ArrayList

object TingShu74 : TingShu(){

    override fun getSourceId(): String {
        return "e23e50167d0847278320a7204adb088c"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n" +
                "资源比较多，但是大多数都来自于听中国"
    }

    override fun getUrl(): String {
        return "http://www.ting74.com/"
    }

    override fun getName(): String {
        return "74听书"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "http://www.ting74.com/search.html"
        val encodedKeywords = URLEncoder.encode(keywords, "utf8")
        val geturl = "$url?searchtype=name&searchword=$encodedKeywords&page=$page"
        val doc = getUrl(geturl)
        val list = ArrayList<Book>()
        doc.select(".list-works > li").forEach {
            val coverUrl = it.selectFirst("div > a > img").absUrl("data-original")
            val bookUrl = it.selectFirst("div > a").absUrl("href")
            val title = it.selectFirst(".list-book-dt > a").text()
            val info = it.selectFirst(".list-book-des").text()
            var author = it.select(".list-book-cs > span").first().text()
            var artist = it.select(".list-book-cs > span")[1].text()
            var status = it.select(".list-book-cs > span")[2].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
                this.intro = info
            })
        }
        return if(list.count() == 0) {
            Pair(list, page)
        }else{
            val uiPage = doc.selectFirst(".fanye")
            val nextUrl = uiPage.select("a").last().absUrl("href")
            var currentPage = uiPage.selectFirst("strong").text()
            val totoalPage = nextUrl.substringAfterLast("=")
            println("$nextUrl,$currentPage,$totoalPage")
            Pair(list, totoalPage.toInt())
        }
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(script = "(function() { return ('<html>'+document.getElementById(\"play\").contentDocument.documentElement.innerHTML+'</html>'); })();") { html ->
            val doc = Jsoup.parse(html)
            val audioElement = doc.getElementById("jp_audio_0")
            audioElement?.attr("src")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val url = "http://www.ting74.com/"
        val doc = getUrl(url)
        val list = ArrayList<CategoryMenu>()
        val menus = ArrayList<CategoryTab>()
        doc.select(".nav-ol > li").map{
            val title = it.text()
            if(title != "首页" && title != "排行榜" && title != "完本榜"){
                menus.add( CategoryTab(title, it.selectFirst("a").absUrl("href")) )
            }
        }
        return listOf(CategoryMenu("列表",menus))
    }

    override fun getCategoryList(url: String): Category {
        val doc = getUrl(url)
        val list = ArrayList<Book>()
        doc.select(".list-works > li").forEach {
            val coverUrl = it.selectFirst("div > a > img").absUrl("data-original")
            val bookUrl = it.selectFirst("div > a").absUrl("href")
            val title = it.selectFirst(".list-book-dt > a").text()
            val info = it.selectFirst(".list-book-des").text()

            var author = it.select(".list-book-cs > span").first().text()
            var artist = it.select(".list-book-cs > span")[1].text()
            var status = it.select(".list-book-cs > span")[2].text()

            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
                this.intro = info
            })
        }
        val uiPage = doc.selectFirst(".fanye")
        val nextUrl = uiPage.select("a").last().absUrl("href")
        var currentPage = uiPage.selectFirst("strong").text()
        val totoalPage = nextUrl.substringAfterLast("/").substringBeforeLast(".")
        return Category(list,currentPage.toInt(),totoalPage.toInt(),url,nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        var info = ""
        if(loadEpisodes) {
            val doc = getUrl(bookUrl)
            doc.select(".playlist > ul > li").forEach {
                episodes.add(Episode(it.text(), it.select("a").first().absUrl("href")))
            }
            info = doc.selectFirst(".book-des").text()
        }
        return BookDetail(episodes, info)
    }

    private fun getUrl(url:String): Document {
        val conn = Jsoup.connect(url)
//        conn.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36 Edg/87.0.664.41")
        conn.header("User-Agent", getDesktopUA())
        return conn.get()
    }

}