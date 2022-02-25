package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.AudioUrlJsoupExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup

object ShengBoFM : TingShu() {
    override fun getSourceId(): String {
        return "51047efa852e449da5f3144c1a46c9d8"
    }

    override fun getUrl(): String {
        return "http://fm.shengbo.org.cn"
    }

    override fun getName(): String {
        return "声波FM"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐\n众多视障伙伴学习、交流的平台\n网站已关闭，此源无法使用。"
    }

    override fun isDiscoverable(): Boolean {
        return false
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val totalPage = 1
        val list = ArrayList<Book>()
        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config().get()

        val episodes = ArrayList<Episode>()
        val title = doc.selectFirst(".card-header").text()
        if (loadEpisodes) {
            episodes.add(Episode(title, bookUrl))
        }

        val intro = doc.select(".card-body > .card-text").joinToString("\n") { it.text() }
        return BookDetail(episodes, intro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
       AudioUrlJsoupExtractor.setUp { doc ->
           return@setUp doc.selectFirst(".program-player > audio")?.absUrl("src") ?: ""
       }
        return AudioUrlJsoupExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu0 = CategoryMenu(
            "首页", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn"),
                CategoryTab("推荐节目", "http://fm.shengbo.org.cn/Index/index/recommend/1")
            )
        )
        val menu1 = CategoryMenu(
            "声波课堂", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/33"),
                CategoryTab("电脑/网络", "http://fm.shengbo.org.cn/Category/45"),
                CategoryTab("手机/数码", "http://fm.shengbo.org.cn/Category/46"),
                CategoryTab("医疗健康", "http://fm.shengbo.org.cn/Category/55"),
                CategoryTab("易学相关", "http://fm.shengbo.org.cn/Category/66"),
                CategoryTab("综合/其他", "http://fm.shengbo.org.cn/Category/71"),
                CategoryTab("2018年度金盲杖空间·视障者职业拓展计划", "http://fm.shengbo.org.cn/Category/76")
            )
        )
        val menu2 = CategoryMenu(
            "个人电台", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/34"),
                CategoryTab("综合台", "http://fm.shengbo.org.cn/Category/47"),
                CategoryTab("音乐台", "http://fm.shengbo.org.cn/Category/48"),
                CategoryTab("文学台", "http://fm.shengbo.org.cn/Category/59"),
                CategoryTab("曲艺台", "http://fm.shengbo.org.cn/Category/60")
            )
        )
        val menu3 = CategoryMenu(
            "视觉讲述", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/36"),
                CategoryTab("口述影像", "http://fm.shengbo.org.cn/Category/50"),
                CategoryTab("赛事讲解", "http://fm.shengbo.org.cn/Category/51"),
                CategoryTab("耳朵阅读", "http://fm.shengbo.org.cn/Category/63"),
                CategoryTab("耳朵旅行", "http://fm.shengbo.org.cn/Category/67"),
                CategoryTab("无锡新吴区阳光志愿者协会为盲人讲电影项目组", "http://fm.shengbo.org.cn/Category/82")
            )
        )
        val menu4 = CategoryMenu(
            "多才多艺", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/41"),
                CategoryTab("原创", "http://fm.shengbo.org.cn/Category/49"),
                CategoryTab("翻唱", "http://fm.shengbo.org.cn/Category/61"),
                CategoryTab("器乐", "http://fm.shengbo.org.cn/Category/62"),
                CategoryTab("朗诵", "http://fm.shengbo.org.cn/Category/65"),
                CategoryTab("曲艺、戏曲", "http://fm.shengbo.org.cn/Category/72"),
                CategoryTab("中国视障好声音2017", "http://fm.shengbo.org.cn/Category/74"),
                CategoryTab("2018狗年新春才艺大展示", "http://fm.shengbo.org.cn/Category/75"),
                CategoryTab("剑河杯歌唱比赛录音", "http://fm.shengbo.org.cn/Category/78")
            )
        )
        val menu5 = CategoryMenu(
            "校园广播", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/52"),
                CategoryTab("综合台", "http://fm.shengbo.org.cn/Category/53"),
                CategoryTab("音乐台", "http://fm.shengbo.org.cn/Category/54"),
                CategoryTab("文艺台", "http://fm.shengbo.org.cn/Category/68"),
                CategoryTab("曲艺台", "http://fm.shengbo.org.cn/Category/69")
            )
        )
        val menu6 = CategoryMenu(
            "活动录音", listOf(
                CategoryTab("最新节目", "http://fm.shengbo.org.cn/Category/56"),
                CategoryTab("在现场", "http://fm.shengbo.org.cn/Category/57"),
                CategoryTab("聊天室", "http://fm.shengbo.org.cn/Category/58"),
                CategoryTab("内容征集", "http://fm.shengbo.org.cn/Category/73"),
                CategoryTab("2018俄罗斯世界杯专题频道", "http://fm.shengbo.org.cn/Category/77"),
                CategoryTab("中国大爱联盟心理沙龙", "http://fm.shengbo.org.cn/Category/79")
            )
        )
        return listOf(menu0, menu1, menu2, menu3, menu4, menu5, menu6)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val pagination = doc.selectFirst(".pages > .pagination")
        var currentPage = 1
        var totalPage = 1
        var nextUrl = ""
        if (pagination.children().size > 0) {
            currentPage = pagination.selectFirst(".current").text().toInt()
            totalPage = pagination.selectFirst(".end")?.text()?.removePrefix("...")?.toInt() ?: currentPage
            nextUrl = pagination.selectFirst(".next")?.absUrl("href") ?: ""
        }

        val list = ArrayList<Book>()
        val elementList = doc.select(".row > .col-sm-8 > .card > .card-body > ul > li")
        elementList.forEach { element ->
            val children = element.children()
            val bookUrl = children[0].absUrl("href")
            val coverUrl = ""
            val title = children[0].text()
            val author = ""
            val artist = children[1].text()
            val status = children[3].text()
            val intro = children[2].text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.intro = intro
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }
}