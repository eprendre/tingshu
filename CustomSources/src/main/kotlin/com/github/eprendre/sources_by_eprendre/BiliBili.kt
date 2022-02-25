package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.getMobileUA
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.AudioUrlWebViewSniffExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import java.net.URLEncoder

object BiliBili: TingShu() {
    private val headers = mapOf(
        "User-Agent" to getMobileUA()
    )
    override fun getSourceId(): String {
        return "c893546d95f84db194046bd8de5dbcbb"
    }

    override fun getUrl(): String {
        return "https://m.bilibili.com"
    }

    override fun getName(): String {
        return "哔哩哔哩"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n b站也有一些有声小说，放松之余偶尔看看视频吧。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")

        val url = "https://api.bilibili.com/x/web-interface/search/all/v2?keyword=${encodedKeywords}&page=${page}&pagesize=20"
        val data = Fuel.get(url).header(headers).responseJson().third.get().obj().getJSONObject("data")
        val numPages = data.getInt("numPages")
        val list = ArrayList<Book>()
        val results = data.getJSONArray("result")
        run loop@{
            (0 until results.length()).forEach {
                val result = results.getJSONObject(it)
                if (result.getString("result_type") == "video") {
                    val videos = result.getJSONArray("data")
                    (0 until videos.length()).forEach { index ->
                        val videoObj = videos.getJSONObject(index)
                        val coverUrl = "https:" + videoObj.getString("pic")
                        val title = videoObj.getString("title")
                            .replace("<em class=\"keyword\">", "")
                            .replace("</em>", "")
                        val author = ""
                        val artist = videoObj.getString("author")
                        val bookUrl = "https://m.bilibili.com/video/" + videoObj.getString("bvid")
                        val status = "播放次数: " + videoObj.getInt("play")
                        val intro = videoObj.getString("description")

                        list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                            this.status = status
                            this.intro = intro
                            this.sourceId = getSourceId()
                        })
                    }
                    return@loop
                }
            }
        }
        return Pair(list, numPages)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewSniffExtractor.setUp(validateUrl = null)
        return AudioUrlWebViewSniffExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        return listOf(
            CategoryMenu(
                "推荐", listOf(
                    CategoryTab("有声小说","有声小说&&1"),
                    CategoryTab("同人音声","同人音声&&1"),
                    CategoryTab("英文小说","audiobooks&&1"),
                    CategoryTab("经典老歌","经典老歌&&1"),
                    CategoryTab("音乐推荐","音乐推荐&&1"),
                    CategoryTab("有声漫画","有声漫画&&1")
                )
            ))
    }

    override fun getCategoryList(url: String): Category {
        val params = url.split("&&")
        val keywords = params[0]
        val currentPage = params[1].toInt()
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")

        val currentUrl = "https://api.bilibili.com/x/web-interface/search/all/v2?keyword=${encodedKeywords}&page=${currentPage}&pagesize=20"
        val nextUrl = "${keywords}&&${currentPage + 1}"
        val data = Fuel.get(currentUrl).header(headers).responseJson().third.get().obj().getJSONObject("data")
        val numPages = data.getInt("numPages")
        val list = ArrayList<Book>()
        val results = data.getJSONArray("result")
        run loop@{
            (0 until results.length()).forEach {
                val result = results.getJSONObject(it)
                if (result.getString("result_type") == "video") {
                    val videos = result.getJSONArray("data")
                    (0 until videos.length()).forEach { index ->
                        val videoObj = videos.getJSONObject(index)
                        val coverUrl = "https:" + videoObj.getString("pic")
                        val title = videoObj.getString("title")
                            .replace("<em class=\"keyword\">", "")
                            .replace("</em>", "")
                        val author = ""
                        val artist = videoObj.getString("author")
                        val bookUrl = "https://m.bilibili.com/video/" + videoObj.getString("bvid")
                        val status = "播放次数: " + videoObj.getInt("play")
                        val intro = videoObj.getString("description")

                        list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                            this.status = status
                            this.intro = intro
                            this.sourceId = getSourceId()
                        })
                    }
                    return@loop
                }
            }
        }

        return Category(list, currentPage, numPages, url, nextUrl)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        if (loadEpisodes) {
            val bvid = bookUrl.split("/").last().replace("BV1", "")
            val url = "https://api.bilibili.com/x/web-interface/view/detail?aid=&bvid=$bvid"
            val data = Fuel.get(url).header(headers).responseJson().third.get().obj().getJSONObject("data")
            val pages = data.getJSONObject("View").getJSONArray("pages")
            (0 until pages.length()).forEach {
                val item = pages.getJSONObject(it)
                val page = item.getInt("page")
                val title = item.getString("part")
                episodes.add(Episode(title, "$bookUrl?p=$page"))
            }
        }
        return BookDetail(episodes)
    }
}