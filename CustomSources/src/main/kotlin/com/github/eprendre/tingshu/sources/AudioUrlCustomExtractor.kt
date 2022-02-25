package com.github.eprendre.tingshu.sources

import com.github.kittinunf.fuel.json.FuelJson

/**
 * 自定义提取音频地址逻辑
 */
object AudioUrlCustomExtractor : AudioUrlExtractor {

    /**
     * @param parse 传入章节地址，解析后传出音频地址
     */
    fun setUp(parse: (String) -> String) {
        throw RuntimeException("Stub!")
    }

    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        throw RuntimeException("Stub!")
    }
}