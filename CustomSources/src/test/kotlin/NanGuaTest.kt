import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test

class NanGuaTest {

    @Test
    fun search() {
        val keywords = "仙"
        val url = "http://www.nangua55.com/search/"

        val doc = Jsoup.connect(url).testConfig(true)
            .headers(mapOf("Referer" to "http://www.nangua55.com/search/"))
            .data("wd", keywords)
            .post()
//        val pages = doc.selectFirst("#long-page > ul > .visible-xs").text().split("/")
//        val currentPage = pages[0].toInt()
//        val totalPage = pages[1].toInt()
//        val nextUrl = doc.select("#long-page > ul > li").first { it.text() == "下一页" }
//            .selectFirst("a").absUrl("href")
        val currentPage = 1
        val totalPage = 1

        val list = ArrayList<Book>()
        val elementList = doc.select("#content > div")
        elementList.forEach { item ->
            val a = item.selectFirst(".video-pic")
            val coverUrl = a.attr("data-original")
            val bookUrl = a.absUrl("href")
            val title = a.attr("title")
            val author = ""
            val artist = ""
            val status = item.select(".info > li")[2].ownText()
            list.add(Book(coverUrl, bookUrl, title, author, artist))
        }
        println(list)
    }

    @Test
    fun bookDetail() {
        val bookUrl = "http://www.nangua55.com/video/36533.html"
        val doc = Jsoup.connect(bookUrl).testConfig(true).get()
        val tabs = doc.select("#playTab > li")
        val playlists = doc.select(".playlist > ul")

        val episodes = ArrayList<Episode>()
        tabs.forEachIndexed { index, tab ->
            val tabTitle = tab.text()
            val list = playlists[index].select("li > a").map { element ->
                val title = "$tabTitle - ${element.text()}"
                val url = element.absUrl("href")
                Episode(title, url)
            }.asReversed()
            episodes.addAll(list)
        }

        println(episodes)
    }


    @Test
    fun categoryList() {
        val url = "http://www.nangua55.com/index.php?s=home-vod-type-id-2-picm-1-p-1"
        val list = ArrayList<Book>()
        val doc = Jsoup.connect(url).testConfig(true)
            .headers(mapOf("Referer" to "http://www.nangua55.com/")).get()
        val pages = doc.selectFirst("#long-page > ul > .visible-xs").text().split("/")
        val currentPage = pages[0].toInt()
        val totalPage = pages[1].toInt()
        val nextUrl = doc.select("#long-page > ul > li").first { it.text() == "下一页" }
            .selectFirst("a").absUrl("href")

        println(currentPage)
        println(totalPage)
        println(nextUrl)

        val elementList = doc.select("#content > li")
        elementList.forEach { item ->
            val a = item.selectFirst(".video-pic")
            val coverUrl = a.attr("data-original")
            val bookUrl = a.absUrl("href")
            val title = item.selectFirst(".title").text()
            val author = item.selectFirst(".subtitle").text()
            val artist = a.selectFirst(".score").text()
            val status = a.selectFirst(".note").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist))
        }
        println(list)
    }
}