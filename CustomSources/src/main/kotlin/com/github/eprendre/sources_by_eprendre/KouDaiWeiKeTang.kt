package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtraHeaders
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.ceil

object KouDaiWeiKeTang : TingShu(), AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "578c6258c0ee4701b80a7135477cf685"
    }

    override fun getUrl(): String {
        return "http://www.xiai123.com"
    }

    override fun getName(): String {
        return "口袋微课堂"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n带孩子用"
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "英语", listOf(
                CategoryTab("首页推荐", "http://www.xiai123.com/index.html"),
                CategoryTab("译林版小学", "http://www.xiai123.com/yilinbanyingyu.html"),
                CategoryTab("人教PEP三起", "http://www.xiai123.com/rjpep.html"),
                CategoryTab("外研社三起", "http://www.xiai123.com/wysyy3q.html"),
                CategoryTab("外研社一起", "http://www.xiai123.com/wysyy.html"),
                CategoryTab("辽师大三起", "http://www.xiai123.com/lsdbklyy3q.html"),
                CategoryTab("EEC三起", "http://www.xiai123.com/eecyy3q.html"),
                CategoryTab("科普版三起", "http://www.xiai123.com/kpbyy.html"),
                CategoryTab("重庆版三起", "http://www.xiai123.com/cqbyy3q.html"),
                CategoryTab("广州版三起", "http://www.xiai123.com/gzbyy3q.html"),
                CategoryTab("湘少版三起", "http://www.xiai123.com/xsbyy3q.html"),
                CategoryTab("新版(精通)", "http://www.xiai123.com/xbyyjt3q.html"),
                CategoryTab("沪牛津三起", "http://www.xiai123.com/shnjyy3q.html"),
                CategoryTab("冀教版一起", "http://www.xiai123.com/jjbyy1q.html"),
                CategoryTab("冀教版三起", "http://www.xiai123.com/jjbyy3q.html"),
                CategoryTab("剑桥版三起", "http://www.xiai123.com/jqxxyy3q.html"),
                CategoryTab("新起点一起", "http://www.xiai123.com/xqdyy1q.html"),
                CategoryTab("广东版开心", "http://www.xiai123.com/gdbyy3q.html"),
                CategoryTab("陕旅版三起", "http://www.xiai123.com/sxlybyy3q.html"),
                CategoryTab("北京版一起", "http://www.xiai123.com/bjbyy1q.html"),
                CategoryTab("清华版一起", "http://www.xiai123.com/qhbyy1q.html"),
                CategoryTab("湘鲁版三起", "http://www.xiai123.com/xlbyy3q.html"),
                CategoryTab("北师大三起", "http://www.xiai123.com/bsdbyy3q.html"),
                CategoryTab("北师大一起", "http://www.xiai123.com/bsdbyy1q.html"),
                CategoryTab("川教新路径", "http://www.xiai123.com/cjbxljyy3q.html"),
                CategoryTab("鲁科版三起", "http://www.xiai123.com/lkbyy54z3q.html"),
                CategoryTab("人教版高中", "http://www.xiai123.com/gaozhongyingyu-rjbxkb.html"),
                CategoryTab("国际音标", "http://www.xiai123.com/48geguojiyingyuyinbiaofayin.html"),
                CategoryTab("译林版初中", "http://www.xiai123.com/yilinbanyingyu-chuzhong.html"),
                CategoryTab("新目标初中", "http://www.xiai123.com/rjbxmbyy-chuzhong.html"),
                CategoryTab("沪牛津初中", "http://www.xiai123.com/njyyshb-chuzhong.html"),
                CategoryTab("冀教版初中", "http://www.xiai123.com/jjbyy3q-chuzhong.html"),
                CategoryTab("外研社初中", "http://www.xiai123.com/wysyy-chuzhong.html"),
                CategoryTab("鲁教版初中", "http://www.xiai123.com/ljbyy54z-chuzhong.html"),
                CategoryTab("北师大初中", "http://www.xiai123.com/bsdbyy-chuzhong.html"),
                CategoryTab("外研社高中", "http://www.xiai123.com/gaozhongyingyu-wysxbz.html")
            )
        )
        val menu2 = CategoryMenu(
            "语文", listOf(
                CategoryTab("人教版小学", "http://www.xiai123.com/rjxiaoxueyuwen.html"),
                CategoryTab("苏教版小学", "http://www.xiai123.com/sujiao-xiaoxueyuwen.html"),
                CategoryTab("语文S版小学", "http://www.xiai123.com/ywsb-xiaoxueyuwen.html"),
                CategoryTab("西师大小学", "http://www.xiai123.com/xsdbyw-xiaoxueyuwen.html"),
                CategoryTab("北师大小学", "http://www.xiai123.com/bsdbyw-xiaoxueyuwen.html"),
                CategoryTab("湘教版小学", "http://www.xiai123.com/xjbyw-xiaoxueyuwen.html"),
                CategoryTab("人教版初中", "http://www.xiai123.com/rjchuzhongyuwen.html"),
                CategoryTab("鲁教版初中", "http://www.xiai123.com/ljbyw54z-chuzhong.html"),
                CategoryTab("上下五千年", "http://www.xiai123.com/zhonghuashangxiawuqiannian.html"),
                CategoryTab("西游记", "http://www.xiai123.com/xiyouji-sunjingxiu.html"),
                CategoryTab("三国演义", "http://www.xiai123.com/sanguoyanyi-caocan.html"),
                CategoryTab("周易", "http://www.xiai123.com/zhouyi.html"),
                CategoryTab("老子道德经", "http://www.xiai123.com/daodejing.html")
            )
        )

        val menu3 = CategoryMenu(
            "历史", listOf(
                CategoryTab("川教版历史", "http://www.xiai123.com/cjchuzhonglishi.html")
            )
        )

        return listOf(menu1, menu2, menu3)
    }

    override fun getCategoryList(url: String): Category {
        val currentPage = 1
        val pageCount = 1
        val doc = Jsoup.connect(url).config().get()
        val list = ArrayList<Book>()
        doc.select(".index_item > ul > li > a").forEach {
            val coverUrl = it.selectFirst("img").absUrl("src")
            val title = it.text()
            val bookUrl = it.absUrl("href")
            if (bookUrl != "http://www.xiai123.com/kewen.html") {
                list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                    this.sourceId = getSourceId()
                })
            }
        }
        return Category(list, currentPage, pageCount, url, "")
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config().get()
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
            Gson().fromJson(result, Array<TempBean>::class.java).toList()
                .forEach {
                    val u = it.src
                    val audioUrl = if (URLDecoder.decode(u, "UTF-8") != u) {//已编码
                        u
                    } else {//未编码
                        val url1 = URL(u)
                        val uri = URI(url1.protocol, url1.userInfo, url1.host, url1.port, url1.path, url1.query, url1.ref)
                        uri.toASCIIString()//若音频地址含中文会导致某些设备播放失败
                    }
                    episodes.add(Episode(it.title, audioUrl))
                }
        }
        return BookDetail(episodes)
    }

    data class TempBean(
        val title: String,
        val singer: String,
        val cover: String,
        val src: String
    )

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("xiai123")) {
            hashMap["Host"] = URL(audioUrl).host
            hashMap["Referer"] = "http://www.xiai123.com/"
        }
        return hashMap
    }
}