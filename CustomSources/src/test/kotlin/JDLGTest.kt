import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test

class JDLGTest {

    @Test
    fun audioUrl() {
        val url = "http://www.jdlg.net/90niandaijingdianlaoge/A1106.html"
        val doc = Jsoup.connect(url).testConfig(true).get()
        val audioUrl = doc.selectFirst(".jp_audio_0")?.attr("src")
        println(audioUrl)//结果不行，需要借助webview的渲染
    }


    @Test
    fun categoryList() {
        val type = "0"
        val url = "http://www.jdlg.net/jingdianlaoge500shou/"
        val doc = Jsoup.connect(url).testConfig(true).get()
        val divs = doc.select("#tipcontent > div > div")
        println(divs.size)

        val list = ArrayList<Book>()
        val index = type.toInt()
        divs[index].select("ul > li > a").forEach {
            list.add(Book("", it.absUrl("href"), it.text(), "", ""))
        }
        list.take(5).forEach { println(it) }
        assert(list.isNotEmpty())
    }

    @Test
    fun bookDetail() {
        val url = "http://www.jdlg.net/jingdianlaoge500shou/1.html"
//        val url = "http://www.jdlg.net/80niandaijingdianlaoge/1.html"
//        val url = "http://www.jdlg.net/jdlgphb/1.html"
        val doc = Jsoup.connect(url).testConfig(true).get()
        val episodes = doc.select("div.rankBox > ol > li, div.songList > ol > li")
            .map {
                val a = it.selectFirst(".singer_title > a, .songName")
                var artist = it.selectFirst(".songer > a")?.text()
                if (artist == null) {
                    artist = it.selectFirst("a:last-child")?.text()
                    if (artist == "加入列表") {
                        artist = null
                    }
                }
                var title = a.text()
                if (artist != null) {
                    title += " - $artist"
                }
                Episode(title, a.absUrl("href"))
            }
        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }
}