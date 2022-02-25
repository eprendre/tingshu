package com.github.eprendre.tingshu.utils

data class Book(
    var coverUrl: String,//封面链接
    val bookUrl: String,//书籍链接
    val title: String,//标题
    var author: String,//作者
    var artist: String//演播
) {
    var id: Int? = null
    var intro: String = ""
    var currentEpisodeUrl: String? = null
    var currentEpisodeName: String? = null
    var currentEpisodePosition: Long = 0
    var skipBeginning: Long = 0
    var skipEnd: Long = 0
    var volumeBoostLevel: Int = 0
    var playOrderType: Int = 0
    var playSpeed: Float = 1f
    var isFree: Boolean = true
    var isEpisodesReversed: Boolean = false
    var episodeList: List<Episode>? = null
    var hasFullEpisodes: Boolean = false//只有 source 的 isMultipleEpisodePages 为 true 时，这个属性才起作用。
    var isShowBriefChapterTitle: Boolean = false
    var sourceId: String? = null
    var status: String = ""
    var episodesUpdateTime: Long = 0
    var isTransientEpisodes: Boolean = false //如果章节列表是动态变化的，把这个参数设置为true。在每次进入播放页时会自动刷新。将在1.8.6后加入
    var isCompleted: Boolean = false//是否已完结，如果是，app里面将不再触发章节列表的自动刷新。

    override fun equals(other: Any?): Boolean {
        throw RuntimeException("Stub!")
    }

    override fun hashCode(): Int {
        throw RuntimeException("Stub!")
    }

    fun copyFrom(book: Book) {
        throw RuntimeException("Stub!")
    }
}

data class Episode(val title: String, val url: String) {
    var isFree: Boolean = true
    var isCached: Boolean = false
    var progress: Int = 0
}

interface IMenu {
    fun getType(): Int
}

data class CategoryTab(
    val title: String,
    val url: String
) : IMenu {

    override fun getType(): Int {
        throw RuntimeException("Stub!")
    }
}

data class BookDetail(
    val playList: List<Episode>,
    val intro: String? = "",
    val artist: String = "",
    val author: String = "",
    val episodesCount: Int = 0,
    val coverUrl: String = ""
)


/**
 * 大分类
 */
data class CategoryMenu(
    val title: String, //大分类标题
    val tabs: List<CategoryTab>//子分类
) : IMenu {

    override fun getType(): Int {
        throw RuntimeException("Stub!")
    }

    override fun equals(other: Any?): Boolean {
        throw RuntimeException("Stub!")
    }

    override fun hashCode(): Int {
        throw RuntimeException("Stub!")
    }
}

data class Category(
    val list: List<Book>,
    val currentPage: Int,
    val totalPage: Int,
    val currentUrl: String,
    val nextUrl: String
)