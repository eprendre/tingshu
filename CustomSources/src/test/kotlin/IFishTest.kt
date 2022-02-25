import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test

class IFishTest {

    @Test
    fun bookDetail() {
        val doc = Jsoup.connect("https://ifish.fun/music/luoo/996").testConfig(false).get()
        val episodes = doc.select(".player-list > ol > li").map {
            var url = it.selectFirst(".player-list-url").text()
            url = "https://ifish.fun${url}"
            val title = it.selectFirst(".player-list-name").text() + " - " + it.selectFirst(".player-list-author").text()
            Episode(title, url)
        }
        val intro = doc.selectFirst(".text-center")?.outerHtml() ?: ""
        println(intro)
        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }

    @Test
    fun categoryList() {
        val doc = Jsoup.connect("https://ifish.fun/music/fish").testConfig(false).get()
        val pages = doc.select(".page-navigator > li > a")
        val nextPage = pages.first { it.text().contains("下一页") }
        var nextUrl = nextPage.absUrl("href")
        if (nextPage.hasClass("disabled")) {
            nextUrl = ""
        }
        val currentPage = pages.first { it.hasClass("current") }.text().toInt()
        val totalPage = pages.last().absUrl("href").split("p=")[1].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select(".daily-list > div > div")
        elementList.forEach { element ->
            val bookUrl = element.selectFirst("a").absUrl("href")
            val coverStyle = element.selectFirst("a > .item-thumb").attr("style")
            val coverUrl = Regex("url\\((.+)\\);").find(coverStyle)!!.groupValues[1]
            val children = element.selectFirst("a > .item-title").children()
            val title = children.first().text()
            var status = ""
            if (children.size > 1) {
                status = children[1].text()
            }
            var intro = ""
            element.selectFirst(".item-summary")?.let {
                intro = it.text()
            }
            list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                this.intro = intro
                this.status = status
            })
        }
        println(nextUrl)
        println("$currentPage / $totalPage")
        list.take(5).forEach { println(it) }
        assert(list.isNotEmpty())
    }
}