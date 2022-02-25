package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLEncoder

object WoAiTingPingShu : TingShu(), AudioUrlExtraHeaders {
    override fun getSourceId(): String {
        return "cc41d56926064805b0393780ec758e2a"
    }

    override fun getUrl(): String {
        return "https://m.tpsge.com"
    }

    override fun getName(): String {
        return "我爱听评书网"
    }

    override fun getDesc(): String {
        return "推荐指数:3星 ⭐⭐⭐"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "https://m.tpsge.com/so?sid=1&q=$encodedKeywords"
        val doc = Jsoup.connect(url).config().get()

        val totalPage = 1
        val list = ArrayList<Book>()
        val elementList = doc.select(".story_list_class > dd")
        elementList.forEach { element ->
            val a = element.selectFirst("a")
            val bookUrl = a.absUrl("href")
            val coverUrl = ""
            val title = a.text()
            val author = ""
            val span = element.selectFirst("span")
            if (span != null && a.selectFirst("font") != null) {
                val artist = span.text()
                list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                    this.sourceId = getSourceId()
                })
            }
        }

        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = ArrayList<Episode>()
        val doc = Jsoup.connect(bookUrl).config().get()
        val text = doc.selectFirst("#story_msg").ownText()
        val author = Regex("作者：(.*?) ").find(text)?.groupValues?.get(1) ?: ""
        val intro = doc.selectFirst(".desc").children().last().text()
        val coverUrl = doc.selectFirst(".desc > table > tbody > tr > .tl > img").absUrl("src")

        episodes.addAll(doc.select(".mp3_list > dd  > a").map {
            Episode(it.text(), it.absUrl("href"))
        })

        return BookDetail(episodes, intro, author = author, coverUrl = coverUrl)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        AudioUrlWebViewExtractor.setUp { str ->
            val doc = Jsoup.parse(str)
            val audioElement = doc.selectFirst("#jp_audio_0")
            return@setUp audioElement?.attr("src")?.replace("http://", "https://")
        }
        return AudioUrlWebViewExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 =
            CategoryMenu("评书分类", listOf(
                CategoryTab("单田芳", "https://m.tpsge.com/boyin/hot/dantianfang"),
                CategoryTab("袁阔成", "https://m.tpsge.com/boyin/hot/yuankuocheng"),
                CategoryTab("孙岩", "https://m.tpsge.com/boyin/hot/sunyan"),
                CategoryTab("刘兰芳", "https://m.tpsge.com/boyin/hot/liulanfang"),
                CategoryTab("连丽如", "https://m.tpsge.com/boyin/hot/lianliru"),
                CategoryTab("刘纪同", "https://m.tpsge.com/boyin/hot/liujitong"),
                CategoryTab("张少佐", "https://m.tpsge.com/boyin/hot/zhangshaozuo"),
                CategoryTab("周建龙", "https://m.tpsge.com/boyin/hot/zhoujianlong"),
                CategoryTab("田连元", "https://m.tpsge.com/boyin/hot/tianlianyuan"),
                CategoryTab("石连壁", "https://m.tpsge.com/boyin/hot/shilianbi"),
                CategoryTab("关永超", "https://m.tpsge.com/boyin/hot/guanyongchao"),
                CategoryTab("孙一", "https://m.tpsge.com/boyin/hot/sunyi"),
                CategoryTab("郭德纲", "https://m.tpsge.com/boyin/hot/guodegang"),
                CategoryTab("赵维莉", "https://m.tpsge.com/boyin/hot/zhaoweili"),
                CategoryTab("仲维维", "https://m.tpsge.com/boyin/hot/zhongweiwei"),
                CategoryTab("孙刚", "https://m.tpsge.com/boyin/hot/sungang"),
                CategoryTab("王玥波", "https://m.tpsge.com/boyin/hot/wangyuebo"),
                CategoryTab("田战义", "https://m.tpsge.com/boyin/hot/tianzhanyi"))
            )

        val menu2 = CategoryMenu("有声小说", listOf(
            CategoryTab("网络玄幻", "https://m.tpsge.com/fenlei/hot/wlxh"),
            CategoryTab("刑侦推理", "https://m.tpsge.com/fenlei/hot/xztl"),
            CategoryTab("历史军事", "https://m.tpsge.com/fenlei/hot/lsjs"),
            CategoryTab("官场商战", "https://m.tpsge.com/fenlei/hot/gcsz"),
            CategoryTab("人物纪实", "https://m.tpsge.com/fenlei/hot/rwjs"),
            CategoryTab("都市言情", "https://m.tpsge.com/fenlei/hot/dsyq"),
            CategoryTab("儿童读物", "https://m.tpsge.com/fenlei/hot/etdw"),
            CategoryTab("相声小品", "https://m.tpsge.com/fenlei/hot/xsxp"),
            CategoryTab("武侠小说", "https://m.tpsge.com/fenlei/hot/wxxs"),
            CategoryTab("恐怖悬疑", "https://m.tpsge.com/fenlei/hot/kbxy"),
            CategoryTab("百家讲坛", "https://m.tpsge.com/fenlei/hot/bjjt"),
            CategoryTab("粤语评书", "https://m.tpsge.com/fenlei/hot/yyps"),
            CategoryTab("有声文学", "https://m.tpsge.com/fenlei/hot/yswx"),
            CategoryTab("英文读物", "https://m.tpsge.com/fenlei/hot/ywdw"),
            CategoryTab("广播剧", "https://m.tpsge.com/fenlei/hot/gbj"),
            CategoryTab("戏曲", "https://m.tpsge.com/fenlei/hot/xq"),
            CategoryTab("综艺", "https://m.tpsge.com/fenlei/hot/zy"),
            CategoryTab("养生", "https://m.tpsge.com/fenlei/hot/ys"))
            )

        return listOf(menu1, menu2)
    }

    override fun getCategoryList(url: String): Category {
        val doc = Jsoup.connect(url).config().get()
        val nextUrl = doc.select(".page > a").firstOrNull { it.text().contains("下一页") }?.absUrl("href") ?: ""
        val currentPage = doc.selectFirst(".page > font")?.text()?.toInt() ?: 1
        val totalPage = doc.selectFirst(".page").ownText().split("/")[1].toInt()

        val list = ArrayList<Book>()
        val elementList = doc.select(".story_list_class > dd > a")
        elementList.forEach { element ->
            val bookUrl = element.absUrl("href")
            val coverUrl = ""
            val title = element.ownText()
            val author = ""
            val span = element.selectFirst("span")
            val array = span.text().split("／")
            val artist = array[0]
            val status = array[1]
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.status = status
                this.sourceId = getSourceId()
            })
        }

        return Category(list, currentPage, totalPage, url, nextUrl)
    }

    override fun headers(audioUrl: String): Map<String, String> {
        val hashMap = hashMapOf<String, String>()
        if (audioUrl.contains("tpsge.com")) {
            hashMap["Host"] = URL(audioUrl).host
        }
        return hashMap
    }

}