package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlCustomExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList

object XinMo : TingShu() {
    override fun getSourceId(): String {
        return "1c835d8272a74716b82758dd2422594c"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n" +
                "资源数量尚可，但别访问的太频繁，会封IP的"
    }

    override fun getUrl(): String {
        return "http://m.ixinmoo.com/"
    }

    override fun getName(): String {
        return "心魔听书"
    }

    override fun isWebViewNotRequired(): Boolean {//当源没涉及到webview则返回true，代表在没有webview的设备上也可以正常使用这个源
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "http://m.ixinmoo.com/search.html"
        val length = "searchword:${URLEncoder.encode(keywords, "utf8")}".length //编码
        val conn = Jsoup.connect(url).config()
        conn.referrer(url)
        conn.data("searchword", keywords)
        conn.cookie("ooo", getCookie())
        conn.header("Content-Length", length.toString())
        conn.header("Content-Type", "application/x-www-form-urlencoded")
        conn.header(
            "Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        )
        conn.header("Accept-Encoding", "gzip, deflate")
        conn.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        conn.header("Cache-Control", "max-age=0")
        conn.header("Connection", "keep-alive")
        conn.header("Host", "m.ixinmoo.com")
        conn.header("Origin", "http://m.ixinmoo.com")
        conn.header("Upgrade-Insecure-Requests", "1")
        val doc = conn.post()
        val list = ArrayList<Book>()
        doc.select(".xxzx > .list-ov-tw").forEach {
            val coverUrl = it.selectFirst(".list-ov-t > a > img").absUrl("src")
            val bookUrl = it.selectFirst(".list-ov-w > a").absUrl("href")
            val title = it.select(".list-ov-w > a > .bt").text()
            var author = it.select(".list-ov-w > a > .zz")[0].text()
            var artist = it.select(".list-ov-w > a > .zz")[1].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = getStatus(bookUrl)
                this.sourceId = getSourceId()
            })
        }
        return Pair(list, 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlCustomExtractor.setUp { url ->
            val ref = url.replaceAfterLast("/", "").removeSuffix("/") + ".html"
            val date = Date().time.toString()
            val str = "1234" + date + "115599"
            val md5 = str.md5()
            val ooo = "$date|$md5"
            val doc = Jsoup.connect(url).config()
                .referrer(ref)
                .header(
                    "Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
                )
                .header("Accept-Encoding", "gzip,deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .header("Connection", "keep-alive")
                .header("Host", "m.ixinmoo.com")
                .header("Referer", ref)
                .header("Upgrade-Insecure-Requests", "1")
                .cookie("ooo", ooo)
                .get()
            return@setUp doc.getElementsByTag("source")?.first()?.attr("src") ?: ""
        }
        return AudioUrlCustomExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val url = "http://m.ixinmoo.com/"
        val doc = getUrl(url, url)
        val list = ArrayList<CategoryMenu>()
        val subMenu = doc.select(".dh > a").map {
            CategoryTab(it.text(), it.absUrl("href"))
        }
        list.add(CategoryMenu("列表", subMenu))
        return list
    }

    override fun getCategoryList(url: String): Category {
        val doc = getUrl(url, "http://m.ixinmoo.com/")
        val uiPage = doc.select(".pages.cate-pages.clearfix > ul > li")
        val pageNum = uiPage.select("li")[1].text()
        val nextUrl = uiPage.select("li")[2].selectFirst("a").absUrl("href")
        val totalPage = pageNum.split("/")[1].toInt()
        val currentPage = pageNum.split("/")[0].toInt()
        val list = ArrayList<Book>()
        doc.select(".xsdz > .list-ov-tw").forEach {
            val coverUrl = it.selectFirst(".list-ov-t > a > img").absUrl("src")
            val bookUrl = it.selectFirst(".list-ov-w > a").absUrl("href")
            val title = it.select(".list-ov-w > a > .bt").text()
            var author = it.select(".list-ov-w > a > .zz")[0].text()
            var artist = it.select(".list-ov-w > a > .zz")[1].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = getStatus(bookUrl)
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        var info = ""
        if (loadEpisodes) {
            val doc = getUrl(bookUrl, bookUrl)
            doc.select(".compress > ul > li").forEach {
                episodes.add(Episode(it.text(), it.select("a").first().absUrl("href")))
            }
            info = doc.selectFirst(".book_intro").text()
        }
        return BookDetail(episodes, info)
    }

    private fun getUrl(url: String, ref: String): org.jsoup.nodes.Document {
        val conn = Jsoup.connect(url).config()
        conn.header("Connection", "keep-alive")
        conn.header("Host", "m.ixinmoo.com")
        conn.header("Upgrade-Insecure-Requests", "1")
        conn.referrer(ref)
        conn.cookie("ooo", getCookie())
        return conn.get()
    }

    private fun String?.md5(): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest: ByteArray = instance.digest(this?.toByteArray())
            var sb: StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i: Int = b.toInt() and 0xff
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

    private fun getCookie(): String {
        val date = Date().time.toString()
        val str = "1234" + date + "115599"
        return "$date|${str.md5()}"
    }

    private fun getStatus(bookUrl: String): String {
        return "完结"
    }

}