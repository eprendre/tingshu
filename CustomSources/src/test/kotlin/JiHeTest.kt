import assertk.assertThat
import assertk.assertions.isGreaterThan
import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import org.jsoup.Jsoup
import org.junit.Test

class JiHeTest {
    @Test
    fun bookDetail() {
        val doc = Jsoup.connect("https://www.gcores.com/radios/138374").testConfig(false).get()
        val downloadUrl = doc.selectFirst("svg[data-icon=download]").parent().absUrl("href")
        val episodes = ArrayList<Episode>()
        Episode("1", downloadUrl).apply {
            episodes.add(this)
        }

        episodes.take(20).forEach { println(it) }
    }

    @Test
    fun category() {
        val doc = Jsoup.connect("https://www.gcores.com/radios?page=2").testConfig(false).get()
        val totalPage = 132
        val currentPage = doc.selectFirst(".pagination_item.is_active > a").text().toInt()
        val nextUrl = doc.selectFirst(".pagination_next > a").absUrl("href")

        println("$currentPage/$totalPage")
        println("nextUrl: $nextUrl")

        val list = ArrayList<Book>()
        val elementList = doc.select(".original-radio")
        elementList.forEach { element ->
            val coverUrl = element.selectFirst(".original_imgArea").attr("style")
                .replace("background-image:url(", "")
                .replace(")", "")
            val bookUrl = element.selectFirst(".am_card_inner > .am_card_content.original_content").absUrl("href")
            val title = element.selectFirst(".am_card_title").text()
            val author = ""
            val artist = element.selectFirst(".avatar_text > h3").text()
            list.add(Book(coverUrl, bookUrl, title, author, artist).apply {
            })
        }
        list.forEach { println(it) }
        assertThat(list.size).isGreaterThan(0)

    }

}