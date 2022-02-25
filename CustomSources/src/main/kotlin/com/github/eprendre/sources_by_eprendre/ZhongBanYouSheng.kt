package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.getMobileUA
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.net.URLEncoder
import kotlin.math.ceil

object ZhongBanYouSheng : TingShu() {
    override fun getSourceId(): String {
        return "e6b1890ef87e4f3682c68de3359a1397"
    }

    override fun getUrl(): String {
        return "http://guotu.audio.3eol.com.cn"
    }

    override fun getName(): String {
        return "中版有声书"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n有不少出版书"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?Resid=17&catid=0&pageIndex=$page&pageSize=9&keyword=$encodedKeywords"
        val result = Fuel.get(url)
            .header("X-Requested-With" to "XMLHttpRequest")
            .header("User-Agent" to getMobileUA())
            .responseJson()
        val jsonObject = result.third.get().obj()
        val body = jsonObject.getJSONObject("body")
        val totalAudio = body.getInt("totalAudio")
        val totalPage = ceil(totalAudio / 9.0).toInt()

        val audioList = body.getJSONArray("AudioList")

        val list = ArrayList<Book>()
        (0 until audioList.length()).forEach { index ->
            val audioObj = audioList.getJSONObject(index)
            try {
                val coverUrl = audioObj.getString("fullCoverImagePath")
                val bookUrl = "http://guotu.audio.3eol.com.cn/Mobile/Reader?id=${audioObj.getInt("id")}"
                val title = audioObj.getString("name")
                val author = audioObj.getString("publisher")
                val status = audioObj.getString("pubdate")
                val artist = "作者: ${audioObj.getString("author").replace("\\n", "")}"
                val intro = audioObj.getString("summary").replace("\\n", "").replace("<.+?>".toRegex(), "")
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.intro = intro
                    this.status = status
                    this.sourceId = getSourceId()
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config().get()

        val script = doc.select("script").first { it.html().contains("audio.audioSrcArr = ") }.html().trim()
        val srcJson = Regex("audioSrcArr = (\\[.+])").find(script)!!.groupValues[1]
        val titleJson = Regex("audioTitleArr = (\\[.+])").find(script)!!.groupValues[1]
        val srcArray = Gson().fromJson(srcJson, Array<String>::class.java)
        val titleArray = Gson().fromJson(titleJson, Array<String>::class.java)
        val episodes = ArrayList<Episode>()
        titleArray.forEachIndexed  { index, title ->
            val episode = Episode(title, "http://ebook.3eol.com.cn${srcArray[index]}")
            episodes.add(episode)
        }
        episodes.sortWith(EpisodeComparator())
        return BookDetail(episodes, "")
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "首页", listOf(
//                CategoryTab("外国文学", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=1&pageIndex=1&pageSize=18"),
                CategoryTab("推理悬疑", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=2&pageIndex=1&pageSize=18"),
                CategoryTab("时事战争", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=3&pageIndex=1&pageSize=18"),
                CategoryTab("官场商战", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=4&pageIndex=1&pageSize=18"),
                CategoryTab("当代文学", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=5&pageIndex=1&pageSize=18"),
                CategoryTab("纪实文学", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=6&pageIndex=1&pageSize=18"),
                CategoryTab("都市生活", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=7&pageIndex=1&pageSize=18"),
                CategoryTab("人文旅游", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=8&pageIndex=1&pageSize=18"),
                CategoryTab("人物传记", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=9&pageIndex=1&pageSize=18"),
                CategoryTab("评论访谈", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=10&pageIndex=1&pageSize=18"),
                CategoryTab("儿童文学", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=11&pageIndex=1&pageSize=18"),
                CategoryTab("历史军事", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=12&pageIndex=1&pageSize=18"),
                CategoryTab("情感故事", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=13&pageIndex=1&pageSize=18"),
                CategoryTab("职场励志", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=14&pageIndex=1&pageSize=18"),
                CategoryTab("影视原著", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=15&pageIndex=1&pageSize=18"),
                CategoryTab("生活百科", "http://guotu.audio.3eol.com.cn/Mobile/GetBooks?ResID=17&catid=16&pageIndex=1&pageSize=18")
            )
        )
        return listOf(menu1)
    }

    override fun getCategoryList(url: String): Category {
        val result = Fuel.get(url)
            .header("X-Requested-With" to "XMLHttpRequest")
            .header("User-Agent" to getMobileUA())
            .responseJson()
        val jsonObject = result.third.get().obj()
        val body = jsonObject.getJSONObject("body")
        val totalAudio = body.getInt("totalAudio")
        val totalPage = ceil(totalAudio / 9.0).toInt()
        val currentPage = Regex("pageIndex=(\\d+)").find(url)?.groupValues?.get(1)?.toInt() ?: 0
        val nextUrl = url.replace("pageIndex=(\\d+)".toRegex(), "pageIndex=${currentPage + 1}")

        val audioList = body.getJSONArray("AudioList")

        val list = ArrayList<Book>()
        (0 until audioList.length()).forEach { index ->
            val audioObj = audioList.getJSONObject(index)
            try {
                val coverUrl = audioObj.getString("fullCoverImagePath")
                val bookUrl = "http://guotu.audio.3eol.com.cn/Mobile/Reader?id=${audioObj.getInt("id")}"
                val title = audioObj.getString("name")
                val author = audioObj.getString("publisher")
                val status = audioObj.getString("pubdate")
                val artist = "作者: ${audioObj.getString("author").replace("\\n", "")}"
                val intro = audioObj.getString("summary").replace("<.+?>|\\\\n".toRegex(), "")
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.intro = intro
                    this.status = status
                    this.sourceId = getSourceId()
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    class EpisodeComparator : Comparator<Episode> {
        private fun isDigit(ch: Char): Boolean {
            return ch.toInt() in 48..57
        }

        private fun getChunk(s: String, slength: Int, _marker: Int): String {
            var marker = _marker
            val chunk = StringBuilder()
            var c = s[marker]
            chunk.append(c)
            marker++
            if (isDigit(c)) {
                while (marker < slength) {
                    c = s[marker]
                    if (!isDigit(c)) break
                    chunk.append(c)
                    marker++
                }
            } else {
                while (marker < slength) {
                    c = s[marker]
                    if (isDigit(c)) break
                    chunk.append(c)
                    marker++
                }
            }
            return chunk.toString()
        }

        override fun compare(e1: Episode, e2: Episode): Int {
            val s1 = e1.title
            val s2 = e2.title
            var thisMarker = 0
            var thatMarker = 0
            val s1Length = s1.length
            val s2Length = s2.length
            while (thisMarker < s1Length && thatMarker < s2Length) {
                val thisChunk = getChunk(s1, s1Length, thisMarker)
                thisMarker += thisChunk.length
                val thatChunk = getChunk(s2, s2Length, thatMarker)
                thatMarker += thatChunk.length

                // If both chunks contain numeric characters, sort them numerically
                var result = 0
                if (isDigit(thisChunk[0]) && isDigit(thatChunk[0])) {
                    result = thisChunk.toInt().compareTo(thatChunk.toInt())
                } else {
                    result = thisChunk.compareTo(thatChunk)
                }
                if (result != 0) return result
            }
            return s1Length - s2Length
        }
    }
}