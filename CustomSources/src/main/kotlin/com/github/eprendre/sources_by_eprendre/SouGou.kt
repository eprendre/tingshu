package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.notifyLoadingEpisodes
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.ceil
import kotlin.random.Random

object SouGou : TingShu() {
    override fun getSourceId(): String {
        return "5beb88fbabb74ab4b8ac264bce2f14cb"
    }

    override fun getUrl(): String {
        return "http://as.sogou.com/ting/pages/index4wx?categoryID=3"
    }

    override fun getName(): String {
        return "搜狗阅读"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n" +
                "确定了，的确不支持搜索"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        TODO("Not yet implemented")
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
//        val url1 = "http://as.sogou.com/ting/pages/index4wx?categoryID=3&gf=e-d-pother-i&uID=CBFDA57B6F3E990A000000005FC89C09&sgid=0&src=0"
//        val url2 = "http://as.sogou.com/ting/pages/index4wx?categoryID=12&gf=e-d-pother-i&uID=CBFDA57B6F3E990A000000005FC89C09&sgid=0&src=0"
//        val menu1 = parseMenus(url1)
//        val menu2 = parseMenus(url2)
//        return listOf(CategoryMenu("有声小说",menu1),
//        CategoryMenu("相声评书",menu2))
        return listOf(
            CategoryMenu(
                "有声小说", listOf(
                    CategoryTab(
                        "悬疑",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=悬疑&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "言情",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=言情&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "幻想",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=幻想&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "历史",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=历史&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "都市",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=都市&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "文学",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=文学&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "武侠",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=武侠&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "官场商战",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=官场商战&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "经管",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=经管&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "社科",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=社科&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "读客图书",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=读客图书&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "博集天卷",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=博集天卷&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "磨铁阅读",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=磨铁阅读&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "速播专区",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=速播专区&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "蓝狮子",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=3&tagName=蓝狮子&gf=e-d-pother-i&uID=8ADA652A9F13A00A000000005FCFA391&sgid=0&src=0"
                    )
                )
            ), CategoryMenu(
                "相声评书", listOf(
                    CategoryTab(
                        "郭德纲相声",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=郭德纲相声&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "天津相声",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=天津相声&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "赵本山",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=赵本山&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "小品大全",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=小品大全&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "刘兰芳",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=刘兰芳&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "评书大全",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=评书大全&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "相声大全",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=相声大全&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "鼓曲大全",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=鼓曲大全&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    ),
                    CategoryTab(
                        "非遗专区",
                        "http://as.sogou.com/ting/pages/list4wx?categoryID=12&tagName=非遗专区&gf=e-d-pother-i&uID=8ADA652AED18A00A000000005FCFA391&sgid=0&src=0"
                    )
                )
            )
        )
    }

    override fun getCategoryList(url: String): Category {
        val tag = Regex("tagName=(.+?)&").find(url)?.groupValues?.get(1) ?: ""
        val categoryID = Regex("categoryID=(.+?)&").find(url)?.groupValues?.get(1) ?: ""
        val page = Regex("page=(.+?)&").find(url)?.groupValues?.get(1)?.toInt() ?: 1

        val xhrUrl = "http://as.sogou.com/ting/pages/querylist"
        var nextPage = page + 1

        val list = if (!url.contains("http")) {
            val jsonObj = Fuel.post(xhrUrl, listOf("categoryID" to categoryID, "tagName" to tag, "pageNo" to page.toString(), "pageSize" to "10"))
                .responseJson().third.get().obj()
            if (jsonObj.getJSONArray("albums").length() == 0) nextPage = page
            parseJson(jsonObj, tag)
        } else {
            val doc = Jsoup.connect(url).config().get()
            nextPage = 2
            parseDoc(doc, tag)
        }
        val nextUrl = "categoryID=$categoryID&tagName=$tag&page=$nextPage&"
        return Category(list, page, nextPage, url, nextUrl)
    }

    private val pageList = ArrayList<Int>()//保存分页加载的后续任务

