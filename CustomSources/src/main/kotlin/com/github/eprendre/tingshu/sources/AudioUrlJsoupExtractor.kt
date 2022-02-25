package com.github.eprendre.tingshu.sources

import org.jsoup.nodes.Document

/**
 * Jsoup 就可搞定的音频提取用此类
 */
object AudioUrlJsoupExtractor : AudioUrlExtractor {
    /**
     * @param isDesktop true 请求加上 PC UA, false 请求加上手机 UA
     * @param parse 处理返回的 org.jsoup.nodes.Document
     */
    @JvmOverloads
    fun setUp(isDesktop: Boolean = false, parse: (Document) -> String) {
        throw RuntimeException("Stub!")
    }

    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        throw RuntimeException("Stub!")
    }

}