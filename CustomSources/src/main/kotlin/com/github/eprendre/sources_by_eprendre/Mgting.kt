package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewExtractor
import com.github.eprendre.tingshu.sources.CoverUrlExtraHeaders
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URLEncoder

object Mgting : TingShu(), CoverUrlExtraHeaders {
    override fun getSourceId(): String {
        return "34297649ff264078833e6283b496e6ed"
    }

    override fun getUrl(): String {
        return "https://www.mgting.com/"
    }

    override fun getName(): String {
        return "芒果听书"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n速度很慢"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val url = "https://www.mgting.com/search.php?page=$page&searchword=${URLEncoder.encode(keywords, "utf8")}&searchtype="
        val doc = Jsoup.connect(url).config(true).get()
        val clist = doc.select(".clist")
        val lis = clist.select("li")
        val books = lis.map { li ->
            val a = li.selectFirst("a")
            //标题
            val title = li.select("p")[0].selectFirst("a").text()
            //书籍链接
            val href = a.absUrl("href")
            //图片链接
            val img = a.selectFirst(".imgc").absUrl("src")
            //作者名称
            val author = li.select("p")[1].text()
            //播音
            val art = li.select("p")[3].text()
            //更新时间
            val status = li.select("p")[4].text()
            //println("标题:$title,链接:$href,图片:$img,作者:$author,播音:$art,更新时间:$status")
            Book(img, href, title, author, art).apply {
                this.sourceId = getSourceId()
                this.status = status
            }
        }
        //获取翻页信息
        val page = doc.selectFirst(".page")
        val span = page.selectFirst("span")
        //共有页
        val totalPage = Regex("页次:\\d*/(.+?)页").find(span.text())?.groupValues?.get(1)?.toInt() ?: 1
        return Pair(books, totalPage)
    }

    //开始播放
    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        //需要js加载
        AudioUrlWebViewExtractor.setUp(script = "document.getElementsByTagName(\"iframe\")[0].contentDocument.getElementById(\"jp_audio_0\").src") {
            return@setUp it.replace("\"", "")
        }
        return AudioUrlWebViewExtractor
    }

    //书籍分类
    override fun getCategoryMenus(): List<CategoryMenu> {
        //网站
        val url = "https://www.mgting.com/"
        val doc = Jsoup.connect(url).config(true).get()
        //通过分析得知获取id=nav
        val nav = doc.getElementById("nav")
        //获取所有的li（分类）
        val lis = nav.select("li")
        //保存分类
        val list = ArrayList<CategoryTab>()
        lis.forEach { li ->
            val title = li.text()
            val href = li.selectFirst("a").absUrl("href")
            if (title != "首页")
                list.add(CategoryTab(title, href))
        }
        return listOf(CategoryMenu("标题", list))
    }

    //书籍列表
    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config(true).get()
        val clist = doc.select(".clist")
        val lis = clist.select("li")
        val books = lis.map { li ->
            val a = li.selectFirst("a")
            //标题
            val title = a.attr("title")
            //书籍链接
            val href = a.absUrl("href")
            //图片链接
            val img = a.selectFirst(".imgc").absUrl("src")
            //作者名称
            val author = li.select("p")[1].text()
            //播音
            val art = li.select("p")[3].text()
            //更新时间
            val status = li.select("p")[4].text()
            //println("标题:$title,链接:$href,图片:$img,作者:$author,播音:$art,更新时间:$status")
            Book(img, href, title, author, art).apply {
                this.sourceId = getSourceId()
                this.status = status
            }
        }
        //获取翻页信息
        val page = doc.selectFirst(".page")
        val span = page.selectFirst("span")
        //当前页
        val currentPage = Regex("fenlei/\\d*-(.+?).html").find(url)?.groupValues?.get(1)?.toInt() ?: 1
        //共有页
        val totalPage = Regex("页次:\\d*/(.+?)页").find(span.text())?.groupValues?.get(1)?.toInt() ?: 1
        //下一页
        val nextPage = if (currentPage < totalPage) currentPage + 1 else currentPage
        //书籍的ID
        val bookId = Regex("fenlei/(.+?)-\\d*.html").find(url)?.groupValues?.get(1) ?: Regex("fenlei/(.+?).html").find(
            url
        )?.groupValues?.get(1) ?: "error"
        //下一页网址
        val nextUrl = "https://www.mgting.com/fenlei/$bookId-$nextPage.html"
        return Category(books, currentPage, totalPage, url, nextUrl)
    }

    //章节列表
    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val list = ArrayList<Episode>()
        var intro = ""
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(true).get()
            val ul = doc.selectFirst(".compress")
            val lis = ul.select("li")
            intro = doc.selectFirst(".introBox").text()
            lis.forEach { li ->
                val title = li.selectFirst("a").attr("title")
                val href = li.selectFirst("a").absUrl("href")
                list.add(Episode(title, href))
            }
        }
        return BookDetail(list, intro)
    }

    override fun coverHeaders(coverUrl: String, headers: MutableMap<String, String>): Boolean {
        if (coverUrl.contains("mgting.com")) {
            headers["referer"] = "https://www.mgting.com/"
            return true
        }
        return false
    }

}