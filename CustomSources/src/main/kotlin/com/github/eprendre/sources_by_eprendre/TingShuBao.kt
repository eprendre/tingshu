package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.*

object TingShuBao : TingShu(){

    override fun getSourceId(): String {
        return "4c099c03129640fe8416d920ea6ae842"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n" +
                "PC端的炸了，此为移动端，可以正常使用"
    }

    override fun getUrl(): String {
        return "http://m.tingshubao.com/fenlei.html"
    }

    override fun getName(): String {
        return "听书宝"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "http://m.tingshubao.com/search.asp"
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val geturl = "$url?page=$page&searchword=$encodedKeywords&searchtype=-1"
        val doc = getUrl(geturl, geturl)
        val uiPage = doc.select(".paging > a").last()
        var nextPage = 1
        if(uiPage != null){
            val uiList = uiPage.absUrl("href")
            nextPage = uiList.replaceBefore("=","").replaceAfter("&","").replace("=","").replace("&","").toInt()
        }
        val list = ArrayList<Book>()
        doc.select(".book-ol > li").forEach {
            val coverUrl = it.selectFirst("a > img").absUrl("data-original")
            val bookUrl = it.selectFirst("a").absUrl("href")
            val title = it.select(".book-cell > .book-title").text().trim()
            var author = it.select(".book-cell > .book-meta").text().split("著")[0]+"著)".trim()
            var artist = it.select(".book-cell > .book-meta").text().split("著")[1].substring(1).trim()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.sourceId = getSourceId()
            })
        }
        return if(nextPage >= page)
            Pair(list, nextPage)
        else
            Pair(list, page)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
//        AudioUrlWebViewSniffExtractor.setUp { url ->
//            url.contains(".mp3", true) || url.contains(".m4a", true) || url.contains(".php",true)
//        }
//        return AudioUrlWebViewSniffExtractor

        AudioUrlWebViewExtractor.setUp { html ->
            val doc = Jsoup.parse(html)
            val audioElement = doc.getElementById("jp_audio_0")
            audioElement?.attr("src")
        }
        return AudioUrlWebViewExtractor

    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val url = "http://m.tingshubao.com/fenlei.html"
        val doc = getUrl(url, "http://m.tingshubao.com/")
        val list = ArrayList<CategoryMenu>()
        doc.select(".module > .pd-module-box").map{
            val title = it.select(".pd-class > dt > a").text()
            val subMenu = it.select(".pd-class > dd > a").map{
                val titles = it.text()
                val urls = it.absUrl("href")
                CategoryTab(titles, urls)
            }
            list.add(CategoryMenu(title, subMenu))
        }
        return list
    }

    override fun getCategoryList(url: String): Category {
        val doc = getUrl(url, "http://m.tingshubao.com/fenlei.html")
        val uiPage = doc.select(".paging > a").last()
        if(uiPage != null){
            val nextUrl = uiPage.absUrl("href")
            val index = url.lastIndexOf("_")
            var str1 = url.substring(url.lastIndexOf("_") + 1).substringBefore(".")
            if(index == -1) str1 = "1"
            val currentPageNum = try {
                str1.toInt()
            }catch (e: Exception) {
                1
            }
            var str2 = nextUrl.substring(nextUrl.lastIndexOf("_") + 1).substringBefore(".")
            val totalPage = str2.toInt()
            val list = ArrayList<Book>()
            doc.select(".book-ol > li").forEach {
                val coverUrl = it.selectFirst("a > img").absUrl("data-original")
                val bookUrl = it.selectFirst("a").absUrl("href")
                val title = it.select(".book-cell > .book-title").text().trim()
                var author = it.select(".book-cell > .book-meta").text().split("著")[0]+"著)".trim()
                var artist = it.select(".book-cell > .book-meta").text().split("著")[1].substring(1).trim()
                val intro = it.selectFirst(".book-desc").text()
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.intro = intro
                    this.sourceId = getSourceId()
                })
            }
            return Category(list, currentPageNum, totalPage, url, nextUrl)
        }else{
            val list = ArrayList<Book>()
            doc.select(".list-ul > li").forEach {
                val coverUrl = it.selectFirst("a > img").absUrl("data-original")
                val bookUrl = it.selectFirst("a").absUrl("href")
                val title = it.select(".list-name > a").text().trim()
                var author = ""
                var artist = "演播：" + it.select(".module-slide-author > a").text()
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.sourceId = getSourceId()
                })
            }
            return Category(list, 1, 1, url, url)
        }
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        var info = ""
        if(loadEpisodes) {
            val doc = getUrl(bookUrl, bookUrl)
            doc.select(".play-list > ul > li").forEach {
                episodes.add(Episode(it.text(), it.select("a").first().absUrl("href")))
            }
            info = doc.selectFirst(".book-des").text()
        }
        return BookDetail(episodes, info)
    }

    private fun getUrl(url: String, ref: String):org.jsoup.nodes.Document{
        val conn = Jsoup.connect(url).config()
        conn.header("Upgrade-Insecure-Requests", "1")
        conn.referrer(ref)
        return conn.get()
    }
}