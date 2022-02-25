import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class Wap520Test {

    @Test
    fun search() {
        val keywords = "凡人修仙传"

        val url = "http://wap.fushu520.com/search.html"
        val requestParams = mapOf("searchword" to keywords, "searchtype" to "novelname")

        val doc = Jsoup.connect(url)
            .testConfig(false)
            .headers(mapOf("Referer" to "http://wap.fushu520.com/"))
            .data(requestParams)
            .post()

        val list = ArrayList<Book>()

        val elementList = doc.select(".book-ol > .book-li")
        elementList.forEach { item ->
            var coverUrl = item.selectFirst(".book-cover").attr("data-original")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://wap.fushu520.com$coverUrl"
            }
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst(".book-title").text().trim()
            val a = item.selectFirst(".book-meta").text().trim().split(" ")
            var author = ""
            var artist = ""
            if (a.isNotEmpty()) {
                author = a[0]
                if (a.size > 1) {
                    artist = a[1]
                }
            }
            val intro = item.selectFirst(".book-desc").text().trim()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
            })
        }

        println(list)
    }

    @Test
    fun bookDetail() {
        val bookUrl = "http://wap.fushu520.com/tingshu/9200/"
        val doc = Jsoup.connect(bookUrl).testConfig(false).get()
        val episodes = doc.select("#playlist > ul > li > a").map {
            val title = it.text()
            val url = it.absUrl("href")
            Episode(title, url)
        }
        println(episodes)
    }

    @Test
    fun audioUrl() {
        val url = "http://wap.fushu520.com/mp3/9200/1.html"
        val doc = Jsoup.connect(url).testConfig(false).get()
        println(doc)
    }

    @Test
    fun categoryList() {
        val url = "http://wap.fushu520.com/sort/xuanhuan.html"

        val doc = Jsoup.connect(url)
            .testConfig(false)
            .headers(mapOf("Referer" to "http://wap.fushu520.com/"))
            .get()

        val nextUrl = doc.selectFirst(".paging > .next").absUrl("href")
        val totalPage = Regex(".+\\/(\\d+)\\.html").find(nextUrl)!!.groupValues[1].toInt()
        val currentPage = if (url == nextUrl) {
            totalPage
        } else {
            totalPage - 1
        }

        val list = ArrayList<Book>()

        val elementList = doc.select(".book-ol > .book-li")
        elementList.forEach { item ->
            var coverUrl = item.selectFirst(".book-cover").attr("data-original")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://wap.fushu520.com$coverUrl"
            }
            val bookUrl = item.selectFirst("a").absUrl("href")
            val title = item.selectFirst(".book-title").text().trim()
            val a = item.selectFirst(".book-meta").text().trim().split(" ")
            var author = ""
            var artist = ""
            if (a.isNotEmpty()) {
                author = a[0]
                if (a.size > 1) {
                    artist = a[1]
                }
            }
            val intro = item.selectFirst(".book-desc").text().trim()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
            })
        }

        println(list)
    }

    @Test
    fun menus() {
        val doc = Jsoup.connect("http://wap.fushu520.com/sort/").get()
        val items = doc.select(".pd-module-box > dl > dd > a")
        items.forEach {
            val title = it.text()
            val url = it.absUrl("href")
            println("CategoryTab(\"${title}\", \"${url}\"),")
        }
    }
}