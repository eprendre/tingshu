import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

/**
 * 海洋听书网测试
 */
class HaiYangUnitTest {

    @Test
    fun search() {
        val keywords = "仙"
        val encodedKeywords = URLEncoder.encode(keywords, "gb2312")
        val url = "http://m.ychy.com/search.asp?page=1&searchword=$encodedKeywords"
        val doc = Jsoup.connect(url).testConfig().get()

        val totalPage = doc.selectFirst(".page").ownText().split("/")[1]
        println(totalPage)

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookId = element.attr("bookid")
            val bookUrl = "http://m.ychy.com/book/$bookId.html"
            val coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            val bookinfo = element.selectFirst(".bookinfo")
            val title = bookinfo.selectFirst(".bookname").text()
            val (author, artist) = bookinfo.selectFirst(".author").text().split(" ").let {
                Pair(it[0], it[1])
            }
            val intro = bookinfo.selectFirst(".intro_line").text()
            val status = bookinfo.selectFirst(".update").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
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
        val doc = Jsoup.connect("http://m.ychy.com/book/14689.html").testConfig().get()

        val episodes = doc.select("#playlist > ul > li > a").map {
            Episode(it.text(), it.attr("abs:href"))
        }

        val intro = doc.selectFirst(".book_intro").text()
        println(intro)

        episodes.take(10).forEach { println(it) }
        assertThat(episodes.size).isGreaterThan(0)
    }

    /**
     * 分类
     */
    @Test
    fun category() {
        val doc = Jsoup.connect("http://m.ychy.com/list/52.html").testConfig().get()
        val nextUrl = doc.selectFirst(".ychy_next").attr("abs:href") ?: ""
        val pages = doc.selectFirst(".page").ownText().let { text ->
            Regex("(\\d+)/(\\d+)").find(text)!!.groupValues
        }
        val currentPage = pages[1].toInt()
        val totalPage = pages[2].toInt()

        println("$currentPage/$totalPage")
        println("nextUrl: $nextUrl")

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
            })
        }
        list.take(5).forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun fetchCategory() {
        val doc = Jsoup.connect("http://m.ychy.com/category.html").testConfig().get()

        val str = doc.select(".fleiList > dl > dt > h2 > a").map { a ->
            val href = a.absUrl("href")
            val text = a.ownText()
            return@map "CategoryTab(\"$text\", \"$href\")"
        }.joinToString(",\n")

        println(str)
    }
}