    override fun reset() {
        pageList.clear()//如果用户提前退出加载会调用reset方法，需要在这里及时清空后续任务，打断加载。
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val list = ArrayList<Episode>()
        if (loadEpisodes) {
            val tag = Regex("tagName=(.+?)&").find(bookUrl)?.groupValues?.get(1) ?: ""
            val key = Regex("key=(.+?)&").find(bookUrl)?.groupValues?.get(1) ?: ""
            val tagName = URLDecoder.decode(tag, "utf-8")
            val doc = Jsoup.connect(bookUrl).config().get()
            val chapterText = doc.selectFirst(".read-anthology-toolbar").text()
            val charterNum = Regex("共(.+?)集").find(chapterText)?.groupValues?.get(1)?.toInt() ?: 50
            val totalPage = ceil(charterNum.toDouble() / 50).toInt()
            list.addAll(parseInfo(key, tagName, "1"))
            if (loadFullPages) {//第二次打开播放页触发加载所有章节，此时才进行耗资源的相关操作。
                if (totalPage > 1) {
                    pageList.addAll(2..totalPage)//保存待加载的页码
                    while (pageList.size > 0) {
                        val page = pageList.removeAt(0)//每次循环按顺序拿一个页码出来
                        notifyLoadingEpisodes("$page / $totalPage")//通知界面正在加载第几页
                        list.addAll(parseInfo(key, tagName, page.toString()))
                        Thread.sleep(Random.nextLong(100, 500))//随机延迟一段时间
                    }
                }
                notifyLoadingEpisodes(null)//通知界面加载完毕
            }
        }
        return BookDetail(list)
    }

    fun parseJson(jsonObj: JSONObject, tag: String): List<Book> {
        val list = ArrayList<Book>()
        val jsonArr = jsonObj.getJSONArray("albums")
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)
            val image = obj["coverUrlLarge"].toString()
            val title = obj["albumTitle"].toString()
            val intro = obj["albumIntro"].toString()
            val key = obj["id"].toString()
            val bookUrl = "http://as.sogou.com/ting/pages/getAlbum?s=1&key=$key&cid=3&hot=-1&tagName=${
                URLEncoder.encode(
                    tag,
                    "utf-8"
                )
            }" +
                    "&gf=e-d-pother-i&amp;uID=CBFDA57B6F3E990A000000005FC89C09&amp;sgid=0&amp;src=0"
            list.add(Book(image, bookUrl, title, "", "").apply {
                this.intro = if (!intro.isNotEmpty()) "暂无简介" else intro
                this.sourceId = getSourceId()
            })
        }
        return list
    }

    fun parseDoc(doc: Document, tag: String): List<Book> {
        val list = ArrayList<Book>()
        doc.select(".vertical-list1 > li").forEach {
            val image = it.selectFirst("img").absUrl("src")
            val title = it.selectFirst(".book-title").text()
            val intro = it.selectFirst(".book-intro").text()

            val key = it.selectFirst("div").attr("key")
            val bookUrl = "http://as.sogou.com/ting/pages/getAlbum?s=1&key=$key&cid=3&hot=-1&tagName=${
                URLEncoder.encode(
                    tag,
                    "utf-8"
                )
            }" +
                    "&gf=e-d-pother-i&amp;uID=CBFDA57B6F3E990A000000005FC89C09&amp;sgid=0&amp;src=0"
            list.add(Book(image, bookUrl, title, "", "").apply {
                this.intro = if (!intro.isNotEmpty()) "暂无简介" else intro
                this.sourceId = getSourceId()
            })
        }
        return list
    }

    fun parseInfo(key: String, tagName: String, page: String): List<Episode> {
        val list = ArrayList<Episode>()
        val xhrUrl = "http://as.sogou.com/ting/pages/querytracks"
        val jsonObj = Fuel.post(xhrUrl, listOf("key" to key, "tagName" to tagName, "pageNo" to page, "pageSize" to "50"))
            .responseJson().third.get().obj()
        val jsonArr = jsonObj.getJSONArray("tracks")
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)
            val url = obj["downloadUrl"].toString()
            val title = obj["trackTitle"].toString()
            list.add(Episode(title, url))
        }
        return list
    }
}