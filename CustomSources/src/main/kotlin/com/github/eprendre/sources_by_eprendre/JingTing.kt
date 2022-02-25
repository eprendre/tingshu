package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList

object JingTing : TingShu() {

    override fun getSourceId(): String {
        return "ec9440c632b04292b0836004434eecc7"
    }

    override fun getUrl(): String {
        return "http://m.audio698.com"
    }

    override fun getName(): String {
        return "静听网"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n" +
                "资源数量尚可，但别访问的太频繁，会封IP的"
    }

    override fun isWebViewNotRequired(): Boolean {//当源没涉及到webview则返回true，代表在没有webview的设备上也可以正常使用这个源
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf8") //编码
        val url = "http://m.audio698.com/search?keyword=$encodedKeywords"
        val doc = Jsoup.connect(url).referrer(url).config().get()
        val list = ArrayList<Book>()
        doc.select(".clist > a").forEach {
            print(it.text())
            val coverUrl = it.selectFirst("a > dl > dt > img ").absUrl("src")
            val bookUrl = it.selectFirst("a").absUrl("href")
            val title = it.selectFirst("a > dl > dd > h3").text()
            var author = ""
            var artist = ""
            var status = ""
            it.select("a > dl > dd > p").forEach {
                if (it.text().contains("作者")) {
                    author = it.text()
                }
                if (it.text().contains("播音")) {
                    artist = it.text()
                }
                if (it.text().contains("状态")) {
                    status = it.text()
                }
            }
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlCustomExtractor.setUp { url ->
            val ref = url.replaceAfterLast("/","").removeSuffix("/")+".html"
            val date = Date().time.toString()
            val str = "1234"+date+"115599"
            val md5 = encode(str)
            val ooo = "$date|$md5"
            val doc = Jsoup.connect(url).config()
                    .referrer(ref)
                    .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Accept-Encoding","gzip,deflate")
                    .header("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                    .header("Connection","keep-alive")
                    .header("Host","m.audio698.com")
                    .header("Referer",ref)
                    .header("Upgrade-Insecure-Requests","1")
                    .cookie("ooo", ooo)
                    .get()
            return@setUp doc.getElementsByTag("source")?.first()?.attr("src") ?: ""
        }
        return AudioUrlCustomExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val doc = Jsoup.connect("http://m.audio698.com/").config().get()
        val list = ArrayList<CategoryMenu>()
        val subMenu = doc.select(".nav > a").map { catefory ->
            CategoryTab(catefory.text(), catefory.absUrl("href"))
        }
        list.add(CategoryMenu("列表", subMenu))
        return list
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val uiPage = doc.select(".cpage")

        val upPageUrl = uiPage.select("a").filter { it.text().contains("上一页") }.first().absUrl("href")
        val nextPageUrl = uiPage.select("a").filter { it.text().contains("下一页") }.first().absUrl("href")
        val lastPage = uiPage.select("a").filter { it.text().contains("末页") }.first().absUrl("href");

        val totalPage = lastPage.split("_")[1].split(".")[0]

        val up = upPageUrl.split("_")[1].split(".")[0];
        val next = nextPageUrl.split("_")[1].split(".")[0];
        var currentPage = ""
        if (up.toInt() == next.toInt()) {
            currentPage = "1"
        } else if ((next.toInt() != totalPage.toInt()) && (next.toInt() - up.toInt()) == 1) {
            currentPage = "1"
        } else {
            currentPage = (up.toInt() + 1).toString()
        }
        val list = ArrayList<Book>()
        doc.select(".clist > a").forEach {
            val coverUrl = it.selectFirst("a > dl > dt > img ").absUrl("src")
            val bookUrl = it.selectFirst("a").absUrl("href")
            val title = it.selectFirst("a > dl > dd > h3").text()
            var author = ""
            var artist = ""
            var status = ""
            it.select("a > dl > dd > p").forEach {
                if (it.text().contains("作者")) {
                    author = it.text()
                }
                if (it.text().contains("播音")) {
                    artist = it.text()
                }
                if (it.text().contains("状态")) {
                    status = it.text()
                }
            }
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage.toInt(), totalPage.toInt(), url, nextPageUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        var info = ""
        if(loadEpisodes)
        {
            val doc = Jsoup.connect(bookUrl).config().get()
            doc.select(".plist > a").forEach {
                episodes.add(Episode(it.text(), it.select("a").first().absUrl("href")))
            }
            info = doc.select(".intro > p").text()
        }
        return BookDetail(episodes, info)
    }

    fun encode(text: String): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest:ByteArray = instance.digest(text.toByteArray())
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i :Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}