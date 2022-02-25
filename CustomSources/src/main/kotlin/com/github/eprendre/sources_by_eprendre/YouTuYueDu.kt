package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.getDesktopUA
import com.github.eprendre.tingshu.extensions.splitQuery
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtraHeaders
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 米兔阅读
 */
object YouTuYueDu : TingShu(), AudioUrlExtraHeaders {
    private val headers = mapOf(
        "devicetype" to "3",
        "channelname" to "official",
        "origin" to "https://www.mituyuedu.com",
        "Referer" to "https://www.mituyuedu.com",
        "seq" to "11111111111111111111111111111111",
        "User-Agent" to getDesktopUA(),
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


    override fun getSourceId(): String {
        return "41aa5aff6ee54089a09ef74a4adb3a77"
    }

    override fun getUrl(): String {
        return "https://www.mituyuedu.com"
    }

    override fun getName(): String {
        return "有兔阅读"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n加载很慢，请耐心等待。"
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val list = ArrayList<CategoryMenu>()

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
            val subMenu =  (0 until subCls.length()).map { t ->
                val subObj = subCls.getJSONObject(t)
                val subName = subObj.getString("clsName")
                val subCode = subObj.getInt("code")
                val parentCode = subObj.getInt("parentCode")
                CategoryTab(subName, "https://app1.youzibank.com/audio/list?fullFlag=2&orderBy=play_cnt&clsIdFirst=${parentCode}&clsIdSecond=${subCode}&pageNo=1&pageSize=10&page=1&size=10")
            }
            list.add(CategoryMenu(name, subMenu))
        }
        return list
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val jsonObject = manager.get(bookUrl)
                .header(headers)
                .responseJson()
                .third.get().obj()
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
                val url = "https://ys.xxhainan.com/klajdfiaoj/music_collect${musicPath}"
                episodes.add(Episode(name, url))
            }
        }
        return BookDetail(episodes)
    }

    override fun getCategoryList(url: String): Category {
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
            val status = "共 ${item.getInt("chapterCnt")} 章"
            val intro = item.getString("intro")
            val book = Book(coverUrl, bookUrl, title, author, artist )
            book.status = status
            book.intro = intro
            book.sourceId = getSourceId()
            list.add(book)
        }

        return Category(list, currentPage, pageCount, url, nextUrl)
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
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
            val book = Book(coverUrl, bookUrl, title, author, artist)
            book.status = status
            book.intro = intro
            book.sourceId = getSourceId()
            list.add(book)
        }

        return Pair(list, pageCount)
    }

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("jiuhew.com") || audioUrl.contains("dayouzh.com")) {
            hashMap["Host"] = URL(audioUrl).host
        }
        return hashMap
    }
}
