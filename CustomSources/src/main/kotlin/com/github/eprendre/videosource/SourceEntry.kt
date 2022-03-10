package com.github.eprendre.videosource

import com.github.eprendre.tingshu.sources.TingShu

object SourceEntry {

    /**
     * 说明
     */
    @JvmStatic
    fun getDesc(): String {
        return "视频源"
    }

    /**
     * 返回此包下面的源
     */
    @JvmStatic
    fun getSources(): List<TingShu> {
        return listOf(
            NanGua,
            JiuZhou,
            NiuNiu,
            YingHuaCD
        )
    }
}