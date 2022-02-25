package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import java.net.URLEncoder
import kotlin.math.ceil

object KuWo: TingShu() {
    override fun getSourceId(): String {
        return "502efedf0613460a9967d9e86ce2b24c"
    }

    override fun getUrl(): String {
        return "http://kuwo.cn/downtingshu"
    }

    override fun getName(): String {
        return "酷我畅听"
    }

    override fun getDesc(): String {
        return "推荐指数:5星 ⭐⭐⭐⭐⭐"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
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
            val artist = "播音: ${item.getString("artistName")}"
            val status = "共 ${item.getInt("songTotal")} 章"
            val intro = item.getString("title")
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }

        return Pair(list, pageCount)
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "小说", listOf(
                CategoryTab("付费排行", "http://baby.kuwo.cn/tingshu/api/page/boutique/getBoutiqueData?pt=1&rn=100&version=8.5.6.1&pn=1&kweexVersion=1.0.2"),
                CategoryTab("免费排行", "http://baby.kuwo.cn/tingshu/api/page/boutique/getBoutiqueData?pt=2&rn=100&version=8.5.6.1&pn=1&kweexVersion=1.0.2"),
                CategoryTab("都市传说", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=42&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("玄幻奇幻", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=44&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("悬疑推理", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=45&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("现代言情", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=41&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("武侠仙侠", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=48&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("穿越架空", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=52&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("经典小说", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=64&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("青春校园", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=55&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("历史军事", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=56&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("科幻竞技", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=57&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("古代言情", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=207&rn=20&categoryId=2&pn=1&kweexVersion=1.0.2"),
                CategoryTab("能力提升", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=82&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("人文艺术", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=77&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("国学文化", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=78&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("成功法则", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=79&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("外语精通", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=76&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("养生健康", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=81&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("酷我读书", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=211&rn=20&categoryId=4&pn=1&kweexVersion=1.0.2"),
                CategoryTab("国学经典", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=117&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("历史小说", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=181&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("纪实档案", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=118&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("历史传奇", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=119&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("人物传奇", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=120&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("文化讲堂", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=121&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2"),
                CategoryTab("百家讲坛", "http://baby.kuwo.cn/tingshu/api/filter/albums?sortType=tsScore&classifyId=212&rn=20&categoryId=9&pn=1&kweexVersion=1.0.2")
            )
        )
        return listOf(menu1)
    }

    override fun getCategoryList(url: String): Category {
        val currentPage = Regex("pn=(\\d+)").find(url)!!.groupValues[1].toInt()
        val pageCount: Int
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        if (data.has("pageInfo")) {
            val total = data.getJSONObject("pageInfo").getInt("total")
            pageCount = ceil(total.toFloat() / 100).toInt()
        } else {
            val total = data.getInt("total")
            pageCount = ceil(total.toFloat() / 20).toInt()
        }
        val nextUrl = if (currentPage < pageCount) {
            url.replace(Regex("pn=(\\d+)"), "pn=${currentPage + 1}")
        } else ""

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
                    this.sourceId = getSourceId()
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
                val artist = "播音: ${item.getString("artistName")}"
                val status = "共 ${item.getInt("songTotal")} 章"
                val intro = item.getString("title")
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.status = status
                    this.intro = intro
                    this.sourceId = getSourceId()
                })
            }
        }
        return Category(list, currentPage, pageCount, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val data = Fuel.get(bookUrl).responseJson().third.get().obj().getJSONArray("data")
            (0 until data.length()).forEach {
                val item = data.getJSONObject(it)
                val name = item.getString("name")
                val musicrid = item.getString("musicrid")
                val url = "http://antiserver.kuwo.cn/anti.s?useless=/resource/&format=mp3&rid=MUSIC_${musicrid}&response=res&type=convert_url"
                episodes.add(Episode(name, url))
            }
        }
        return BookDetail(episodes)
    }
}