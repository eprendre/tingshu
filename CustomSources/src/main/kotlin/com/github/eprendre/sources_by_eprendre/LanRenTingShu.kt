package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.extractorAsyncExecute
import com.github.eprendre.tingshu.extensions.getMobileUA
import com.github.eprendre.tingshu.extensions.notifyLoadingEpisodes
import com.github.eprendre.tingshu.extensions.showToast
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONArray
import java.lang.Exception
import java.net.URLEncoder
import kotlin.math.ceil
import kotlin.random.Random

object LanRenTingShu : TingShu(), ILogin {
    private val bookIdMap = HashMap<Int, List<Long>>()
    override fun getSourceId(): String {
        return "4a3ed84e5cf841609ed4d7f790fc7fbf"
    }

    override fun getUrl(): String {
        return "https://m.lrts.me"
    }

    override fun getName(): String {
        return "懒人听书"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n正版源！免费书籍较少。带[VIP]和[精品]的是收费书籍，如需收听这类书籍，请大家在播放页右上角用自己的VIP或者购买了精品书的懒人听书账号登录后才能播放。注意：此源登录后有效期只有大约半天的时间。"
    }

    override fun isMultipleEpisodePages(): Boolean {
        return true
    }

    override fun isWebViewNotRequired(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "https://m.lrts.me/ajax/search?keyWord=${encodedKeywords}&pageSize=40&pageNum=${page}&searchOption=1"
        val result = Fuel.get(url).header(mapOf("User-Agent" to getMobileUA())).responseJson()
        val jsonObject = result.third.get().obj()

        val list = ArrayList<Book>()
        var totalPage = 1
        try {
            val bookResult = jsonObject.getJSONObject("data").getJSONObject("bookResult")
            totalPage = ceil(bookResult.getInt("count").toDouble() / 40).toInt()
            val l = bookResult.getJSONArray("list")
            (0 until l.length()).forEach { index ->
                val book = l.getJSONObject(index)
                var coverUrl = book.getString("cover")
                if (!coverUrl.contains("180")) {
                    val coverName = coverUrl.substringBeforeLast(".")
                    val coverExt = coverUrl.substringAfterLast(".")
                    coverUrl = "${coverName}_180x254.${coverExt}"
                }
                val bookUrl = "https://m.lrts.me?" + book.getLong("id").toString()
                var title = book.getString("name")
                val author = book.getString("author")
                val isFinished = book.getInt("state") == 2
                val statusPrefix = if (isFinished) "完本|" else ""
                val tracksCount = book.getInt("sections")
                val status = "${statusPrefix}共${tracksCount}章"
                val artist = book.getString("announcer")
                val intro = if (book.isNull("recReason")) {
                    book.optString("desc", "")
                } else {
                    book.getString("recReason")
                }
                val tags = book.getJSONArray("tags").toString()
                if (tags.contains("VIP")) {
                    title = "[VIP] $title"
                } else if (tags.contains("精品")) {
                    title = "[精品] $title"
                }

                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.intro = intro
                    this.status = status
                    this.sourceId = getSourceId()
                    this.isCompleted = isFinished
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(list, totalPage)
    }

    private val pageList = ArrayList<Int>()

    override fun reset() {
        pageList.clear()
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val bookId = bookUrl.split("?")[1]
        val pageSize = 200
        val list = ArrayList<Episode>()
        pageList.clear()
        if (loadEpisodes) {
            val url = "https://m.lrts.me/ajax/getBookMenu?bookId=${bookId}&pageNum=1&pageSize=${pageSize}&sortType=0"

            val result = Fuel.get(url).header("User-Agent" to getMobileUA()).responseJson()
            val obj = result.third.get().obj()
            val trackTotalCount = obj.getInt("sections")
            val pageCount = ceil(trackTotalCount.toDouble() / pageSize).toInt()

            list.addAll(getEpisodes(obj.getJSONArray("list"), bookId, 0))

            if (loadFullPages) {
                pageList.addAll(2..pageCount)
                while (pageList.size > 0) {
                    val page = pageList.removeAt(0)
                    notifyLoadingEpisodes("$page / $pageCount")
                    val nextUrl = "https://m.lrts.me/ajax/getBookMenu?bookId=${bookId}&pageNum=${page}&pageSize=${pageSize}&sortType=0"
                    val tracksArray = Fuel.get(nextUrl).header("User-Agent" to getMobileUA())
                        .responseJson().third.get().obj().getJSONArray("list")
                    list.addAll(getEpisodes(tracksArray, bookId, list.size))
                    Thread.sleep(Random.nextLong(100, 500))
                }
                notifyLoadingEpisodes(null)
            }
        }
        val url = "https://m.lrts.me/ajax/getBookInfo?id=$bookId"
        val result = Fuel.get(url).header(mapOf("User-Agent" to getMobileUA())).responseJson().third.get().obj()
        val extraInfo = result.getJSONArray("extraInfos")
        var intro = ""
        if (extraInfo.length() > 0) {
            intro = extraInfo.getJSONObject(0).getString("content")
        }
        val episodesCount = result.optInt("sections")
        return BookDetail(list, intro, episodesCount = episodesCount)
    }

    private fun getEpisodes(tracks: JSONArray, bookId: String, size: Int): List<Episode> {
        val list = ArrayList<Episode>()
        (0 until tracks.length()).forEach {
            val track = tracks.getJSONObject(it)
            val id = track.getLong("id")
            val index = size + it
            val section = track.getInt("section")
            val isFree = track.getInt("payType") == 0
            val episodeUrl = if (isFree) {
                "https://m.lrts.me/ajax/getPlayPath?entityId=${bookId}&entityType=3&opType=1&sections=[${section}]&type=0"
            } else {
                "https://m.lrts.me/player?index=${index}&entityType=3&sonId=${id}&id=${bookId}"
            }
            val episode = Episode(track.getString("name"), episodeUrl)
            episode.isFree = isFree
            list.add(episode)
        }
        return list
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
//        AudioUrlWebViewSniffExtractor.setUp(validateUrl = null)//重置条件
//        return AudioUrlWebViewSniffExtractor
        return LanRenExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
//        val menu1 = CategoryMenu(
//            "主播电台", listOf(
//                CategoryTab("全部", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=1000&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("付费精品", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=-16&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("情感", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3091&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("脱口秀", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3088&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("广播剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9252&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("亲子", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3090&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("音乐", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3089&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("人文历史", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3094&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("娱乐", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9030&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("财经科技", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3092&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("汽车", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9243&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("动漫", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=5251&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("健康生活", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3093&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("其他类别", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3095&entityType=1&pageNum=1&showFilters=1"),
//                CategoryTab("精品课", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9268&entityType=1&pageNum=1&showFilters=1")
//            )
//        )

        val menu2 = CategoryMenu(
            "有声小说", listOf(
                CategoryTab("玄幻奇幻", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=11&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("都市传说", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=8&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("穿越架空", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3109&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("武侠仙侠", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=14&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("青春校园", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3106&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("历史幻想", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=12&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("科幻空间", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3021&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("网游竞技", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9042&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("热血军事", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9041&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("官场商战", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=44&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("次元专区", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9287&entityType=1&pageNum=1&showFilters=1")
            )
        )

        val menu3 = CategoryMenu(
            "财经", listOf(
                CategoryTab("投资理财", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3059&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("股市", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3058&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("商业智慧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3057&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("管理营销", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=16&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("创业", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9048&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu33 = CategoryMenu(
            "儿童", listOf(
                CategoryTab("益智故事", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=63&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("儿童文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3027&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("国学启蒙", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9031&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("家教育儿", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=65&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("卡通动画", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9029&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("少儿名著", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9245&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("少儿科普", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=64&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("少儿历史", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9247&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("绘本早教", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9246&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("少儿英语", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=68&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("儿童歌谣", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=59&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("母婴胎教", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=66&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("通识教育", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9248&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("教辅教材", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9249&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu333 = CategoryMenu(
            "人文", listOf(
                CategoryTab("心理百科", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9045&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("科学科普", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9046&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("人物传记", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=17&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("纪实传奇", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3063&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("哲学宗教", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=1026&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("文艺文化", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9044&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("公开课", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=109&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("法律基础", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=85&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("农林渔牧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9043&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("方言学习", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=88&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu4 = CategoryMenu(
            "曲艺戏曲", listOf(
                CategoryTab("戏曲名家", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9060&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("豫剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=95&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("京剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=89&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("黄梅戏", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=93&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("曲剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=1031&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("越剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=90&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("楚剧", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=96&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("昆曲", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=91&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("地方戏", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=58&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("鼓书琴书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=67&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu5 = CategoryMenu(
            "文学", listOf(
                CategoryTab("影视文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=5241&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("名家名著", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=13&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("推理小说", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9139&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("古典文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9039&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("探险小说", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9140&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("外国文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=82&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("青春文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3113&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("官场文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9028&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("通俗文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=69&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("散文诗歌", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3022&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("军旅文学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9040&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("科幻小说", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9208&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu6 = CategoryMenu(
            "相声评书", listOf(
                CategoryTab("单田芳", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=19&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("刘兰芳", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=5247&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("袁阔成", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=21&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("连丽如", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=20&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("短打评书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=74&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("历史评书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3083&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("公案评书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=5250&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("鬼狐评书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3047&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("文化评书", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=48&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("相声小品", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=61&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu7 = CategoryMenu(
            "健康", listOf(
                CategoryTab("中医养生", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3069&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("女性养生", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3070&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("健康课堂", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3072&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("疾病预防", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3074&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("饮食药膳", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=3073&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("医学药典", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=1030&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu8 = CategoryMenu(
            "精品课", listOf(
                CategoryTab("商业财经", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9253&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("历史解密", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9263&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("人际沟通", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9255&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("名著精读", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9262&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("职场提升", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9254&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("心理学", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9258&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("文化艺术", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9264&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("健康养生", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9256&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("教育培训", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9257&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("时尚生活", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9265&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("语言学习", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9267&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("哲学思想", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9266&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("法律讲堂", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9259&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("科普知识", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9260&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("情感解惑", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9261&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu9 = CategoryMenu(
            "历史", listOf(
                CategoryTab("历史小说", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9072&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("中国古代史", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9209&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("中国近代史", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9210&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("世界史", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9068&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("红色足迹", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9070&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("野史异闻", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9071&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("普及读物", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=9073&entityType=1&pageNum=1&showFilters=1")
            )
        )
        val menu10 = CategoryMenu(
            "其他", listOf(
                CategoryTab("生活", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=1019&entityType=1&pageNum=1&showFilters=1"),
                CategoryTab("成功", "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=79&entityType=1&pageNum=1&showFilters=1")
            )
        )
        return listOf(menu2, menu3, menu33, menu333, menu4, menu5, menu6, menu7, menu8, menu9, menu10)

    }

    override fun getCategoryList(url: String): Category {
        val paramMap = url.split("?")[1].split("&").associate {
            val s = it.split("=")
            return@associate s.first() to s.last()
        }
        val entityId = paramMap["entityId"]!!.toInt()
        val pageNum = paramMap["pageNum"]?.toInt() ?: 1
        val result = Fuel.get(url).header(mapOf("User-Agent" to getMobileUA())).responseJson().third.get().obj()
        val list = ArrayList<Book>()
        var nextUrl = ""
        val currentPage = 1
        var totalPage = 1
        val albumCount = result.getInt("albumCount")
        val books = if (albumCount > 0) {
            result.getJSONArray("albums")
        } else {
            result.getJSONArray("books")
        }

        (0 until books.length()).forEach { index ->
            val book = books.getJSONObject(index)
            var coverUrl = book.getString("cover")
            if (!coverUrl.contains("180")) {
                val coverName = coverUrl.substringBeforeLast(".")
                val coverExt = coverUrl.substringAfterLast(".")
                coverUrl = "${coverName}_180x254.${coverExt}"
            }
            val bookUrl = "https://m.lrts.me?" + book.getLong("id").toString()
            var title = book.getString("name")
            val author = book.getString("author")
            val isFinished = book.getInt("state") == 2
            val status = if (isFinished) "完本" else "连载中"
            val artist = book.getString("announcer")
            val intro = if (book.isNull("recReason")) {
                book.optString("desc", "")
            } else {
                book.getString("recReason")
            }
            val tags = book.getJSONArray("tags").toString()
            if (tags.contains("VIP")) {
                title = "[VIP] $title"
            } else if (tags.contains("精品")) {
                title = "[精品] $title"
            }

            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
                this.isCompleted = isFinished
            })
        }

        if (pageNum == 1) {
            val bookIdList = ArrayList<Long>()
            val bookIds = if (albumCount > 0) {
                result.getJSONArray("albumIds")
            } else {
                result.getJSONArray("bookIds")
            }
            (0 until bookIds.length()).forEach {
                bookIdList.add(bookIds.getLong(it))
                bookIdMap[entityId] = bookIdList
            }
            if (bookIdList.size > 20) {
                totalPage = 2
                val fromIndex = 20
                var toIndex = fromIndex + 19
                if (toIndex > bookIdList.size) {
                    toIndex = bookIdList.size
                }
                val bookIdString = bookIdList.subList(fromIndex, toIndex).joinToString(",", "[", "]")
                nextUrl = "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=${entityId}&entityType=0&pageNum=0&showFilters=0&bookIds=${bookIdString}"
            }
        } else {
            val lastId = paramMap["bookIds"]!!.replace("[", "").replace("]", "").split(",").last().toLong()
            val bookIdList = bookIdMap[entityId]!!
            val fromIndex = bookIdList.indexOf(lastId) + 1
            if (fromIndex < bookIdList.size) {
                totalPage = 2//用来判断是否有下一页，不一定需要知道实际页数，只要current不等于total就行
                var toIndex = fromIndex + 19
                if (toIndex > bookIdList.size) {
                    toIndex = bookIdList.size
                }
                val bookIdString = bookIdList.subList(fromIndex, toIndex).joinToString(",", "[", "]")
                nextUrl = "https://m.lrts.me/ajax/getResourceList?dsize=20&entityId=${entityId}&entityType=0&pageNum=0&showFilters=0&bookIds=${bookIdString}"
            }
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getLoginUrl(): String {
        showToast("登录成功后即可关闭页面")
        return "https://m.lrts.me/user"
    }

    override fun isLoginDesktop(): Boolean {
        return false
    }
}

object LanRenExtractor: AudioUrlExtractor {
    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        if (url.contains("getPlayPath")) {
            AudioUrlJsonExtractor.setUp {
                val jsonarray = it.obj().getJSONArray("list")
                return@setUp jsonarray.getJSONObject(0).getString("path")
            }
            AudioUrlJsonExtractor.extract(url, autoPlay, isCache, isDebug)
        } else {
            AudioUrlWebViewSniffExtractor.setUp(validateUrl = null)//重置条件
            AudioUrlWebViewSniffExtractor.extract(url, autoPlay, isCache, isDebug)
        }
    }
}