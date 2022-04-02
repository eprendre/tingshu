package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object M56TingShu : TingShu() {
    override fun getSourceId(): String {
        return "0ea16a8a11fb4d1b93550d059b5f1b5a"
    }

    override fun getUrl(): String {
        return "http://m.ting56.com"
    }

    override fun getName(): String {
        return "56听书网"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n搜索已失效，而且打开速度极慢科学上网可提高速度。"
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu("有声小说", listOf(
            CategoryTab("玄幻武侠", "http://m.ting56.com/book/1.html"),
            CategoryTab("都市言情", "http://m.ting56.com/book/2.html"),
            CategoryTab("恐怖悬疑", "http://m.ting56.com/book/3.html"),
            CategoryTab("综艺娱乐", "http://m.ting56.com/book/45.html"),
            CategoryTab("网游竞技", "http://m.ting56.com/book/4.html"),
            CategoryTab("军事历史", "http://m.ting56.com/book/6.html"),
            CategoryTab("刑侦推理", "http://m.ting56.com/book/41.html")
        )
        )
        val menu2 = CategoryMenu("评书", listOf(
            CategoryTab("单田芳", "http://m.ting56.com/byy/shantianfang.html"),
            CategoryTab("刘兰芳", "http://m.ting56.com/byy/liulanfang.html"),
            CategoryTab("袁阔成", "http://m.ting56.com/byy/yuankuocheng.html"),
            CategoryTab("田连元", "http://m.ting56.com/byy/tianlianyuan.html"),
            CategoryTab("连丽如", "http://m.ting56.com/byy/lianliru.html"),
            CategoryTab("王玥波", "http://m.ting56.com/byy/wangyuebo.html"),
            CategoryTab("孙一", "http://m.ting56.com/byy/sunyi.html"),
            CategoryTab("更多", "http://m.ting56.com/book/9.html")
        )
        )
        val menu3 = CategoryMenu("其它", listOf(
            CategoryTab("职场商战", "http://m.ting56.com/book/7.html"),
            CategoryTab("百家讲坛", "http://m.ting56.com/book/10.html"),
            CategoryTab("广播剧", "http://m.ting56.com/book/40.html"),
            CategoryTab("幽默笑话", "http://m.ting56.com/book/44.html"),
            CategoryTab("相声", "http://m.ting56.com/book/43.html"),
            CategoryTab("儿童读物", "http://m.ting56.com/book/11.html")
        )
        )
        return listOf(menu1, menu2, menu3)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp { html ->
            val doc = Jsoup.parse(html)
            val audioElement = doc.getElementById("jp_audio_0")
            var audioUrl = audioElement?.attr("src") ?: ""
            if (audioUrl.contains("tingchinakey.php?url=")) {
                audioUrl = audioUrl.split("tingchinakey.php?url=")[1]
            }
            return@setUp audioUrl
        }
        return AudioUrlWebViewExtractor
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config().get()
//            val book = doc.getElementsByClass("list-ov-tw").first()
//            val cover = book.getElementsByTag("img").first().attr("src")
        //获取书本信息
//            val bookInfos = book.getElementsByTag("span").map { it.text() }
//            Prefs.currentBookName = bookInfos[0]
//            Prefs.author = bookInfos[2]
//            Prefs.artist = bookInfos[3]

        //获取章节列表
        val episodes = doc.getElementById("playlist")
            .getElementsByTag("a")
            .map {
                Episode(it.text(), it.attr("abs:href"))
            }
        val currentIntro = doc.selectFirst(".book_intro").ownText()

        return BookDetail(episodes, currentIntro)
    }

    override fun getCategoryList(url: String): Category {
        var currentPage = 1
        var totalPage = 1

        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).config().get()
        doc.getElementById("page_num1")?.text()?.split("/")?.let {
            currentPage = it[0].toInt()
            totalPage = it[1].toInt()
        }
        val nextUrl = doc.getElementById("page_next1")?.attr("abs:href") ?: ""
        val elementList = doc.getElementsByClass("list-ov-tw")
        elementList.forEach { item ->
            var coverUrl = item.selectFirst(".list-ov-t a img").attr("original")
            if (coverUrl.startsWith("/")) {//有些网址已拼接好，有些没有拼接
                //这里用主站去拼接，因为用http://m.ting56.com/拼接时经常封面报错
                coverUrl = "http://www.ting56.com$coverUrl"
            }
            val ov = item.selectFirst(".list-ov-w")
            val bookUrl = ov.selectFirst(".bt a").attr("abs:href")
            val title = ov.selectFirst(".bt a").text()
            val (author, artist) = ov.select(".zz").let { element ->
                Pair(element[0].text(), element[1].text())
            }
            val intro = ov.selectFirst(".nr").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        //            var currentPage: Int
        var totalPage: Int
        val url = "http://m.ting56.com/search.asp?searchword=${URLEncoder.encode(keywords, "gb2312")}&page=$page"
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).config().get()
        val container = doc.selectFirst(".xsdz")
        container.getElementById("page_num1").text().split("/").let {
            //                currentPage = it[0].toInt()
            totalPage = it[1].toInt()
        }
        val elementList = container.getElementsByClass("list-ov-tw")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst(".list-ov-t a img").attr("original")
            val ov = item.selectFirst(".list-ov-w")
            val bookUrl = ov.selectFirst(".bt a").attr("abs:href")
            val title = ov.selectFirst(".bt a").text()
            val (author, artist) = ov.select(".zz").let { element ->
                Pair(element[0].text(), element[1].text())
            }
            val intro = ov.selectFirst(".nr").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, totalPage)
    }
}
