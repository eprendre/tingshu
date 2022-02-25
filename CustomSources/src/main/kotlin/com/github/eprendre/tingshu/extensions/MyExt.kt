package com.github.eprendre.tingshu.extensions

import com.github.eprendre.tingshu.utils.Book
import io.reactivex.disposables.Disposable
import org.jsoup.Connection
import java.net.URL
import java.net.URLDecoder

fun splitQuery(url: URL): LinkedHashMap<String, String> {
    val queryPairs = LinkedHashMap<String, String>()
    val query = url.query
    val pairs = query.split("&")
    for (pair in pairs) {
        val idx = pair.indexOf("=")
        queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
            URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
    }
    return queryPairs
}

/**
 * 是否使用 PC 版的 UA，UA 字段会自动从 app 里的配置读取。
 */
fun Connection.config(isDesktop: Boolean = false): Connection {
    throw RuntimeException("Stub!")
}

/**
 * 获取 PC 版 UA
 */
fun getDesktopUA(): String {
    throw RuntimeException("Stub!")
}

/**
 * 获取手机版 UA
 */
fun getMobileUA(): String {
    throw RuntimeException("Stub!")
}

/**
 * WebView 登录之后，可以用此方法得到 Cookie
 */
fun getCookie(url: String): String? {
    throw RuntimeException("Stub!")
}

/**
 * 提示消息
 */
fun showToast(msg: String) {
    throw RuntimeException("Stub!")
}

/**
 * 获取当前正在播放的书籍
 * 2.1.1 开始加入
 * 2.1.7 改为 nullable
 */
fun getCurrentBook(): Book? {
    throw RuntimeException("Stub!")
}


/**
 * 加载多页章节列表时用到这个
 * 调用之后相关界面上会显示：正在加载章节列表: $pageInfo
 * 如果 pageInfo 传空，代表加载完毕
 */
fun notifyLoadingEpisodes(pageInfo: String?) {
    throw RuntimeException("Stub!")
}

/**
 * 适用于继承 AudioUrlExtractor，若需要嵌套调用多个 AudioUrlXXXExtractor 时务必调用此方法
 * backgroundTask: 网络请求或者耗时代码需要在后台线程处理, 返回一个 String
 * mainThreadCallback: 处理前者返回的String，并根据情况选择具体的 AudioUrlXXXExtractor (因为部分AudioUrlXXXExtractor需要在主线程执行）
 * 2.1.3 开始加入
 */
fun extractorAsyncExecute(
    url: String,
    autoPlay: Boolean,
    isCache: Boolean,
    isDebug: Boolean,
    backgroundTask: () -> String,
    mainThreadCallback: (String) -> Unit
): Disposable {
    throw RuntimeException("Stub!")
}