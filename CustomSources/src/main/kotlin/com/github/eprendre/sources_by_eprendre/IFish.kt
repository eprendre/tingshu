package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object IFish : TingShu() {
    override fun getSourceId(): String {
        return "e342700214a9450791c32f55d1f9fb78"
    }

    override fun getUrl(): String {
        return "https://ifish.fun"
    }

    override fun getName(): String {
        return "洛奇Town"
    }

    override fun getDesc(): String {
        return "推荐指数:4星 ⭐⭐⭐⭐\n每天精选一篇美文以及两首纯音乐，文章的主要来源为《每日一文》。纯音乐风格倾向后摇、电子、古典、新世纪、小清新等，源自个人日常搜藏，欢迎关注公众号：鱼声音乐精选。"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    /**
     * 没有搜索
     */
    override fun isSearchable(): Boolean {
        return false
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu = CategoryMenu(
                "音乐", listOf(
                CategoryTab("鱼声音乐精选", "https://ifish.fun/music/fish"),
                CategoryTab("落网音乐精选", "https://ifish.fun/music/luoo"),
                CategoryTab("发现音乐", "daily"))
        )
        return listOf(menu)
    }

    override fun getCategoryList(url: String): Category {
        if (url == "daily") {
            val list = ArrayList<Book>()
            val book1 = Book("", "https://ifish.fun/music/daily?t=wy", "云村部落", "", "").apply {
                this.sourceId = getSourceId()
            }
            val book2 = Book("", "https://ifish.fun/music/daily?t=xm", "大虾世界", "", "").apply {
                this.sourceId = getSourceId()
            }
            val book3 = Book("", "https://ifish.fun/music/daily?t=qq", "杂食天下", "", "").apply {
                this.sourceId = getSourceId()
            }
            list.add(book1)
            list.add(book2)
            list.add(book3)
            return Category(list, 1, 1, url, "")
        } else {
            val doc = Jsoup.connect(url).config(false).get()
            val pages = doc.select(".page-navigator > li > a")
            val nextPage = pages.first { it.text().contains("下一页") }
            var nextUrl = nextPage.absUrl("href")
            if (nextPage.hasClass("disabled")) {
                nextUrl = ""
            }
            val currentPage = pages.first { it.hasClass("current") }.text().toInt()
            val totalPage = pages.last().absUrl("href").split("p=")[1].toInt()

            val list = ArrayList<Book>()
            val elementList = doc.select(".daily-list > div > div")
            elementList.forEach { element ->
                val bookUrl = element.selectFirst("a").absUrl("href")
                val coverStyle = element.selectFirst("a > .item-thumb").attr("style")
                val coverUrl = Regex("url\\((.+)\\);").find(coverStyle)!!.groupValues[1]
                val children = element.selectFirst("a > .item-title").children()
                val title = children.first().text()
                var status = ""
                if (children.size > 1) {
                    status = children[1].text()
                }
                var intro = ""
                element.selectFirst(".item-summary")?.let {
                    intro = it.text()
                }
                list.add(Book(coverUrl, bookUrl, title, "", "").apply {
                    this.intro = intro
                    this.status = status
                    this.sourceId = getSourceId()
                })
            }
            return Category(list, currentPage, totalPage, url, nextUrl)
        }
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        var intro = ""
        if (loadEpisodes) {
            val doc = Jsoup.connect(bookUrl).config(false).get()
            doc.select(".player-list > ol > li").forEach {
                var url = it.selectFirst(".player-list-url").text()
                url = "https://ifish.fun${url}"
                val title = it.selectFirst(".player-list-name").text() + " - " + it.selectFirst(".player-list-author").text()
                episodes.add(Episode(title, url))
            }
            if (!bookUrl.contains("/daily")) {
                intro = doc.selectFirst(".text-center")?.outerHtml() ?: ""
            }
        }
        return BookDetail(episodes, intro = intro)
    }
}