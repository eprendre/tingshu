package com.github.eprendre.tingshu.sources

/**
 * 有些源的章节地址已经是最终音频地址，不需要进行提取操作
 */
object AudioUrlDirectExtractor : AudioUrlExtractor {
    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        throw RuntimeException("Stub!")
    }
}