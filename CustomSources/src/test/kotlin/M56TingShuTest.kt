import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

/**
 * 56听书网测试
 */
class M56TingShuTest {
    val userAgent = "Mozilla/5.0 (Android 4.4; Mobile; rv:46.0) Gecko/46.0 Firefox/46.0"

    /**
     * 源的名字
     */
    @Test
    fun name() {
        println("56听书网")
    }

    /**
     * 发现分类，如果没有则不用提供
     */
    @Test
    fun categories() {
        val doc = Jsoup.connect("http://m.ting56.com/mulu.html").testConfig(false).get()
        doc.select(".chan_box").forEach { box ->
            val title = box.selectFirst("h2").text()
            println("title: $title")
            box.select("ul > li > a").forEach { category ->
                println(category.text())
                println(category.absUrl("href"))
            }
        }

//        val cat1 = mapOf(
//            "玄幻武侠" to  "http://m.ting56.com/book/1.html",
//            "都市言情" to  "http://m.ting56.com/book/2.html",
//            "恐怖悬疑" to  "http://m.ting56.com/book/3.html",
//            "综艺娱乐" to  "http://m.ting56.com/book/45.html",
//            "网游竞技" to  "http://m.ting56.com/book/4.html",
//            "军事历史" to  "http://m.ting56.com/book/6.html",
//            "刑侦推理" to  "http://m.ting56.com/book/41.html"
//        )
//        println("有声小说: $cat1")
//        val cat2 = mapOf(
//            "单田芳" to  "http://m.ting56.com/byy/shantianfang.html",
//            "刘兰芳" to  "http://m.ting56.com/byy/liulanfang.html",
//            "袁阔成" to  "http://m.ting56.com/byy/yuankuocheng.html",
//            "田连元" to  "http://m.ting56.com/byy/tianlianyuan.html",
//            "连丽如" to  "http://m.ting56.com/byy/lianliru.html",
//            "王玥波" to  "http://m.ting56.com/byy/wangyuebo.html",
//            "孙一" to "http://m.ting56.com/byy/sunyi.html",
//            "更多" to "http://m.ting56.com/book/9.html"
//        )
//        println("评书: $cat2")
    }

    /**
     * 搜索
     * 需要返回：1.结果list， 2.总页数
     */
    @Test
    fun search() {
        val keywords = "仙" //搜索关键词
        val page = 1 //当前页数
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312") //编码
        val url = "http://m.ting56.com/search.asp?searchword=$encodedKeywords&page=$page"

        val list = ArrayList<Book>()
        var totalPage = 1

        val doc = Jsoup.connect(url).userAgent(userAgent).get()
        totalPage = doc.selectFirst("#page_num1").text().split("/")[1].toInt()
        val elementList = doc.select(".xsdz > .list-ov-tw")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst(".list-ov-t a img").attr("original")
            val ov = item.selectFirst(".list-ov-w")
            val bookUrl = ov.selectFirst(".bt a").attr("abs:href")
            val title = ov.selectFirst(".bt a").text()
            val (author, artist) = ov.select(".zz").let { element ->
                Pair(element[0].text(), element[1].text())
            }
            val status = ""
            val intro = ov.selectFirst(".nr").text()
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                )
            )
        }

        println("总页数: $totalPage")
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    /**
     * 书籍详情页，包含更多的书籍信息，以及章节列表。
     * 章节列表需要返回，其它书籍信息看情况返回
     */
    @Test
    fun bookDetail() {
        val bookUrl = "http://m.ting56.com/mp3/4826.html"
        val doc = Jsoup.connect(bookUrl).userAgent(userAgent).get()

        //获取章节列表
        val episodes = doc.getElementById("playlist")
            .getElementsByTag("a")
            .map {
                Episode(it.text(), it.attr("abs:href"))
            }
        val intro = doc.selectFirst(".book_intro").ownText()
        episodes.take(10).forEach {
            println(it)
        }
        println("详细简介:$intro")
        assert(episodes.isNotEmpty())
    }

    /**
     * 56听书网不能直接通过jsoup获取音频地址，但最好也描述一下音频地址所在的位置
     */
    @Test
    fun audioUrl() {
        val doc = Jsoup.connect("blablabla").get()
        val audioUrl = doc.selectFirst("#jp_audio_0")?.attr("src")
    }

    /**
     * 分类列表获取
     * 需要返回：1. 下一页地址 2. 当前页数 3. 总页数 4. 书籍list
     */
    @Test
    fun categoryList() {
        val url = "http://m.ting56.com/book/1.html"
        var currentPage = 1
        var totalPage = 1

        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).userAgent(userAgent).get()
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
            val status = ""
            val intro = ov.selectFirst(".nr").text()
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                )
            )
        }
        println(nextUrl)
        println("$currentPage / $totalPage")
        list.take(5).forEach { println(it) }
        assert(list.isNotEmpty())
    }
}