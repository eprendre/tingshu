package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.notifyLoadingEpisodes
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONArray
import java.net.URLEncoder
import kotlin.random.Random

/**
 * 博看有声
 */
object BoKanYouSheng : TingShu() {
    val instance_id = "25304"

    override fun getSourceId(): String {
        return "c98a21452583434da5cfef8be16b71d6"
    }

    override fun getUrl(): String {
        return "https://voicewk.bookan.com.cn/25303/index"
    }

    override fun getName(): String {
        return "博看有声"
    }

    override fun getDesc(): String {
        return "推荐指数:5星 ⭐⭐⭐⭐⭐\n有文化的人听这个😭"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8") //编码
        val listUrl = listOf(
            "https://es.bookan.com.cn/api/v3/voice/book?instanceId=${instance_id}&keyword=${encodedKeywords}&pageNum=1&limitNum=20",
            "https://es.bookan.com.cn/api/v3/voice/album?instanceId=${instance_id}&keyword=${encodedKeywords}&pageNum=1&limitNum=20"
        )
        val list = ArrayList<Book>()
        listUrl.forEach { url ->
            try {
                val jsonObject = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
                val totalPage = jsonObject.getInt("last_page")
                val jsonArray = jsonObject.getJSONArray("list")
                (0 until jsonArray.length()).forEach { i ->
                    val item = jsonArray.getJSONObject(i)
                    val coverUrl = item.getString("cover")
                    val bookUrl = item.getLong("id").toString()
                    val title = item.getString("name")
                    val artist = ""
                    val author = ""
                    list.add(
                        Book(
                            coverUrl,
                            bookUrl,
                            title,
                            author,
                            artist
                        ).apply {
                            this.sourceId = getSourceId()
                        }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return Pair(list, 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        return listOf(
            CategoryMenu(
                "图书", listOf(
                    CategoryTab("经典必读","book::1314"),
                    CategoryTab("国学经典","book::1320"),
                    CategoryTab("文学文艺","book::1306"),
                    CategoryTab("少年读物","book::1305"),
                    CategoryTab("儿童文学","book::1304"),
                    CategoryTab("心理哲学","book::1310"),
                    CategoryTab("育儿心经","book::1309"),
                    CategoryTab("家庭健康","book::1311"),
                    CategoryTab("青春励志","book::1307"),
                    CategoryTab("历史小说","book::1312"),
                    CategoryTab("商业财经","book::1315"),
                    CategoryTab("科技科普","book::1313"),
                    CategoryTab("故事会", "book::1303"),
                    CategoryTab("红色岁月","book::1316"),
                    CategoryTab("社会观察","book::1318"),
                    CategoryTab("音乐戏曲","book::1317"),
                    CategoryTab("相声评书","book::1319")
                )
            ),
            CategoryMenu(
                "专辑", listOf(
                    CategoryTab("健康养生","album::4"),
                    CategoryTab("休闲娱乐","album::5"),
                    CategoryTab("财经科技","album::6"),
                    CategoryTab("广播节目","album::7"),
                    CategoryTab("人文社科","album::8"),
                    CategoryTab("少儿学堂","album::9"),
                    CategoryTab("文史军事","album::10"),
                    CategoryTab("投资理财","album::11"),
                    CategoryTab("亲子教育","album::12"),
                    CategoryTab("时尚生活","album::13"),
                    CategoryTab("汽车知识","album::14"),
                    CategoryTab("发展创业","album::15"),
                    CategoryTab("婚恋情感","album::16"),
                    CategoryTab("自我提升","album::17"),
                    CategoryTab("商业资讯","album::18"),
                    CategoryTab("新闻热点","album::19")
                )
            ),
        )
    }

    override fun getCategoryList(url: String): Category {
        val type: String
        val categoryId: String
        val _url = if (url.contains("::")) {
             val array = url.split("::")
            type = array[0]
            categoryId = array[1]
            "https://api.bookan.com.cn/voice/$type/list?instance_id=$instance_id&page=1&category_id=$categoryId&num=24"
        } else {
            type = Regex("voice/(.+)/list").find(url)!!.groupValues[1]
            categoryId = Regex("category_id=(.+)&num").find(url)!!.groupValues[1]
            url
        }
        val data = Fuel.get(_url).responseJson().third.get().obj().getJSONObject("data")
        val currentPage = data.getInt("current_page")
        val totalPage = data.getInt("last_page")
        val nextUrl = "https://api.bookan.com.cn/voice/$type/list?instance_id=$instance_id&page=${currentPage + 1}&category_id=$categoryId&num=24"
        val list = ArrayList<Book>()
        val jsonArray = data.getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("id").toString()
            val title = item.getString("name")
            val artist = ""
            val author = ""
            val status = "共 ${item.getInt("total")} 章"
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.sourceId = getSourceId()
                }
            )
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    /**
     * 告知app这个源的章节列表需要分页加载
     */
    override fun isMultipleEpisodePages(): Boolean {
        return true
    }

    private val pageList = ArrayList<Int>()//保存分页加载的后续任务

    override fun reset() {
        pageList.clear()//如果用户提前退出加载会调用reset方法，需要在这里及时清空后续任务，打断加载。
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {//为true时再进行网络请求，可提升性能
            val bookId = bookUrl
            val url = "https://api.bookan.com.cn/voice/album/units?album_id=$bookId&page=1&num=20&order=1"
            val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
            val totalPage = data.getInt("last_page")
            val list = data.getJSONArray("list")
            episodes.addAll(getEpisodes(list)) //第一次打开播放页时只暂时加载第一页，避免多余的接口请求。
            if (loadFullPages) {//第二次打开播放页触发加载所有章节，此时才进行耗资源的相关操作。
                if (totalPage > 1) {
                    pageList.addAll(2..totalPage)//保存待加载的页码
                    while (pageList.size > 0) {
                        val page = pageList.removeAt(0)//每次循环按顺序拿一个页码出来
                        notifyLoadingEpisodes("$page / $totalPage")//通知界面正在加载第几页
                        val nextUrl = "https://api.bookan.com.cn/voice/album/units?album_id=$bookId&page=${page}&num=20&order=1"
                        val jsonArray = Fuel.get(nextUrl).responseJson().third.get().obj().getJSONObject("data").getJSONArray("list")
                        episodes.addAll(getEpisodes(jsonArray))
                        Thread.sleep(Random.nextLong(100, 500))//随机延迟一段时间
                    }
                }
                notifyLoadingEpisodes(null)//通知界面加载完毕
            }
        }
        return BookDetail(episodes)
    }

    private fun getEpisodes(jsonArray: JSONArray): List<Episode> {
        val episodes = ArrayList<Episode>()
        (0 until jsonArray.length()).forEach {
            val obj = jsonArray.getJSONObject(it)
            val title = obj.getString("title")
            val file = obj.getString("file")
            episodes.add(Episode(title, file))
        }
        return episodes
    }

}