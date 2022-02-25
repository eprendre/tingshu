import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class NiuNiuTest {
    @Test
    fun search() {
        val keywords = "修仙"
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val page = 1
        val url = "http://www.ziliao6.com/tv/?name=${encodedKeywords}"
        val doc = Jsoup.connect(url).testConfig(false).get()

        val list = ArrayList<Book>()

        doc.selectFirst(".alert.alert-info").children()
            .forEach { element ->
                val coverUrl = ""
                if (element.`is`("details")) {
                    val title = element.selectFirst("summary").ownText()
                    val bookUrl = element.select("ul > a").apply { removeFirst() }
                        .joinToString(separator = ",") { a ->
                            return@joinToString a.text() + "&&" + a.absUrl("href")
                        }
                    list.add(Book(coverUrl, bookUrl, title, "", "").apply {

                    })
                } else if(element.`is`("a")) {
                    val title = element.text()
                    val bookUrl = title + "&&" + element.absUrl("href")
                    list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                    })
                }
            }
        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun bookDetail() {
        val episodes = ArrayList<Episode>()
        val bookUrl = "01集&&https://vod6.wenshibaowenbei.com/20211114/frtPK312/index.m3u8,02集&&https://vod6.wenshibaowenbei.com/20211114/Ijpk6PKj/index.m3u8,03集&&https://vod6.wenshibaowenbei.com/20211114/ufXVqIjy/index.m3u8,04集&&https://vod6.wenshibaowenbei.com/20211121/BWJ74ex0/index.m3u8,05集&&https://vod11.bdzybf.com/20211128/KDOldL5K/index.m3u8,06集&&https://vod11.bdzybf.com/20211205/ATlaRkQz/index.m3u8,07集&&https://vod11.bdzybf.com/20211205/BlxG8krc/index.m3u8,08集&&https://vod8.wenshibaowenbei.com/20211212/6cELh9ju/index.m3u8,09集&&https://vod8.wenshibaowenbei.com/20211212/s2sQm62o/index.m3u8,10集&&https://vod8.wenshibaowenbei.com/20211212/vNP9XO8m/index.m3u8,11集&&https://vod8.wenshibaowenbei.com/20211212/tRFWdqmb/index.m3u8,12集&&https://vod8.wenshibaowenbei.com/20211219/QgmkOIK6/index.m3u8,13集&&https://vod8.wenshibaowenbei.com/20211225/FPzIEg0C/index.m3u8"
//        val bookUrl = "修仙传之炼剑&&https://vod3.bdzybf3.com/20210227/FCt0usUs/index.m3u8"
        bookUrl.split(",").forEach {
            val params = it.split("&&")
            val e = Episode(params[0], params[1])
            episodes.add(e)
        }
        episodes.take(20).forEach { println(it) }
        assertThat(episodes.size).isGreaterThan(0)
    }
}