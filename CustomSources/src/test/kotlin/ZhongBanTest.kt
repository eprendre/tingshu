import com.github.eprendre.tingshu.extensions.config
import com.github.eprendre.tingshu.utils.Episode
import com.google.gson.Gson
import org.jsoup.Jsoup
import org.junit.Test

class ZhongBanTest {
    @Test
    fun bookDetail() {
        val bookUrl = "http://ebook.3eol.com.cn/database/webreader/Index?id=466&resid=17&libraryid=0&enc=9e8c98f05fc79f43e26b28ccab9d9e63"
        val doc = Jsoup.connect(bookUrl).testConfig().get()

        val script = doc.select("script").first { it.html().contains("audio.audioSrcArr = ") }.html().trim()
        println(script)
        val srcJson = Regex("audioSrcArr = (\\[.+])").find(script)!!.groupValues[1]
        val titleJson = Regex("audioTitleArr = (\\[.+])").find(script)!!.groupValues[1]
        val srcArray = Gson().fromJson(srcJson, Array<String>::class.java)
        val titleArray = Gson().fromJson(titleJson, Array<String>::class.java)
        val episodes = ArrayList<Episode>()
        titleArray.forEachIndexed  { index, title ->
            val episode = Episode(title, "http://ebook.3eol.com.cn${srcArray[index]}")
            episodes.add(episode)
        }

        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }
}