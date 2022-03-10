import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class YingHuaTest {

    @Test
    fun search() {
        val keywords = "我"
        val page = 1
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = if (page == 1) {
            "http://www.yinghuacd.com/search/${encodedKeywords}"
        } else {
            "http://www.yinghuacd.com/search/${encodedKeywords}/?page=${page}"
        }
        val doc = Jsoup.connect(url).testConfig(true).get()

        val currentPage = 1
        val totalPage = doc.selectFirst("#lastn")?.text()?.toInt() ?: 1

        println(currentPage)
        println(totalPage)

        val list = ArrayList<Book>()
        val elementList = doc.select(".lpic > ul > li")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst("a > img").attr("src")
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst("h2 > a").text()
            val status = item.selectFirst("span").text()
            val desc = item.selectFirst("p").text()
            val author = ""
            val artist = ""
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = desc
            })
        }
        println(list)
    }

    @Test
    fun bookDetail() {
        val bookUrl = "http://www.yinghuacd.com/show/5478.html"
        val doc = Jsoup.connect(bookUrl).testConfig(true).get()

        val episodes = ArrayList<Episode>()
        val elements = doc.select(".movurl > ul > li > a")
        elements.forEach { element ->
            val title = element.text()
            val url = element.absUrl("href")
            episodes.add(Episode(title, url))
        }
        episodes.reverse()
        println(episodes)
    }

    @Test
    fun categoryList() {
        val url = "http://www.yinghuacd.com/japan/"
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).testConfig(true).get()

        var currentPage = 1
        val totalPage = doc.selectFirst("#lastn")?.text()?.toInt() ?: 1
        var nextUrl = ""
        if (totalPage > 1) {
            nextUrl = doc.select(".pages > .a1").last()?.absUrl("href") ?: ""
            currentPage = doc.selectFirst(".pages > span")?.text()?.toInt() ?: 1
        }

        println(currentPage)
        println(totalPage)
        println(nextUrl)

        val elementList = doc.select(".lpic > ul > li")
        elementList.forEach { item ->
            val coverUrl = item.selectFirst("a > img").attr("src")
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst("h2 > a").text()
            val status = item.selectFirst("span").text()
            val desc = item.selectFirst("p").text()
            val author = ""
            val artist = ""
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = desc
            })
        }
        println(list)
    }

    @Test
    fun getPlayUrl() {
        val url = "http://www.yinghuacd.com/v/5478-1.html"
        val doc = Jsoup.connect(url).testConfig(true).get()
        val u = doc.selectFirst("#playbox").attr("data-vid")
            .split("$")[0]
        println(u)
    }
}