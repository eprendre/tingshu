package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.extensions.splitQuery
import com.github.eprendre.tingshu.sources.*
import com.github.eprendre.tingshu.utils.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.jsoup.Jsoup
import java.net.URL
import java.net.URLEncoder

object LibriVox : TingShu() {
    override fun getSourceId(): String {
        return "b722b6d38f1a4a25b453e8f84e5e331a"
    }

    override fun getUrl(): String {
        return "https://librivox.org"
    }

    override fun getName(): String {
        return "LibriVox 测试"
    }

    override fun getDesc(): String {
        return "服务器位于: 美国\n需要科学上网才能正常打开封面和播放。\n当之无愧的全世界最大的自制有声书社区，都是志愿者录制的公共版权的英语有声书。优点是基本上出名的书都有有声书版本，包括非常少有有声版本的文史哲书籍。学生党如果要学习本学科专业著作，大可以下载对应名著，边听边读，一条网线即座与哈佛牛津学生同窗。"
    }

    override fun isWebViewNotRequired(): Boolean {
        return true
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8")
        val url = "https://librivox.org/advanced_search?title=&author=&reader=&keywords=&genre_id=0&status=all&project_type=either&recorded_language=&sort_order=alpha&search_page=$page&search_form=advanced&q=$encodedKeywords"
        val result = Fuel.get(url)
            .header("X-Requested-With" to "XMLHttpRequest")
            .responseJson()
        val jsonObject = result.third.get().obj()
        val paginationJson = jsonObject.getString("pagination")
        var totalPage = 1
        if (!paginationJson.isNullOrEmpty()) {
            val pagination = Jsoup.parse(paginationJson)
            totalPage = pagination.selectFirst(".last").attr("data-page_number").toInt()
        }

        val doc = Jsoup.parse(jsonObject.getString("results"))

        val list = ArrayList<Book>()
        val elementList = doc.select(".catalog-result")
        elementList.forEach { element ->
            try {
                val coverUrl = element.selectFirst(".book-cover > img").absUrl("src")
                val bookUrl = element.selectFirst(".result-data > h3 > a").absUrl("href")
                val title = element.selectFirst(".result-data > h3 > a").text()
                val author = element.select(".book-author > a").text()
                val infos = element.select(".book-meta").text().split("|")
                val status = infos[0].trim()
                val artist = infos[1].trim()
                val intro = infos[2].trim()
                if (!bookUrl.startsWith("https://forum.librivox.org")) {
                    list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                        this.intro = intro
                        this.status = status
                        this.sourceId = getSourceId()
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Pair(list, totalPage)
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val doc = Jsoup.connect(bookUrl).config(true).get()

        val episodes = doc.select(".chapter-download > tbody > tr > td > .chapter-name").map {
            Episode(it.text(), it.absUrl("href"))
        }
        val intro = doc.selectFirst(".description")?.text()
        return BookDetail(episodes, intro)
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu(
            "Children", listOf(
                CategoryTab("Children's Fiction", "https://librivox.org/search/get_results?primary_key=1&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Action & Adventure", "https://librivox.org/search/get_results?primary_key=37&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Animals & Nature", "https://librivox.org/search/get_results?primary_key=38&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Myths, Legends & Fairy Tales", "https://librivox.org/search/get_results?primary_key=39&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Family", "https://librivox.org/search/get_results?primary_key=40&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("General", "https://librivox.org/search/get_results?primary_key=41&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Historical", "https://librivox.org/search/get_results?primary_key=42&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Poetry", "https://librivox.org/search/get_results?primary_key=43&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Religion", "https://librivox.org/search/get_results?primary_key=44&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("School", "https://librivox.org/search/get_results?primary_key=45&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Short works", "https://librivox.org/search/get_results?primary_key=46&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction", "https://librivox.org/search/get_results?primary_key=2&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction > Arts", "https://librivox.org/search/get_results?primary_key=47&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction > General", "https://librivox.org/search/get_results?primary_key=48&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction > Reference", "https://librivox.org/search/get_results?primary_key=49&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction > Religion", "https://librivox.org/search/get_results?primary_key=50&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Non-fiction > Science", "https://librivox.org/search/get_results?primary_key=51&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either")
            )
        )
        val menu2 = CategoryMenu(
            "Fantastic", listOf(
                CategoryTab("Fantastic Fiction", "https://librivox.org/search/get_results?primary_key=13&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Myths, Legends & Fairy Tales", "https://librivox.org/search/get_results?primary_key=11&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Horror & Supernatural Fiction", "https://librivox.org/search/get_results?primary_key=16&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Gothic Fiction", "https://librivox.org/search/get_results?primary_key=17&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Science Fiction", "https://librivox.org/search/get_results?primary_key=30&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Fantasy Fiction", "https://librivox.org/search/get_results?primary_key=55&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either")
            )
        )
        val menu3 = CategoryMenu(
            "Fiction", listOf(
                CategoryTab("General Fiction", "https://librivox.org/search/get_results?primary_key=15&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("General Fiction > Published before 1800", "https://librivox.org/search/get_results?primary_key=52&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("General Fiction > Published 1800-1900", "https://librivox.org/search/get_results?primary_key=53&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("General Fiction > Published 1900 onward", "https://librivox.org/search/get_results?primary_key=54&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Historical Fiction", "https://librivox.org/search/get_results?primary_key=18&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Humorous Fiction", "https://librivox.org/search/get_results?primary_key=19&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Literary Fiction", "https://librivox.org/search/get_results?primary_key=20&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Nature & Animal Fiction", "https://librivox.org/search/get_results?primary_key=21&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Nautical & Marine Fiction", "https://librivox.org/search/get_results?primary_key=23&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either")
            )
        )
        val menu4 = CategoryMenu(
            "Nonfiction", listOf(
                CategoryTab("Non-fiction", "https://librivox.org/search/get_results?primary_key=36&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("War & Military", "https://librivox.org/search/get_results?primary_key=73&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Animals", "https://librivox.org/search/get_results?primary_key=77&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Art, Design & Architecture", "https://librivox.org/search/get_results?primary_key=78&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Bibles", "https://librivox.org/search/get_results?primary_key=79&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Biography & Autobiography", "https://librivox.org/search/get_results?primary_key=80&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Biography & Autobiography > Memoirs", "https://librivox.org/search/get_results?primary_key=111&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Business & Economics", "https://librivox.org/search/get_results?primary_key=81&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Education", "https://librivox.org/search/get_results?primary_key=83&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Essays & Short Works", "https://librivox.org/search/get_results?primary_key=84&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("History", "https://librivox.org/search/get_results?primary_key=87&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("History > Antiquity", "https://librivox.org/search/get_results?primary_key=113&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("History > Middle Ages/Middle History", "https://librivox.org/search/get_results?primary_key=114&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("History > Early Modern", "https://librivox.org/search/get_results?primary_key=115&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("History > Modern (19th C)", "https://librivox.org/search/get_results?primary_key=116&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Modern (20th C)", "https://librivox.org/search/get_results?primary_key=117&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Nature", "https://librivox.org/search/get_results?primary_key=96&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Philosophy", "https://librivox.org/search/get_results?primary_key=98&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Political Science", "https://librivox.org/search/get_results?primary_key=99&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Psychology", "https://librivox.org/search/get_results?primary_key=100&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Religion", "https://librivox.org/search/get_results?primary_key=102&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Science", "https://librivox.org/search/get_results?primary_key=103&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Self-Help", "https://librivox.org/search/get_results?primary_key=104&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Social Science (Culture & Anthropology)", "https://librivox.org/search/get_results?primary_key=105&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Travel & Geography", "https://librivox.org/search/get_results?primary_key=108&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either")
            )
        )
        val menu5 = CategoryMenu(
            "Others", listOf(
                CategoryTab("Action & Adventure Fiction", "https://librivox.org/search/get_results?primary_key=3&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Crime & Mystery Fiction", "https://librivox.org/search/get_results?primary_key=5&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Crime & Mystery Fiction > Detective Fiction", "https://librivox.org/search/get_results?primary_key=22&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Plays", "https://librivox.org/search/get_results?primary_key=24&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Poetry", "https://librivox.org/search/get_results?primary_key=25&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Romance", "https://librivox.org/search/get_results?primary_key=27&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Satire", "https://librivox.org/search/get_results?primary_key=29&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Short Stories", "https://librivox.org/search/get_results?primary_key=31&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Short Stories > Anthologies", "https://librivox.org/search/get_results?primary_key=75&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Short Stories > Single Author Collections", "https://librivox.org/search/get_results?primary_key=76&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("War & Military Fiction", "https://librivox.org/search/get_results?primary_key=34&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either"),
                CategoryTab("Westerns", "https://librivox.org/search/get_results?primary_key=35&search_category=genre&sub_category=&search_page=1&search_order=catalog_date&project_type=either")
            )
        )
        return listOf(menu1, menu2, menu3, menu4, menu5)
    }

    override fun getCategoryList(url: String): Category {
        val result = Fuel.get(url)
            .header("X-Requested-With" to "XMLHttpRequest")
            .responseJson()
        val jsonObject = result.third.get().obj()
        val currentPage = jsonObject.getJSONObject("input").getInt("search_page")
        val pagination = Jsoup.parse(jsonObject.getString("pagination"))
        val totalPage = pagination.selectFirst(".last").attr("data-page_number").toInt()
        val queryMap = splitQuery(URL(url))
        queryMap["search_page"] = (currentPage + 1).toString()
        val nextUrl = "https://librivox.org/search/get_results?" + queryMap.map { "${it.key}=${it.value}" }.joinToString("&")

        val doc = Jsoup.parse(jsonObject.getString("results"))

        val list = ArrayList<Book>()
        val elementList = doc.select(".catalog-result")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".book-cover > img").absUrl("src")
            val bookUrl = element.selectFirst(".result-data > h3 > a").absUrl("href")
            val title = element.selectFirst(".result-data > h3 > a").text()
            val author = element.select(".book-author > a").text()
            val infos = element.select(".book-meta").text().split("|")
            val status = infos[0].trim()
            val artist = infos[1].trim()
            val intro = infos[2].trim()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
                this.intro = intro
                this.status = status
                this.sourceId = getSourceId()
            })
        }
        return Category(list, currentPage, totalPage, url, nextUrl)
    }
}