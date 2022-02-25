package com.github.eprendre.tingshu.sources

import com.github.kittinunf.fuel.json.FuelJson

/**
 * 提取音频的地址返回json时，使用此类
 */
object AudioUrlJsonExtractor : AudioUrlExtractor {
    /**
     * @param isDesktop true 请求加上 PC UA, false 请求加上手机 UA
     * @param parse 处理json，参数类型为 FuelJson，通过 FuelJson.obj() 得到 JSONObject, FuelJson.array() 得到 JSONArray
     */
    @JvmOverloads
    fun setUp(isDesktop: Boolean = false, parse: (FuelJson) -> String) {
        throw RuntimeException("Stub!")
    }

    override fun extract(url: String, autoPlay: Boolean, isCache: Boolean, isDebug: Boolean) {
        throw RuntimeException("Stub!")
    }

}