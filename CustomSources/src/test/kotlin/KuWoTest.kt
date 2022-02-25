import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.junit.Test
import java.net.URLEncoder
import kotlin.math.ceil

class KuWoTest {

    @Test
    fun search() {
        val keywords = "仙" //搜索关键词
        val page = 1 //当前页数
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8") //编码

        val url = "http://baby.kuwo.cn/tingshu/api/search/Search?rn=10&type=album&version=8.5.6.1&wd=${encodedKeywords}&pn=${page}&kweexVersion=1.0.2"

        val data = Fuel.get(url)
            .responseJson()
            .third.get().obj().getJSONObject("data")
        val pageCount = ceil(data.getInt("total").toFloat() / 10).toInt()
        val list = ArrayList<Book>()
        val iData = data.getJSONArray("data")
        (0 until iData.length()).forEach { i ->
            val item = iData.getJSONObject(i)
            val albumId = item.getInt("albumId")
            val coverUrl = item.getString("coverImg")
            val bookUrl = "http://baby.kuwo.cn/tingshu/api/data/album/songs?albumId=${albumId}&online=0&kweexVersion=1.0.2"
            val title = item.getString("albumName")
            val author = ""
            val artist = item.getString("artistName")
            val status = "共 ${item.getInt("songTotal")} 章"
            val intro = item.getString("title")
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = intro
//                this.sourceId =
            })
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    @Test
    fun bookDetail() {
        val bookUrl = "http://baby.kuwo.cn/tingshu/api/data/album/songs?albumId=7871200&online=0&kweexVersion=1.0.2"
        val data = Fuel.get(bookUrl).responseJson().third.get().obj().getJSONArray("data")
        val episodes = ArrayList<Episode>()
        (0 until data.length()).forEach {
            val item = data.getJSONObject(it)
            val name = item.getString("name")
            val musicrid = item.getString("musicrid")
            val url = "http://antiserver.kuwo.cn/anti.s?useless=/resource/&format=mp3&rid=MUSIC_${musicrid}&response=res&type=convert_url"
            episodes.add(Episode(name, url))
        }
        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }

    @Test
    fun categoryList() {
        val url = "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=42&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"
        val currentPage = Regex("pn=(\\d+)").find(url)!!.groupValues[1].toInt()
        var pageCount = 1
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        if (data.has("pageInfo")) {
            val total = data.getJSONObject("pageInfo").getInt("total")
            pageCount = ceil(total.toFloat() / 20).toInt()
        } else {
            val total = data.getInt("total")
            pageCount = ceil(total.toFloat() / 20).toInt()
        }
        val nextUrl = if (currentPage < pageCount) {
            url.replace(Regex("pn=(\\d+)"), "pn=${currentPage + 1}")
        } else ""
        println("currentPage: $currentPage")
        println("pageCount: $pageCount")
        println(nextUrl)

        val list = ArrayList<Book>()
        if (data.has("topDatas")) {
            val topDatas = data.getJSONArray("topDatas")
            (0 until topDatas.length()).forEach { i ->
                val item = topDatas.getJSONObject(i).getJSONObject("albums")
                val albumId = item.getInt("albumId")
                val coverUrl = item.getString("img")
                val bookUrl = "http://baby.kuwo.cn/tingshu/api/data/album/songs?albumId=${albumId}&online=0&kweexVersion=1.0.2"
                val title = item.getString("name")
                val author = ""
                val artist = ""
                val status = "共 ${item.getInt("songTotal")} 章"
                val intro = item.getString("title")
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.status = status
                    this.intro = intro
//                this.sourceId =
                })
            }

        } else {
            val iData = data.getJSONArray("data")
            (0 until iData.length()).forEach { i ->
                val item = iData.getJSONObject(i)
                val albumId = item.getInt("albumId")
                val coverUrl = item.getString("coverImg")
                val bookUrl = "http://baby.kuwo.cn/tingshu/api/data/album/songs?albumId=${albumId}&online=0&kweexVersion=1.0.2"
                val title = item.getString("albumName")
                val author = ""
                val artist = item.getString("artistName")
                val status = "共 ${item.getInt("songTotal")} 章"
                val intro = item.getString("title")
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.status = status
                    this.intro = intro
//                this.sourceId =
                })
            }
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)

    }
}