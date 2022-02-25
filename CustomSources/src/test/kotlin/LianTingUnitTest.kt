import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

/**
 * 恋听网测试
 */
class LianTingUnitTest {

    /**
     * 测试搜索
     */
    @Test
    fun search() {
        val keywords = "来自阴间"
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val page = 1
        val url = "https://m.ting55.com/search/$encodedKeywords/page/$page"
        val doc = Jsoup.connect(url).testConfig(false).get()

        val cpage = doc.selectFirst(".cpage")
        var totalPage = 1
        if (cpage != null && cpage.childrenSize() > 0) {
            totalPage = cpage.selectFirst("span").text().replace("页次 ", "").split("/")[1].toInt()
        }

        println(totalPage)
        val list = ArrayList<Book>()
        val elementList = doc.select(".slist > a")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("dl > dt > img").absUrl("src")
            val bookUrl = element.absUrl("href")
            val infos = element.selectFirst("dl > dd").children()
            val title = infos[0].text()
            val author = infos[1].text()
            val artist = infos[2].text()
            val status = infos[3].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply { this.status = status })
        }
        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    /**
     * 书籍详细
     */
    @Test
    fun bookDetail() {
        val doc = Jsoup.connect("https://m.ting55.com/book/14020").testConfig(false).get()

        val episodes = doc.select(".plist > a").map {
            Episode(it.text(), it.absUrl("href")).apply {
                this.isFree = it.hasClass("f")
            }
        }
        val intro = doc.selectFirst(".intro").text()
        println(intro)

        episodes.take(20).forEach { println(it) }
        assertThat(episodes.size).isGreaterThan(0)
    }

    /**
     * 分类
     */
    @Test
    fun category() {
        val doc = Jsoup.connect("https://m.ting55.com/category/1/page/6").testConfig(true).get()

        val cpage = doc.selectFirst(".cpage")
        var totalPage = 1
        var currentPage = 1
        var nextUrl = ""
        if (cpage != null) {
            val pages = cpage.selectFirst("span").text().replace("页次 ", "").split("/")
            currentPage = pages[0].toInt()
            totalPage = pages[1].toInt()
            cpage.select("a").firstOrNull { it.text() == "下一页" }?.let {
                nextUrl = it.absUrl("href")
            }
        }

        println("$currentPage/$totalPage")
        println("nextUrl: $nextUrl")

        val list = ArrayList<Book>()
        val elementList = doc.select(".clist > a")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("dl > dt > img").absUrl("src")
            val bookUrl = element.absUrl("href")
            val infos = element.selectFirst("dl > dd").children()
            val title = infos[0].text()
            val author = infos[1].text()
            val artist = infos[2].text()
            val status = infos[3].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
            })
        }

        list.take(5).forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun fetchCategory() {
        val doc = Jsoup.connect("https://ting55.com/").testConfig(true).get()
        val navs = doc.select(".nav > a")
            val sb = StringBuilder()

            val list = navs.map { a ->
                val href = a.absUrl("href")
                val text = a.text()
                return@map "CategoryTab(\"$text\", \"$href\")"
            }.joinToString(",\n")

            sb.append(list)
            println(sb.toString())
    }
}
