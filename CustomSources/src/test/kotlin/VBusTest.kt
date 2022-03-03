import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test

class VBusTest {
    @Test
    fun audioUrl() {
        val doc = Jsoup.connect("https://www.vbus.cc/v/472").testConfig(false).get()
        println(doc.selectFirst("audio").absUrl("src"))
    }

    @Test
    fun categoryList() {
        val url = "https://www.vbus.cc/recommend/1"
        val doc = Jsoup.connect(url).testConfig(false).get()
        val pages = doc.select(".page-item > a")
        val nextPage = pages.firstOrNull { it.text().contains("下一页") }
        var nextUrl = ""
        if (nextPage != null) {
            nextUrl = nextPage.absUrl("href")
        }
        val currentPage = url.split("/").last().toInt()
        val totalPage = if (nextPage == null) {
            currentPage
        } else {
            currentPage + 1
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".program-list > li")
        elementList.forEach { element ->
            val a = element.selectFirst("h3 > a")
            val bookUrl = a.absUrl("href")
            val coverUrl = ""
            val title = a.text()
            val programMeta = element.selectFirst(".program-meta")
            val status = programMeta.select("span").last().text()
            val artist = programMeta.select("span").first().text()
            val author = programMeta.select("> a").joinToString(separator = ",") { it.text() }
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
            })
        }
        println(nextUrl)
        println("$currentPage / $totalPage")
        list.take(5).forEach { println(it) }
        assert(list.isNotEmpty())
    }
}