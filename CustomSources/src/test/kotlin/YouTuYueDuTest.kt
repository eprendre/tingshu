import com.github.eprendre.tingshu.extensions.splitQuery
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONObject
import org.junit.Test
import java.net.URL
import java.net.URLEncoder
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 有兔阅读测试
 */
class YouTuYueDuTest {
    private val headers = mapOf(
    "devicetype" to "3",
    "channelname" to "official",
    "origin" to "https://www.mituyuedu.com",
    "Referer" to "https://www.mituyuedu.com",
    "seq" to "11111111111111111111111111111111",
    "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
    "version" to "1.9.0"
    )
    private val manager : FuelManager = FuelManager().apply {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })

        socketFactory = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }.socketFactory

        hostnameVerifier = HostnameVerifier { _, _ -> true }
    }

    /**
     * 发现分类，如果没有则不用提供
     */
    @Test
    fun categories() {
        val url = "https://app1.youzibank.com/audio/book/cls/list"
        val jsonObject = manager.get(url)
            .header(headers)
            .responseJson()
            .third.get().obj()

        val data = jsonObject.getJSONArray("data")
        (0 until data.length()).forEach { i ->
            val obj = data.getJSONObject(i)
            val name = obj.getString("clsName")
            val code = obj.getInt("code")
            val subCls = obj.getJSONArray("subCls")
            println(name)
            (0 until subCls.length()).forEach { t ->
                val subObj = subCls.getJSONObject(t)
                val subName = subObj.getString("clsName")
                val subCode = subObj.getInt("code")
                val parentCode = subObj.getInt("parentCode")
                println(subName)
            }
            println("--------------")
        }
    }

    /**
     * 搜索
     * 需要返回：1.结果list， 2.总页数
     */
    @Test
    fun search() {
        val keywords = "仙" //搜索关键词
        val page = 1 //当前页数
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8") //编码
        val url = "https://app1.youzibank.com/es/search/audio?q=${encodedKeywords}&pageSize=10&pageNo=${page}&page=${page}&size=10"

        val jsonObject = manager.get(url)
            .header(headers)
            .responseJson()
            .third.get().obj()

        val pageCount = jsonObject.getInt("pageCount")
        val list = ArrayList<Book>()

        val data = jsonObject.getJSONArray("data")
        (0 until data.length()).forEach { i ->
            val item = data.getJSONObject(i)
            val coverUrl = "https://img.dayouzh.com/klajdfiaoj/music_collect${item.getString("photoPath")}"
            val bookUrl = "https://app1.youzibank.com/audio/chapter/listAll?audioId=${item.getInt("id")}"
            val title = item.getString("name")
            val author = ""
            val artist = item.getString("actorName")
            val status = "共 ${item.getInt("chapterCnt")} 章"
            val intro = item.getString("intro")
            list.add(
                Book(coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
                }
            )
        }

        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    /**
     * 书籍详情页，包含更多的书籍信息，以及章节列表。
     * 章节列表需要返回，其它书籍信息看情况返回
     */
    @Test
    fun bookDetail() {
        val bookUrl = "https://app1.youzibank.com/audio/chapter/listAll?audioId=141"
        val jsonObject = manager.get(bookUrl)
            .header(headers)
            .responseJson()
            .third.get().obj()

        val episodes = ArrayList<Episode>()
        val data = jsonObject.getJSONArray("data")
        (0 until data.length()).forEach {
            val item = data.getJSONObject(it)
            val name = item.getString("name")
            val musicPathObject = JSONObject(item.getString("musicPath"))
            val musicPath = when {
                musicPathObject.has("h") -> {
                    musicPathObject.getJSONObject("h").getString("addr")
                }
                musicPathObject.has("m") -> {
                    musicPathObject.getJSONObject("m").getString("addr")
                }
                else -> {
                    musicPathObject.getJSONObject("l").getString("addr")
                }
            }
            val url = "https://ys.jiuhew.com/klajdfiaoj/music_collect${musicPath}"
            episodes.add(Episode(name, url))
        }
        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }

    /**
     * 分类列表获取
     * 需要返回：1. 下一页地址 2. 当前页数 3. 总页数 4. 书籍list
     */
    @Test
    fun categoryList() {
        val parentId = 38
        val subId = 39
        val url = "https://app1.youzibank.com/audio/list?fullFlag=2&orderBy=play_cnt&clsIdFirst=${parentId}&clsIdSecond=${subId}&pageNo=1&pageSize=10&page=1&size=10"
        val jsonObject = manager.get(url)
            .header(headers)
            .responseJson()
            .third.get().obj()

        val currentPage = jsonObject.getInt("pageNo")
        val pageCount = jsonObject.getInt("pageCount")

        val list = ArrayList<Book>()
        val nextUrl = if (currentPage < pageCount) {
            val queryMap = splitQuery(URL(url))
            queryMap["pageNo"] = (currentPage + 1).toString()
            queryMap["page"] = (currentPage + 1).toString()
            "https://app1.youzibank.com/audio/list?" + queryMap.map { "${it.key}=${it.value}" }.joinToString("&")
        } else {
            ""
        }

        val data = jsonObject.getJSONArray("data")
        (0 until data.length()).forEach { i ->
            val item = data.getJSONObject(i)
            val coverUrl = "https://img.dayouzh.com/klajdfiaoj/music_collect${item.getString("photoPath")}"
            val bookUrl = "https://app1.youzibank.com/audio/chapter/listAll?audioId=${item.getInt("id")}"
            val title = item.getString("name")
            val author = ""
            val artist = item.getString("actorName")
            val status = "共 ${item.getInt("chapterCnt")}章"
            val intro = item.getString("intro")
            list.add(
                Book(coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
                }
            )
        }
        println(nextUrl)
        println("$currentPage / $pageCount")
        list.take(5).forEach { println(it) }
        assert(list.isNotEmpty())
    }
}