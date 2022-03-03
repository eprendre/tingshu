package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.sources.TingShu

object SourceEntry {

    /**
     * 说明
     */
    @JvmStatic
    fun getDesc(): String {
        return "听书源"
    }

    /**
     * 返回此包下面的源
     */
    @JvmStatic
    fun getSources(): List<TingShu> {
        HuanTingWang.c24e329b36b542f4adde9694d4b28a4r()
        return listOf(
            HuanTingWang,
//            JDLG,//已失效
            YouTuYueDu,
            YunTuYouSheng,
            WoTingPingShu,
            KuWo,
            KouDaiWeiKeTang,
            BoKanYouSheng,
            ZhongBanYouSheng,
            CCTV,
            YouShengXiaoShuoBa,
            ShengBoFM,
            IFish,
            TingChina,
            BiliBili,
            JingTing,
            XinMo,
            TingShu74,
            TingShuBao,
            //            MaLaTIngShu
//                YouYuKu,
//                HaiDao,
            SouGou,
            Mgting,
            AiTingShu,
            Wap520,
            LianTingWang,
            WoAiTingPingShu,
            IShuYinTingShu,
            I275,
            JiHe,
            HaiYangTingShu,
            M456TingShu,
            LiuTingWang,
            VBus
        )
    }
}