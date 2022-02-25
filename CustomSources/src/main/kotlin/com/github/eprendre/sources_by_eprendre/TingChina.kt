package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.getCurrentBook
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URLEncoder

object TingChina : TingShu(), AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "5027db6a06934655a73aa99945034c97"
    }

    override fun getUrl(): String {
        return "https://www.tingchina.com"
    }

    override fun getName(): String {
        return "听中国"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n速度不错, 资源稳定"
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("玄幻奇幻", "http://www.tingchina.com/yousheng/lei_135_1.htm"),
                CategoryTab("网络热门", "http://www.tingchina.com/yousheng/lei_146_1.htm"),
                CategoryTab("科幻有声", "http://www.tingchina.com/yousheng/lei_128_1.htm"),
                CategoryTab("武侠小说", "http://www.tingchina.com/yousheng/lei_133_1.htm"),
                CategoryTab("都市言情", "http://www.tingchina.com/yousheng/lei_125_1.htm"),
                CategoryTab("鬼故事", "http://www.tingchina.com/yousheng/lei_129_1.htm"),
                CategoryTab("历史军事", "http://www.tingchina.com/yousheng/lei_130_1.htm"),
                CategoryTab("官场商战", "http://www.tingchina.com/yousheng/lei_126_1.htm"),
                CategoryTab("刑侦推理", "http://www.tingchina.com/yousheng/lei_134_1.htm"),
                CategoryTab("经典纪实", "http://www.tingchina.com/yousheng/lei_127_1.htm"),
                CategoryTab("通俗文学", "http://www.tingchina.com/yousheng/lei_132_1.htm"),
                CategoryTab("人物传记", "http://www.tingchina.com/yousheng/lei_131_1.htm")
            )
        )

        val menu2 = CategoryMenu(
            "评书大全", listOf(
                CategoryTab("单田芳", "http://www.tingchina.com/pingshu/lei_136_1.htm"),
                CategoryTab("田连元", "http://www.tingchina.com/pingshu/lei_141_1.htm"),
                CategoryTab("袁阔成", "http://www.tingchina.com/pingshu/lei_143_1.htm"),
                CategoryTab("连丽如", "http://www.tingchina.com/pingshu/lei_137_1.htm"),
                CategoryTab("张少佐", "http://www.tingchina.com/pingshu/lei_145_1.htm"),
                CategoryTab("孙一", "http://www.tingchina.com/pingshu/lei_140_1.htm"),
                CategoryTab("田战义", "http://www.tingchina.com/pingshu/lei_142_1.htm"),
                CategoryTab("粤语评书", "http://www.tingchina.com/pingshu/lei_144_1.htm"),
                CategoryTab("其他评书", "http://www.tingchina.com/pingshu/lei_139_1.htm")

            )
        )
        return listOf(menu1, menu2)
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val totalPage = 1
        val list = ArrayList<Book>()
        val categories = listOf("0", "1", "2")
        for (category in categories) {
            try {
                searchCategory(list, category, encodedKeywords)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return Pair(list, totalPage)
    }

    private fun searchCategory(list: ArrayList<Book>, category: String, keywords: String) {
        val url = "http://www.tingchina.com/search1.asp?mainlei=$category&lei=0&keyword=$keywords"
        val doc = Jsoup.connect(url).config(true).get()
        val elementList = doc.select(".singerlist1 dd ul li a")
        elementList.forEach { element ->
            val bookUrl = element.absUrl("href")
            val title = element.text()
            val book = Book("", bookUrl, title, "", "")
            book.sourceId = getSourceId()
            list.add(book)
        }
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        //获取章节列表
        val doc = Jsoup.connect(bookUrl).config(true).get()
        val episodes = doc.select(".main03 .list a").map {
            Episode(it.text(), it.attr("abs:href"))
        }
        var coverUrl = ""
        var author = ""
        var artist = ""
//        var status = ""
        var intro = ""
        val book01 = doc.selectFirst(".book01")
        if (book01 != null) {
            coverUrl = book01.selectFirst("img").absUrl("src")
            val lis = book01.select("ul li")
            author = lis[5].text()
            artist = lis[4].text()
//            status = lis[6].text()
            intro = doc.selectFirst(".book02").ownText()
        }
        val book03 = doc.selectFirst(".book03")
        if (book03 != null) {
            coverUrl = "none"
            val lis = book03.select("ul > li")
//            status = lis[1].text()
            intro = lis[3].text()
        }

        return BookDetail(episodes, intro, artist, author, coverUrl = coverUrl)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(true) { html ->
            val doc = Jsoup.parse(html)
            return@setUp doc.selectFirst("#wjAudio").attr("src")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(true).get()
        val pages = doc.selectFirst(".yema > span").children()
        val currentPage = Regex(".+lei_\\d+_(\\d+)\\.htm").find(url)!!.groupValues[1].toInt()
        var totalPage = currentPage
        if (pages.last().absUrl("href") != url) {
            totalPage = currentPage + 1
        }
        var nextUrl = ""
        if (currentPage != totalPage) {
            val index = pages.indexOfFirst { it.text() == currentPage.toString() }
            nextUrl = pages[index + 1].absUrl("href")
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".zimulist > dl")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("dt > a > img").absUrl("src")
            val titleElement = element.selectFirst("dd > a")
            val bookUrl = titleElement.absUrl("href")
            val title = titleElement.text().trim()
            val status = element.selectFirst("dd").ownText().split("　")[0]
            val author = ""
            val artist = ""
//            val (title, author, artist) = titleElement.text().split(" ").let {
//                val i = it[0].replace("《", "").replace("》", "")
//                val j = if (it.size > 2) it[1] else "" //大于2代表不是评书
//                val k = if (it.size > 2) {
//                    if (it.size > 3) {
//                        val temp = StringBuilder()
//                        (2 until (it.size - 1)).forEach { index ->
//                            temp.append(it[index])
//                            temp.append(" ")
//                        }
//                        temp.append(it.last().split("　")[0])
//                        temp.toString()
//                    } else {
//                        it[2].split("　")[0]
//                    }
//                } else ""
//
//                status = if (it.size > 2) {
//                    it.last().split("　")[1]
//                } else{
//                    it[1]
//                }
//                Triple(i, j, k)
//            }
//            val intro = element.selectFirst("dd .info").ownText()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("tingchina.com")) {
            hashMap["Referer"] = getCurrentBook()?.currentEpisodeUrl ?: "https://www.tingchina.com/"
        }
        return hashMap
    }
}