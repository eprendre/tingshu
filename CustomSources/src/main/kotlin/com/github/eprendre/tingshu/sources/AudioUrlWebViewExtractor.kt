package com.github.eprendre.tingshu.sources

/**
 * 某些站点需要渲染一遍网页才能在页面元素提取音频地址的，请用此类。
 */
object AudioUrlWebViewExtractor : AudioUrlExtractor {
    /**
     * @param isDeskTop true 加载 PC 页面, false 加载手机页面
     * @param script 当 WebView 加载完毕后运行的 js 脚本， 默认是提取 html。
     * @param parse 处理脚本运行结果的回调，如果脚本返回的是 html 页面，可通过 jsoup 后续解析。
     * 然后返回获取到的音频地址，若获取失败请返回 null。因为当程序检测到为 null 时会进行若干次尝试直到超时。
     */
    @JvmOverloads
    fun setUp(
        isDeskTop: Boolean = false,
        script: String = "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
        parse: (String) -> String?
    ) {
        throw RuntimeException("Stub!")
    }

    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        throw RuntimeException("Stub!")
    }
}