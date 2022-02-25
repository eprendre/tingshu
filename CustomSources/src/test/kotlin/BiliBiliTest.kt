import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URLEncoder

class BiliBiliTest {

    @Test
    fun bookDetail() {
//        val url = "https://m.bilibili.com/video/BV1W54y1C7jc"
//        val url = "https://m.bilibili.com/video/BV1Jf4y1C7m8"
        val url = "https://m.bilibili.com/video/BV1Ft411W7Wp"
        val doc = Jsoup.connect(url).testConfig(false).get()
        val elements = doc.select(".m-video-part-new > ul > li")
        val episodes = ArrayList<Episode>()
        if (elements.isEmpty()) {
            episodes.add(Episode("1p", url))
        } else {
            elements.forEachIndexed { index, element ->
                val title = element.text()
                val page = index + 1
                episodes.add(Episode(title, "$url?p=$page"))
            }
        }

        episodes.take(5).forEach {
            println(it)
        }
        assert(episodes.size > 0)
    }

    @Test
    fun search() {
        val keywords = "我的听书"
        val page = 1
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")

        val url = "https://api.bilibili.com/x/web-interface/search/all/v2?keyword=${encodedKeywords}&page=${page}&pagesize=20"
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        val numPages = data.getInt("numPages")
        val list = ArrayList<Book>()
        val results = data.getJSONArray("result")
        run loop@{
            (0 until results.length()).forEach {
                val result = results.getJSONObject(it)
                if (result.getString("result_type") == "video") {
                    val videos = result.getJSONArray("data")
                    (0 until videos.length()).forEach { index ->
                        val videoObj = videos.getJSONObject(index)
                        val coverUrl = "https:" + videoObj.getString("pic")
                        val title = videoObj.getString("title")
                            .replace("<em class=\"keyword\">", "")
                            .replace("</em>", "")
                        val author = ""
                        val artist = videoObj.getString("author")
                        val bookUrl = "https://m.bilibili.com/video/" + videoObj.getString("bvid")
                        val status = "播放次数: " + videoObj.getInt("play")
                        val intro = videoObj.getString("description")

                        list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                            this.status = status
                            this.intro = intro
//                            this.sourceId =
                        })
                    }
                    return@loop
                }
            }
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }
}