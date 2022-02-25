import com.github.eprendre.tingshu.utils.Book
import com.github.eprendre.tingshu.utils.Episode
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import org.junit.Test
import java.net.URLEncoder

class YunTuTest {
    val wechatID = "07955551-706c-4259-9aa0-db4627dfca57"

    @Test
    fun search() {
        val keywords = "仙" //搜索关键词
        val page = 1 //当前页数
        val encodedKeywords = URLEncoder.encode(keywords, "utf-8") //编码
        val url = "http://open-service.yuntuys.com/api/w_ys/book/search/wechat:$wechatID/$encodedKeywords?pageSize=20&pageNum=$page"

        val list = ArrayList<Book>()
        val jsonObject = Fuel.get(url).responseJson().third.get().obj()
        val totalPage = jsonObject.getJSONObject("data").getInt("totalPage")
        val jsonArray = jsonObject.getJSONObject("data").getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("bookId").toString()
            val title = item.getString("bookName")
            val artist = item.getString("anchorName")
            val author = item.getString("authorName")
            val status = "共 ${item.getInt("chapters")} 章"
            val intro = item.getString("summary")
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
                }
            )
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    @Test
    fun bookDetail() {
        val bookId = "12660"
        val url = "http://open-service.yuntuys.com/api/w_ys/book/getChapters/wechat:$wechatID/$bookId/true/asc?pageSize=20&pageNum=1"

        val episodes = ArrayList<Episode>()
        val list = Fuel.get(url).responseJson().third.get().obj()
            .getJSONObject("data").getJSONObject("pageQuery")
            .getJSONArray("list")
        (0 until list.length()).forEach {
            val item = list.getJSONObject(it)
            val name = item.getString("name")
            val audioUrl = item.getString("audioUrl")
            episodes.add(Episode(name, audioUrl))
        }
        episodes.take(10).forEach {
            println(it)
        }
        assert(episodes.isNotEmpty())
    }

    @Test
    fun categoryList() {
        val typeId = 585
        val url = "http://open-service.yuntuys.com/api/w_ys/book/getBookListByType/wechat:$wechatID/$typeId?pageNum=1&pageSize=20"

        val list = ArrayList<Book>()
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        val currentPage = data.getInt("pageNumber")
        val totalPage = data.getInt("totalPage")
        val nextUrl = "http://open-service.yuntuys.com/api/w_ys/book/getBookListByType/wechat:$wechatID/$typeId?pageNum=${currentPage + 1}&pageSize=20"
        val jsonArray = data.getJSONArray("list")
        (0 until jsonArray.length()).forEach { i ->
            val item = jsonArray.getJSONObject(i)
            val coverUrl = item.getString("cover")
            val bookUrl = item.getLong("bookId").toString()
            val title = item.getString("bookName")
            val artist = item.getString("anchorName")
            val author = item.getString("authorName")
            val status = "共 ${item.getInt("chapters")} 章"
            val intro = item.getString("summary")
            list.add(
                Book(
                    coverUrl,
                    bookUrl,
                    title,
                    author,
                    artist
                ).apply {
                    this.status = status
                    this.intro = intro
                }
            )
        }
        list.take(5).forEach {
            println(it)
        }
        assert(list.size > 0)
    }

    @Test
    fun menu() {
        val url = "http://open-service.yuntuys.com/api/w_ys/class_type/getHaveOrNoChildrenToPid/178"
        val data = Fuel.get(url).responseJson().third.get().obj().getJSONObject("data")
        val noSubset = data.getJSONArray("noSubset")
        (0 until noSubset.length()).forEach {
            val item = noSubset.getJSONObject(it)
            val name = item.getString("name")
            val classTypeId = item.getInt("classTypeId")
            println("CategoryTab(\"$name\", \"$classTypeId\"),")
        }
        val haveSubset = data.getJSONArray("haveSubset")
        (0 until haveSubset.length()).forEach { it1 ->
            val subSet = haveSubset.getJSONObject(it1)
            println("============================")
            val categoryName = subSet.getString("name")
            println(categoryName)
            val childrenList = subSet.getJSONArray("childrenList")
            (0 until childrenList.length()).forEach {
                val item = childrenList.getJSONObject(it)
                val name = item.getString("name")
                val classTypeId = item.getInt("classTypeId")
                println("CategoryTab(\"$name\", \"$classTypeId\"),")
            }

        }
    }

}