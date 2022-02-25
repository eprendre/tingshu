package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.getCookie
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import org.jsoup.Jsoup
import java.net.URLEncoder

object HaiYangTingShu : TingShu(), AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "fc7a225fc7414567a87b8118e105eb56"
    }

    override fun getUrl(): String {
        return "http://m.ychy.com"
    }

    override fun getName(): String {
        return "海洋听书网"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val url = "http://m.ychy.com/search.asp?page=$page&searchword=$encodedKeywords"
        val doc = Jsoup.connect(url).config().get()
        val totalPage = doc.selectFirst(".page").ownText().split("/")[1].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookId = element.attr("bookid")
            val bookUrl = "http://m.ychy.com/book/$bookId.html"
            val coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            val bookinfo = element.selectFirst(".bookinfo")
            val title = bookinfo.selectFirst(".bookname").text()
            val (author, artist) = bookinfo.selectFirst(".author").text().split(" ").let {
                Pair(it[1].replace("播音:", "作者: "), it[0].replace("作者：", "播音: "))
            }
            val intro = bookinfo.selectFirst(".intro_line").text()
            val status = bookinfo.selectFirst(".update").text()
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
        val episodes = doc.select("#playlist > ul > li > a").map {
            Episode(it.text(), it.attr("abs:href"))
        }

        val intro = doc.selectFirst(".book_intro").text()
        return BookDetail(episodes, intro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
//        AudioUrlWebViewExtractor.setUp(
//            script = "(function() { return ('<html>'+document.getElementById('xplayer').contentDocument.getElementById('viframe').contentDocument.documentElement.innerHTML+'</html>'); })();") { str ->
//            val doc = Jsoup.parse(str)
//            val audioElement = doc.selectFirst("audio")
//            var audioUrl = audioElement?.attr("src")
//            try {
//                if (!audioUrl.isNullOrEmpty() && audioUrl.contains("ysting.ysxs8.com")) {
//                    val result = Fuel.get("http://ysting.ysxs8.com:81/_sys_vw.vhtml?js=yes")
//                        .responseString()
//                        .third
//                        .get()
//                    val vsid = Regex("VW_VSID=\"(.+)\";").find(result)?.groupValues?.get(1)
//                    if (vsid != null) {
//                        audioUrl += "?vsid=$vsid"
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return@setUp audioUrl
//        }
        return AudioUrlWebViewSniffExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("网络玄幻", "http://m.ychy.com/list/52.html"),
                CategoryTab("恐怖悬疑", "http://m.ychy.com/list/17.html"),
                CategoryTab("传统武侠", "http://m.ychy.com/list/12.html"),
                CategoryTab("都市言情", "http://m.ychy.com/list/13.html"),
                CategoryTab("官场刑侦", "http://m.ychy.com/list/14.html"),
                CategoryTab("有声文学", "http://m.ychy.com/list/41.html"),
                CategoryTab("探险盗墓", "http://m.ychy.com/list/45.html")
            )
        )
        val menu2 = CategoryMenu(
            "其他", listOf(
                CategoryTab("评书", "http://m.ychy.com/list/3.html"),
                CategoryTab("儿童读物", "http://m.ychy.com/list/4.html"),
                CategoryTab("历史军事", "http://m.ychy.com/list/15.html"),
                CategoryTab("人物传记", "http://m.ychy.com/list/16.html"),
                CategoryTab("广播剧", "http://m.ychy.com/list/18.html"),
                CategoryTab("百家讲坛", "http://m.ychy.com/list/32.html"),
                CategoryTab("职场商战", "http://m.ychy.com/list/81.html")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val nextUrl = doc.selectFirst(".ychy_next").attr("abs:href") ?: ""
        val pages = doc.selectFirst(".page").ownText().let { text ->
            Regex("(\\d+)/(\\d+)").find(text)!!.groupValues
        }
        val currentPage = pages[1].toInt()
        val totalPage = pages[2].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select("#infocon > li > a")
        elementList.forEach { element ->
            val bookUrl = element.absUrl("href")
            val coverUrl = element.selectFirst("img").attr("src")
            val bookInfo = element.selectFirst(".del")
            val title = bookInfo.selectFirst(".tit").text()
            val (author, artist) = bookInfo.selectFirst(".author").text().split("/").let {
                Pair(it[1].trim(), it[0].trim())
            }
            val intro = bookInfo.selectFirst(".desc").text()
            val status = bookInfo.selectFirst("p > span").text()
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
        if (audioUrl.contains("ysxs8") || audioUrl.contains("ychy")) {
            hashMap["Referer"] = "http://m.ychy.com/"
            hashMap["Cookie"] = getCookie("http://m.ychy.com/") ?: ""
        }
        return hashMap
    }

}