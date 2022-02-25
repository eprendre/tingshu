import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URI
import java.net.URL
import java.net.URLDecoder

class KouDaiWeiKeTangTest {

    @Test
    fun bookDetail() {
        val bookUrl = "http://www.xiai123.com/2016qiu-1s.html"
        val doc = Jsoup.connect(bookUrl).testConfig().get()
        val url = doc.select(".biaoqian1 > div > script").first {
            it.attr("src").startsWith("http")
        }.attr("src")

        val js = Fuel.get(url).responseString().third.get()
            .replace("\t", "")
            .replace("\r\n", "")
            .replace("'", "\"")
            .replace("title:", "\"title\":")
            .replace("singer:", "\"singer\":")
            .replace("cover:", "\"cover\":")
            .replace("src:", "\"src\":")
        val result = Regex("(\\[.+])").find(js)?.groupValues?.get(1)
        val list = Gson().fromJson(result, Array<TempBean>::class.java).toList()
            .map {
                val u = it.src
                val audioUrl = if (URLDecoder.decode(u, "UTF-8") != u) {//已编码
                    u
                } else {//未编码
                    val url1 = URL(u)
                    val uri = URI(url1.protocol, url1.userInfo, url1.host, url1.port, url1.path, url1.query, url1.ref)
                    uri.toASCIIString()//若音频地址含中文会导致某些设备播放失败
                }
                Episode(it.title, audioUrl)
            }

        println(list)
    }

    data class TempBean(
        val title: String,
        val singer: String,
        val cover: String,
        val src: String
    )

    @Test
    fun menu() {
        val url = "http://www.xiai123.com/index.html"
//        val url = "http://www.xiai123.com/kewen.html"
        val doc = Jsoup.connect(url).testConfig().get()
        doc.select(".index_item > ul > li > a").forEach {
            println("CategoryTab(\"${it.text()}\", \"${it.absUrl("href")}\"),")
        }
    }

    @Test
    fun categoryList() {
//        val url = "http://www.xiai123.com/yilinbanyingyu.html"
        val url = "http://www.xiai123.com/index.html"
        val doc = Jsoup.connect(url).testConfig().get()
        val list = ArrayList<Book>()
        doc.select(".index_item > ul > li > a").forEach {
            val coverUrl = it.selectFirst("img").absUrl("src")
            val title = it.text()
            val bookUrl = it.absUrl("href")
            if (bookUrl != "http://www.xiai123.com/kewen.html") {
                list.add(Book(coverUrl, bookUrl, title, "", ""))
            }
        }

        println(list)
    }
}