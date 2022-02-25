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
 * 云图有声
 */
object YunTuYouSheng : TingShu() {
    val wechatID = "07955551-706c-4259-9aa0-db4627dfca57"

    override fun getSourceId(): String {
        return "ab0a5474cd6a40e3ba65045addad390a"
    }

    override fun getUrl(): String {
        return "http://yuntuwechat.yuntuys.com/home"
    }

    override fun getName(): String {
        return "云图有声"
    }

    override fun getDesc(): String {
        return "推荐指数:5星 ⭐⭐⭐⭐⭐\n有文化的人听这个😭"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8") //编码
        val url =
            "http://open-service.yuntuys.com/api/w_ys/book/search/wechat:$wechatID/$encodedKeywords?pageSize=20&pageNum=$page"

        val list = ArrayList<Book>()
        val jsonObject = Fuel.get(url).responseJson().third.get().obj()
        val totalPage = jsonObject.getJSONObject("data").getInt("totalPage")
        val jsonArray = jsonObject.getJSONObject("data").getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("bookId").toString()
            val title = item.getString("bookName")
            val artist = item.getString("anchorName")
            val author = item.getString("authorName")
            val status = "共 ${item.getInt("chapters")} 章"
            val intro = item.getString("summary")
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
                    this.sourceId = getSourceId()
                }
            )
        }

        return Pair(list, totalPage)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        return listOf(
            CategoryMenu(
                "特色", listOf(
                    CategoryTab("四史专栏", "585"),
                    CategoryTab("远读重洋", "571"),
                    CategoryTab("儿童防疫", "122"),
                    CategoryTab("听见真知", "124"),
                    CategoryTab("豆瓣高分", "125"),
                    CategoryTab("广播剧", "126"),
                    CategoryTab("影视同期", "127"),
                    CategoryTab("云图学院", "421")
                )
            ),
            CategoryMenu(
                "经典文学", listOf(
                    CategoryTab("世界名著", "131"),
                    CategoryTab("中国文学", "132"),
                    CategoryTab("国学经典", "134"),
                    CategoryTab("外国文学", "133"),
                    CategoryTab("诗词散文", "135"),
                    CategoryTab("人物传记", "136")
                )
            ),

            CategoryMenu(
                "畅销小说", listOf(
                    CategoryTab("国风古韵", "137"),
                    CategoryTab("青春校园", "138"),
                    CategoryTab("科学幻想", "139"),
                    CategoryTab("官场商战", "140"),
                    CategoryTab("军事谍战", "141"),
                    CategoryTab("悬疑推理", "142"),
                    CategoryTab("现代都市", "143"),
                    CategoryTab("怪奇物语", "144"),
                    CategoryTab("侠义江湖", "335")
                )
            ),
            CategoryMenu(
                "职场财经", listOf(
                    CategoryTab("创业学院", "148"),
                    CategoryTab("职场指南", "149"),
                    CategoryTab("商界大咖", "150"),
                    CategoryTab("金融理财", "151")
                )
            ),
            CategoryMenu(
                "少儿教育", listOf(
                    CategoryTab("儿童文学", "152"),
                    CategoryTab("童话名著", "153"),
                    CategoryTab("国学启蒙", "154"),
                    CategoryTab("儿歌故事", "155"),
                    CategoryTab("百科知识", "156"),
                    CategoryTab("亲子教育", "157")
                )
            ),
            CategoryMenu(
                "文化艺术", listOf(
                    CategoryTab("民俗文化", "161"),
                    CategoryTab("世界之窗", "163"),
                    CategoryTab("哲学思想", "164")
                )
            ),
            CategoryMenu(
                "历史风云", listOf(
                    CategoryTab("古代历史", "165"),
                    CategoryTab("近现代史", "166"),
                    CategoryTab("世界历史", "167"),
                    CategoryTab("传奇史话", "168")
                )
            ),
            CategoryMenu(
                "军事文学", listOf(
                    CategoryTab("军事纪实", "173"),
                    CategoryTab("战争烽火", "174")
                )
            ),
            CategoryMenu(
                "军政人物", listOf(
                    CategoryTab("革命先驱", "176"),
                    CategoryTab("政治领袖", "177")
                )
            ),
            CategoryMenu(
                "健康养生", listOf(
                    CategoryTab("养生保健", "181"),
                    CategoryTab("养颜减肥", "182"),
                    CategoryTab("食疗课堂", "183"),
                    CategoryTab("孕产育儿", "184")
                )
            ),
            CategoryMenu(
                "情感生活", listOf(
                    CategoryTab("心理健康", "185"),
                    CategoryTab("婚恋家庭", "186"),
                    CategoryTab("心灵励志", "187"),
                    CategoryTab("生活百科", "188"),
                    CategoryTab("娱乐休闲", "189")
                )
            )
        )
    }

    override fun getCategoryList(url: String): Category {
        val categoryUrl = if (url.contains("http")) {
            url
        } else {
            "http://open-service.yuntuys.com/api/w_ys/book/getBookListByType/wechat:$wechatID/$url?pageNum=1&pageSize=20"
        }

        val list = ArrayList<Book>()
        val data = Fuel.get(categoryUrl).responseJson().third.get().obj().getJSONObject("data")
        val currentPage = data.getInt("pageNumber")
        val totalPage = data.getInt("totalPage")
        val nextUrl = if (currentPage == totalPage)  {
            ""
        } else {
            val typeId = if (url.contains("http")) {
                url.substringAfterLast("/").split("?pageNum=")[0]
            } else {
                url
            }
            "http://open-service.yuntuys.com/api/w_ys/book/getBookListByType/wechat:$wechatID/$typeId?pageNum=${currentPage + 1}&pageSize=20"
        }
        val jsonArray = data.getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("bookId").toString()
            val title = item.getString("bookName")
            val artist = item.getString("anchorName")
            val author = item.getString("authorName")
            val status = "共 ${item.getInt("chapters")} 章"
            val intro = item.getString("summary")
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
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
            val url =
                "http://open-service.yuntuys.com/api/w_ys/book/getChapters/wechat:$wechatID/$bookId/true/asc?pageSize=200&pageNum=1"
            val pageQuery = Fuel.get(url).responseJson().third.get().obj()
                .getJSONObject("data")
                .getJSONObject("pageQuery")
            val totalPage = pageQuery.getInt("totalPage")
            val list = pageQuery.getJSONArray("list")
            episodes.addAll(getEpisodes(list))//第一次打开播放页时只暂时加载第一页，避免多余的接口请求。
            if (loadFullPages) {//第二次打开播放页触发加载所有章节，此时才进行耗资源的相关操作。
                if (totalPage > 1) {
                    pageList.addAll(2..totalPage)//保存待加载的页码
                    while (pageList.size > 0) {
                        val page = pageList.removeAt(0)//每次循环按顺序拿一个页码出来
                        notifyLoadingEpisodes("$page / $totalPage")//通知界面正在加载第几页
                        val nextUrl = "http://open-service.yuntuys.com/api/w_ys/book/getChapters/wechat:$wechatID/$bookId/true/asc?pageSize=200&pageNum=$page"
                        val jsonArray = Fuel.get(nextUrl).responseJson().third.get().obj()
                            .getJSONObject("data")
                            .getJSONObject("pageQuery")
                            .getJSONArray("list")
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
            val item = jsonArray.getJSONObject(it)
            val name = item.getString("name")
            val audioUrl = item.getString("audioUrl")
            episodes.add(Episode(name, audioUrl))
        }
        return episodes
    }

}