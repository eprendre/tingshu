import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class JiuZhouTest {

    @Test
    fun search() {
        val keywords = "世界"
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val page = 1
        val url = "http://www.unss.net/vodsearch/${encodedKeywords}----------${page}---.html"
        val doc = Jsoup.connect(url).testConfig(false).get()

        var currentPage = 1
        var totalPage = 1
        val pages = doc.selectFirst(".stui-page > .visible-xs")?.text() ?: ""
        if (pages.isNotEmpty()) {
            pages.split("/").let {
                currentPage = it[0].toInt()
                totalPage = it[1].toInt()
            }
        }

        println(currentPage)
        println(totalPage)

        val list = ArrayList<Book>()
        val elementList = doc.select(".stui-vodlist__media > li")
        elementList.forEach { item ->
            val thumb = item.selectFirst(".thumb > .v-thumb")
            val coverUrl = thumb.attr("data-original")
            val bookUrl = thumb.absUrl("href")
            val title = thumb.attr("title")
            val status = thumb.selectFirst(".text-right").text()
            val pList = item.select(".detail > p")
            val author = pList[0].text()
            val artist = pList[1].text()
            val desc = pList[2].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = desc
            })
        }
        println(list)
    }

    @Test
    fun bookDetail() {
        val bookUrl = "http://www.unss.net/voddetail/65586.html"
        val doc = Jsoup.connect(bookUrl).testConfig(false).get()
        val tabs = doc.select(".nav-tabs > li")
        val playlists = doc.select(".tab-content > div")

        val episodes = ArrayList<Episode>()
        tabs.forEachIndexed { index, tab ->
            val tabTitle = tab.text()
            val list = playlists[index].select("ul > li > a").map { element ->
                val title = "$tabTitle - ${element.text()}"
                val url = element.absUrl("href")
                Episode(title, url)
            }
            episodes.addAll(list)
        }

        println(episodes)
    }

    @Test
    fun categoryList() {
        val url = "http://www.unss.net/vodtype/4.html"
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).testConfig(false).get()

        var currentPage = 1
        var totalPage = 1
        var nextUrl = ""
        val pages = doc.selectFirst(".stui-page > .visible-xs")?.text() ?: ""
        if (pages.isNotEmpty()) {
            pages.split("/").let {
                currentPage = it[0].toInt()
                totalPage = it[1].toInt()
            }
            nextUrl = doc.select(".stui-page > li").first { it.text() == "下一页" }
                .selectFirst("a").absUrl("href")
        }

        println(currentPage)
        println(totalPage)
        println(nextUrl)

        val elementList = doc.select(".stui-vodlist > li > div")
        elementList.forEach { item ->
            val thumb = item.selectFirst(".stui-vodlist__thumb")
            val coverUrl = thumb.attr("data-original")
            val bookUrl = thumb.absUrl("href")
            val title = thumb.attr("title")
            val status = thumb.selectFirst(".text-right").text()
            val author = ""
            val artist = ""
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
            })
        }
        println(list)

    }
}