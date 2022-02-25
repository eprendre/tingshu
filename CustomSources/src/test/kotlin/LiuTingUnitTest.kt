import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

/**
 * 6听网测试
 */
class LiuTingUnitTest {

    /**
     * 测试搜索
     */
    @Test
    fun search() {
        val keywords = "来自阴间"
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val page = 1
        val url = "http://www.6ting.cn/search.php?page=${page}&searchword=${encodedKeywords}"
        val doc = Jsoup.connect(url).testConfig(false).get()

        val pager = doc.select(".pager > li")
        var totalPage = page
        if (pager.size > 0) {
            pager.firstOrNull { it.text().equals("下一页") }?.let {
                totalPage = page + 1
            }
        }

        println(totalPage)
        val list = ArrayList<Book>()
        val elementList = doc.select(".list-unstyled > li.ting-col")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("img").absUrl("src")
            val bookUrl = element.selectFirst("h4 > a").absUrl("href")
            val title = element.selectFirst("h4 > a").text()
            val l = element.select("h6 > a")
            val author = l.first().text()
            val artist = l.last().text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {

            })
        }
        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    /**
     * 书籍详细
     */
    @Test
    fun bookDetail() {
        val episodes = ArrayList<Episode>()
        val doc = Jsoup.connect("http://www.6ting.cn/books/4896.html").testConfig(false).get()
        val playlists = doc.select("#ting-tab-content > div > .play-list")
        playlists.forEach { element ->
            val l = element.select("li > a").map {
                Episode(it.text(), it.absUrl("href"))
            }
            if (l.size > episodes.size) {
                episodes.clear()
                episodes.addAll(l)
            }
        }


//        val episodes = doc.select(".play-list > li > a").map {
//            Episode(it.text(), it.absUrl("href"))
//        }
        println(episodes.size)
        episodes.take(20).forEach { println(it) }
        assertThat(episodes.size).isGreaterThan(0)
    }

    /**
     * 分类
     */
    @Test
    fun category() {
        val doc = Jsoup.connect("http://www.6ting.cn/booklist/1.html").testConfig(false).get()

        val pager = doc.select(".pager > li > a")
        var currentPage = 1
        var totalPage = 1
        var nextUrl = ""
        if (pager.size > 0) {
            pager.firstOrNull { it.text().equals("下一页") }?.let {
                totalPage = currentPage + 1
                nextUrl = it.absUrl("href")
            }
        }

        println("$currentPage/$totalPage")
        println("nextUrl: $nextUrl")
        val list = ArrayList<Book>()
        val elementList = doc.select(".list-unstyled > li.ting-col")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst("img").absUrl("src")
            val bookUrl = element.selectFirst("h4 > a").absUrl("href")
            val title = element.selectFirst("h4 > a").text()
            val l = element.select("h6 > a")
            val author = l.first().text()
            val artist = l.last().text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {

            })
        }

        list.take(5).forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun fetchCategory() {
        val doc = Jsoup.connect("http://www.6ting.cn/booklist/1.html").testConfig(false).get()
        val navs = doc.select("#sx > .list-unstyled > li > a")
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
