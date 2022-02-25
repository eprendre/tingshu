import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.junit.Test

class BoKanYouShengTest {

    @Test
    fun bookDetail() {
        val id = "904730"
        val url = "https://api.bookan.com.cn/voice/album/units?album_id=$id&page=1&num=20&order=1"
        val jsonArray = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data").getJSONArray("list")
        val episodes = ArrayList<Episode>()
        (0 until jsonArray.length()).forEach {
            val obj = jsonArray.getJSONObject(it)
            val title = obj.getString("title")
            val file = obj.getString("file")
            episodes.add(Episode(title, file))
        }
        episodes.take(5).forEach {
            println(it)
        }
    }

    @Test
    fun categoryList() {
        val instance_id = 25304
        val catetgory_id = 1308
//        val url = "https://api.bookan.com.cn/voice/recommend/resources?instance_id=25304&section_type=8&num=300&show_latest_issue=0"
        //val url = "https://api.bookan.com.cn/voice/album/list?instance_id=25304&page=1&category_id=8&num=24
        val url = "https://api.bookan.com.cn/voice/book/list?instance_id=$instance_id&page=1&category_id=$catetgory_id&num=24"
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        val currentPage = data.getInt("current_page")
        val totalPage = data.getInt("last_page")
        val nextUrl = "https://api.bookan.com.cn/voice/book/list?instance_id=$instance_id&page=${currentPage + 1}&category_id=$catetgory_id&num=24"
        val list = ArrayList<Book>()
        val jsonArray = data.getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("id").toString()
            val title = item.getString("name")
            val artist = ""
            val author = ""
            val status = "共 ${item.getInt("total")} 章"
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                }
            )
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    fun radio() {
        val url1 = "https://apidata.bookan.com.cn/(radio)_(channels)__(instanceId)_(25304)_(page)_(1)_(pagenum)_()_(tags)_()"
        val url2 = "https://apidata.bookan.com.cn/(radio)_(channels)__(instanceId)_(25304)_(page)_(2)_(pagenum)_()_(tags)_()"
    }

}