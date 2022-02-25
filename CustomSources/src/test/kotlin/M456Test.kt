import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class M456Test {
    @Test
    fun search() {
        val keywords = "修仙"
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val page = 1
        val url = "http://m.ting456.com/search.php?searchword=${encodedKeywords}&Submit="
        val doc = Jsoup.connect(url).testConfig(false).get()
        val totalPage = 1

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookid = element.attr("bookid")
            var coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            if (coverUrl.startsWith("/")) {
                coverUrl = "https://ting456.com${coverUrl}"
            }
            val bookUrl = "http://m.ting456.com/book/d${bookid}.html"
            val title = element.selectFirst(".bookname").text()
            val author = element.selectFirst(".author > a").text()
            val artist = ""
            val status = element.selectFirst(".update").text()
            val intro = element.selectFirst(".intro_line").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
            })
        }
        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun bookDetail() {
        val doc = Jsoup.connect("http://m.ting456.com/book/d5384.html").testConfig(false).get()

        var episodes = emptyList<Episode>()
        doc.select("#playlist").forEach { element ->
            val e = element.select("li > a").map {
                Episode(it.text(), it.absUrl("href"))
            }.toList()
            if (e.size > episodes.size) {
                episodes = e
            }
        }
        println(episodes.size)

        episodes.take(20).forEach { println(it) }
        assertThat(episodes.size).isGreaterThan(0)
    }

    @Test
    fun category() {
        val doc = Jsoup.connect("http://m.ting456.com/fenlei/c2.html").testConfig(false).get()
        var totalPage = 1
        var currentPage = 1

        val page = doc.selectFirst(".page")
        page.ownText().replace("页次 ", "").split("/").let {
            currentPage = it[0].toInt()
            totalPage = it[1].toInt()
        }
        val nextUrl = page.selectFirst("a")?.absUrl("href") ?: ""
        println("$currentPage/$totalPage")
        println("nextUrl: $nextUrl")

        val list = ArrayList<Book>()
        val elementList = doc.select("#cateList_wap > .bookbox")
        elementList.forEach { element ->
            val bookid = element.attr("bookid")
            var coverUrl = element.selectFirst(".bookimg > img").attr("orgsrc")
            if (coverUrl.startsWith("/")) {
                coverUrl = "http://m.ting456.com${coverUrl}"
            }
            val bookUrl = "http://m.ting456.com/book/d${bookid}.html"
            val title = element.selectFirst(".bookname").text()
            val author = element.selectFirst(".author > a")?.text() ?: ""
            val artist = ""
            val status = element.selectFirst(".update").text()
            val intro = element.selectFirst(".intro_line").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
            })
        }

        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun getMenus() {
        val doc = Jsoup.connect("http://m.ting456.com/category.php").testConfig(false).get()
        doc.select(".cat_list > li > a").forEach {
            val title = it.text()
            val url = it.absUrl("href")
            println("CategoryTab(\"${title}\", \"${url}\"),")
        }
    }

}