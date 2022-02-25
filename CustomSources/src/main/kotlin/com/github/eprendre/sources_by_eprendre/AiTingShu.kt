package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.getCurrentBook
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URL

object AiTingShu : TingShu(), AudioUrlExtraHeaders, CoverUrlExtraHeaders {
    override fun getSourceId(): String {
        return "5267b4570f7e45f2903678227d2e7b44"
    }

    override fun getUrl(): String {
        return "https://www.2uxs.com"
    }

    override fun getName(): String {
        return "爱听书"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n有时候资源不稳定"
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "小说", listOf(
                CategoryTab("玄幻修真", "https://www.2uxs.com/yousheng/xuanhuan/lastupdate.html"),
                CategoryTab("灵异惊悚", "https://www.2uxs.com/yousheng/lingyi/lastupdate.html"),
                CategoryTab("都市言情", "https://www.2uxs.com/yousheng/dushi/lastupdate.html"),
                CategoryTab("军事历史", "https://www.2uxs.com/yousheng/junshi/lastupdate.html"),
                CategoryTab("儿童故事", "https://www.2uxs.com/yousheng/ertong/lastupdate.html"),
                CategoryTab("经典纪实", "https://www.2uxs.com/yousheng/jishi/lastupdate.html"),
                CategoryTab("网游竞技", "https://www.2uxs.com/yousheng/jingji/lastupdate.html")
            )
        )

        val menu2 = CategoryMenu(
            "其它", listOf(
                CategoryTab("长篇评书", "https://www.2uxs.com/yousheng/pingshu/lastupdate.html"),
                CategoryTab("相声戏曲", "https://www.2uxs.com/yousheng/xiangsheng/lastupdate.html"),
                CategoryTab("综艺娱乐", "https://www.2uxs.com/yousheng/yule/lastupdate.html"),
                CategoryTab("百家讲坛", "https://www.2uxs.com/yousheng/bjjt/lastupdate.html"),
                CategoryTab("职场商战", "https://www.2uxs.com/yousheng/tongren/lastupdate.html"),
                CategoryTab("人物传记", "https://www.2uxs.com/yousheng/chuanji/lastupdate.html"),
                CategoryTab("通俗文学", "https://www.2uxs.com/yousheng/wenxue/lastupdate.html"),
                CategoryTab("其他有声", "https://www.2uxs.com/yousheng/qita/lastupdate.html")
            )
        )
        return listOf(menu1, menu2)
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "https://www.2uxs.com/novelsearch/search/result.html"
        val doc = Jsoup.connect(url).config(true)
            .data(mapOf("searchtype" to "novelname", "searchword" to keywords))
            .post()

//        val pages = doc.selectFirst(".fanye").children().map { it.ownText() }.filter { it.matches("\\d+".toRegex()) }
//        val totalPage = pages.last().toInt()
        val totalPage = 1

        val list = ArrayList<Book>()
        val elementList = doc.select(".list-works li")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".list-imgbox img").absUrl("data-original")
            val titleElement = element.selectFirst(".list-book-dt a")
            val bookUrl = titleElement.absUrl("href")
            val title = titleElement.ownText()
            val (author, artist, status) = element.select(".list-book-cs .book-author").let {
                Triple(it[0].text(), it[1].text(), it[2].text().replace("更新情况：", ""))
            }

            val intro = element.selectFirst(".list-book-des").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val list = ArrayList<Episode>()
        var currentIntro = ""
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(true).get()
            val episodes = doc.select("#playlist > ul > li > a").map {
                Episode(it.text(), it.attr("abs:href"))
            }
            list.addAll(episodes)
            currentIntro = doc.selectFirst(".book-des").ownText().trim()
        }
        return BookDetail(list, currentIntro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp(true,
            "(function() { return ('<html>'+document.getElementsByName(\"play\")[0].contentDocument.documentElement.innerHTML+'</html>'); })();") { str ->
            val doc = Jsoup.parse(str)
            val audioElement = doc.getElementById("jp_audio_0")
            return@setUp audioElement?.attr("src")
        }
        return AudioUrlWebViewExtractor
//        AudioUrlWebViewSniffExtractor.setUp(true) { url ->
//            return@setUp (url.contains(".mp3", true) ||
//                    url.contains(".m4a", true) ||
//                    url.contains(".m4b", true) ||
//                    url.contains(".flac", true) ||
//                    url.contains(".aa3", true) ||
//                    url.contains(".ogg", true) ||
//                    url.contains(".wma", true) ||
//                    url.contains(".wav", true) ||
//                    url.contains(".aac", true) ||
//                    url.contains(".ac3", true) ||
//                    url.contains(".mp4", true))
//        }
//        return AudioUrlWebViewSniffExtractor
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(true).get()

        val nextUrl = doc.select(".fanye a").firstOrNull { it.text().contains("下页") }?.attr("abs:href") ?: ""
        val currentPage = doc.selectFirst(".fanye strong").ownText().toInt()
        val totalPage = doc.selectFirst(".fanye").children().map { it.ownText() }.filter { it.matches("\\d+".toRegex()) }.last().toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select(".list-works li")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".list-imgbox img").absUrl("data-original")
            val titleElement = element.selectFirst(".list-book-dt a")
            val bookUrl = titleElement.absUrl("href")
            val title = titleElement.ownText()
            val (author, artist, status) = element.select(".list-book-cs .book-author").let {
                Triple(it[0].text(), it[1].text(), it[2].text().replace("更新情况：", ""))
            }
            val intro = element.selectFirst(".list-book-des").text()
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
        if (audioUrl.contains("gongpa.com") || audioUrl.contains("2uxs.com") || audioUrl.contains("dongporen")) {
            hashMap["Referer"] = "https://www.2uxs.com/"
        }
        return hashMap
    }

    override fun coverHeaders(coverUrl: String, headers: MutableMap<String, String>): Boolean {
        if(coverUrl.contains("2uxs.com") || coverUrl.contains("xinexin.cn")) {
            headers["Referer"] = getCurrentBook()?.currentEpisodeUrl ?: "https://www.2uxs.com/"
            headers["Host"] = URL(coverUrl).host
            return true
        }
        return false
    }
}