package com.github.eprendre.sources_by_eprendre

import com.github.eprendre.tingshu.sources.AudioUrlDirectExtractor
import com.github.eprendre.tingshu.sources.AudioUrlExtractor
import com.github.eprendre.tingshu.sources.TingShu
import com.github.eprendre.tingshu.utils.*

object CCTV : TingShu(){
    private val liveList = listOf (
        "CCTV-1 综合" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv1_1/index.m3u8",
        "CCTV-2 财经" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv2_1/index.m3u8",
        "CCTV-3 综艺" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv3_1/index.m3u8",
        "CCTV-4 中文国际（亚）" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv4_1/index.m3u8",
        "CCTV-5 体育" to "https://cctv5txyh5c.liveplay.myqcloud.com/live/cdrmcctv5_1/index.m3u8",
        "CCTV-5+ 体育赛事" to "https://cctv5txyh5c.liveplay.myqcloud.com/live/cdrmcctv5plus_1/index.m3u8",
        "CCTV-6 电影" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv6_1/index.m3u8",
        "CCTV-7 国防军事" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv7_1/index.m3u8",
        "CCTV-8 电视剧" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv8_1/index.m3u8",
        "CCTV-9 纪录" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctvjilu_1/index.m3u8",
        "CCTV-10 科教" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv10_1/index.m3u8",
        "CCTV-11 戏曲" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv11_1/index.m3u8",
        "CCTV-12 社会与法" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv12_1/index.m3u8",
        "CCTV-13 新闻" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv13_1/index.m3u8",
        "CCTV-14 少儿" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctvchild_1/index.m3u8",
        "CCTV-15 音乐" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv15_1/index.m3u8",
        "CCTV-17 农业农村" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv17_1/index.m3u8",
        "CCTV-4 中文国际（欧）" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctveurope_1/index.m3u8",
        "CCTV-4 中文国际（美）" to "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctvamerica_1/index.m3u8"
    )

    private val list2 = listOf(
        "CCTV1综合" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225618/index.m3u8",
        "CCTV2财经" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225619/index.m3u8",
        "CCTV3综艺" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225634/index.m3u8",
        "CCTV4中文国际" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225621/index.m3u8",
        "CCTV5体育" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225633/index.m3u8",
        "CCTV5+体育赛事" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225649/index.m3u8",
        "CCTV6电影" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225632/index.m3u8",
        "CCTV7国防军事" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225624/index.m3u8",
        "CCTV8电视剧" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225631/index.m3u8",
        "CCTV9纪录" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225626/index.m3u8",
        "CCTV10科教" to "http://39.135.138.58:18890/PLTV/88888910/224/3221225627/index.m3u8",
        "CCTV11戏曲" to "http://39.135.138.58:18890/PLTV/88888910/224/3221225628/index.m3u8",
        "CCTV12社会与法" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225629/index.m3u8",
        "CCTV13新闻" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225638/index.m3u8",
        "CCTV14少儿" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225639/index.m3u8",
        "CCTV15音乐" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225641/index.m3u8",
        "CCTV17农业农村" to "http://39.135.138.60:18890/PLTV/88888910/224/3221225908/index.m3u8",
        "CCTV-3蓝光1" to "http://117.169.124.36:6610/ysten-businessmobile/live/cctv-3/1.m3u8",
        "CCTV-3蓝光2" to "http://111.63.117.13:6060/030000001000/CCTV-3/CCTV-3.m3u8",
        "CCTV-5+蓝光3" to "http://117.169.124.46:6410/ysten-businessmobile/live/hdcctv05plus/1.m3u8",
        "CCTV-6蓝光1" to "http://223.110.243.139/PLTV/3/224/3221225548/index.m3u8",
        "CCTV-6蓝光2" to "http://117.169.124.36:6610/ysten-businessmobile/live/cctv-6/1.m3u8",
        "CCTV-8蓝光1" to "http://117.169.124.36:6610/ysten-businessmobile/live/cctv-8/1.m3u8",
        "纯享4K" to "http://hwrr.jx.chinamobile.com:8080/PLTV/88888888/224/3221225786/index.m3u8",
        "爱上4K" to "http://39.134.24.220/PLTV/88888888/224/3221226005/index.m3u8",
        "CCTV2 HD" to "http://117.169.120.160:8080/live/cctv-2/1.m3u8",
        "CCTV-4高清@1" to "http://117.169.120.140:8080/live/cctv-4/.m3u8",
        "CCTV-11高清" to "http://117.169.120.140:8080/live/cctv-11/.m3u8",
        "CCTV-13高清@1" to "http://117.169.120.140:8080/live/cctv-13/.m3u8",
        "CCTV-15高清" to "http://117.169.120.140:8080/live/cctv-15/.m3u8",
        "欢笑剧场4K1" to "http://223.110.243.212/PLTV/3/224/3221227715/index.m3u8",
        "欢笑剧场4K2" to "http://39.134.39.38/PLTV/88888888/224/3221226203/index.m3u8",
        "冬奥纪实" to "http://117.169.120.160:8080/live/HD-8000k-1080P-beijingjishi/1.m3u8",
        "天津卫视蓝光1" to "http://39.134.66.66/PLTV/88888888/224/3221225665/index.m3u8",
        "东方卫视蓝光1" to "http://39.135.138.59:18890/PLTV/88888910/224/3221225659/index.m3u8",
        "浙江卫视蓝光2" to "http://223.110.243.173/PLTV/3/224/3221227215/index.m3u8",
        "江苏卫视蓝光1" to "http://39.134.39.39/PLTV/88888888/224/3221226157/index.m3u8",
        "东南卫视蓝光1" to "http://117.169.124.37:6610/ysten-businessmobile/live/dongnanstv/yst.m3u8",
        "东南卫视蓝光3" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225657/index.m3u8",
        "湖北卫视蓝光3" to "http://223.110.243.171/PLTV/3/224/3221227211/index.m3u8",
        "深圳卫视蓝光1" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225741/index.m3u8",
        "深圳卫视蓝光2" to "http://223.110.243.171/PLTV/3/224/3221227217/index.m3u8",
        "辽宁卫视蓝光3" to "http://223.110.245.145/ott.js.chinamobile.com/PLTV/3/224/3221227410/index.m3u8",
        "龙江卫视蓝光1" to "http://39.134.116.30:8080/PLTV/88888910/224/3221225690/index.m3u8",
        "河南卫视超清2" to "http://223.110.245.157/ott.js.chinamobile.com/PLTV/3/224/3221225815/index.m3u8",
        "云南卫视超清1" to "http://223.110.245.159/ott.js.chinamobile.com/PLTV/3/224/3221225838/index.m3u8",
        "甘肃卫视蓝光1" to "http://39.134.39.39/PLTV/88888888/224/3221226240/index.m3u8",
        "宁夏卫视超清1" to "http://223.110.245.151/ott.js.chinamobile.com/PLTV/3/224/3221225842/index.m3u8",
        "宁夏卫视超清2" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225726/index.m3u8",
        "新疆卫视高清2" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225725/index.m3u8",
        "西藏卫视高清2" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225723/index.m3u8",
        "内蒙卫视蓝光1" to "http://live.m2oplus.nmtv.cn/1/sd/live.m3u8",
        "内蒙卫视超清2" to "http://223.110.245.161/ott.js.chinamobile.com/PLTV/3/224/3221225836/index.m3u8",
        "南方卫视高清2" to "http://223.110.245.153/ott.js.chinamobile.com/PLTV/3/224/3221227005/index.m3u8",
        "CHC动作电影" to "http://125.210.152.18:9090/live/DZDYHD_H265.m3u8",
        "珠峰基站4K" to "http://117.136.154.98/PLTV/88888888/224/3221225753/index.m3u8",
        "东方影视FHD" to "http://140.207.241.2:8080/live/program/live/dsjpdhd/4000000/mnf.m3u8",
        "华侨城FHD" to "http://117.156.28.119/270000001111/1110000028/index.m3u8",
        "数码时代高清" to "http://yd-m-l.cztv.com/channels/lantian/channel012/1080p.m3u8",
        "NewTV超级体育FHD" to "http://39.134.66.66/PLTV/88888888/224/3221225635/index.m3u8",
        "NewTV爱情喜剧" to "http://39.134.66.66/PLTV/88888888/224/3221225533/index.m3u8",
        "NewTV动画王国" to "http://223.110.245.161/ott.js.chinamobile.com/PLTV/3/224/3221225555/index.m3u8",
        "IPTV5+蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226273/index.m3u8",
        "SiTV动漫秀场FHD" to "http://39.134.39.39/PLTV/88888888/224/3221226197/index.m3u8",
        "SiTV都市剧场FHD" to "http://39.134.39.39/PLTV/88888888/224/3221226176/index.m3u8",
        "SiTV欢笑剧场FHD" to "http://39.134.39.39/PLTV/88888888/224/3221226203/index.m3u8",
        "SiTV极速汽车FHD" to "http://39.134.39.39/PLTV/88888888/224/3221226195/index.m3u8",
        "SiTV新视觉 1080" to "http://140.207.241.2:8080/live/program/live/xsjhd/4000000/mnf.m3u8",
        "SiTV动漫秀场 1080" to "http://140.207.241.2:8080/live/program/live/dmxchd/4000000/mnf.m3u8",
        "SiTV游戏风云 1080" to "http://140.207.241.2:8080/live/program/live/yxfyhd/4000000/mnf.m3u8",
        "SiTV劲爆体育 1080" to "http://140.207.241.2:8080/live/program/live/jbtyhd/4000000/mnf.m3u8",
        "SiTV魅力足球 1080" to "http://140.207.241.2:8080/live/program/live/mlyyhd/4000000/mnf.m3u8",
        "SiTV欢笑剧场 1080" to "http://140.207.241.2:8080/live/program/live/hxjchd/4000000/mnf.m3u8",
        "SiTV极速汽车 1080" to "http://140.207.241.2:8080/live/program/live/jsqchd/4000000/mnf.m3u8",
        "SiTV生活时尚 1080" to "http://140.207.241.2:8080/live/program/live/shsshd/4000000/mnf.m3u8",
        "SiTV全纪实 1080" to "http://140.207.241.2:8080/live/program/live/qjshd/4000000/mnf.m3u8",
        "SiTV都市剧场 1080" to "http://140.207.241.2:8080/live/program/live/dsjchd/4000000/mnf.m3u8",
        "SiTV幸福彩 1080" to "http://140.207.241.2:8080/live/program/live/xfchd/4000000/mnf.m3u8",
        "SiTV法治天地 540" to "http://140.207.241.2:8080/live/program/live/fztd/1300000/mnf.m3u8",
        "SiTV金色频道 540" to "http://140.207.241.2:8080/live/program/live/jingsepd/1300000/mnf.m3u8",
        "SiTV七彩戏剧 540" to "http://140.207.241.2:8080/live/program/live/qcxj/1300000/mnf.m3u8",
        "SiTV东方财经 540" to "http://140.207.241.2:8080/live/program/live/dfcj/1300000/mnf.m3u8",
        "北京纪实" to "http://39.134.115.163:8080/PLTV/88888910/224/3221225676/index.m3u8",
        "上海教育" to "http://live.setv.sh.cn/slive/shedu02_1200k.m3u8",
        "上海新闻" to "http://183.207.255.188/live/program/live/xwzhhd/4000000/mnf.m3u8",
        "上海影视" to "http://183.207.255.188/live/program/live/dsjpdhd/4000000/mnf.m3u8",
        "上海外语" to "http://183.207.255.188/live/program/live/wypdhd/4000000/mnf.m3u8",
        "上海都市" to "http://183.207.255.188/live/program/live/ylpdhd/4000000/mnf.m3u8",
        "上海法制" to "http://183.207.255.188/live/program/live/fztd/1300000/mnf.m3u8",
        "上海财经" to "http://183.207.255.188/live/program/live/dfcj/1300000/mnf.m3u8",
        "上海戏曲" to "http://183.207.255.188/live/program/live/qcxj/1300000/mnf.m3u8",
        "上海生活" to "http://183.207.255.188/live/program/live/shsshd/4000000/mnf.m3u8",
        "上海纪实" to "http://183.207.255.188/live/program/live/jspdhd/4000000/mnf.m3u8",
        "甘肃都市蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226248/index.m3u8",
        "甘肃公共蓝光" to "http://39.134.39.37/PLTV/88888888/224/3221226250/index.m3u8",
        "甘肃经济蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226252/index.m3u8",
        "甘肃少儿蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226289/index.m3u8",
        "甘肃文化影视蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226287/index.m3u8",
        "兰州公共频道蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226244/index.m3u8",
        "兰州生活经济蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226285/index.m3u8",
        "兰州新闻综合蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226242/index.m3u8",
        "兰州综艺体育蓝光" to "http://39.134.39.39/PLTV/88888888/224/3221226246/index.m3u8",
        "浙江柯桥综合" to "http://live.scbtv.cn/hls/news/index.m3u8",
        "浙江柯桥时尚台" to "http://live.scbtv.cn/hls/qfc/index.m3u8",
        "浙江影视台" to "http://hw-m-l.cztv.com/channels/lantian/channel05/1080p.m3u8",
        "浙江少儿台" to "http://hw-m-l.cztv.com/channels/lantian/channel08/1080p.m3u8",
        "浙江教育台" to "http://hw-m-l.cztv.com/channels/lantian/channel04/1080p.m3u8",
        "浙江钱江台" to "http://hw-m-l.cztv.com/channels/lantian/channel02/1080p.m3u8",
        "浙江休闲台" to "http://hw-m-l.cztv.com/channels/lantian/channel06/1080p.m3u8",
        "浙江经济台" to "http://hw-m-l.cztv.com/channels/lantian/channel03/1080p.m3u8",
        "浙江新闻台" to "http://hw-m-l.cztv.com/channels/lantian/channel07/1080p.m3u8",
        "浙江留学世界台" to "http://hw-m-l.cztv.com/channels/lantian/channel09/1080p.m3u8",
        "浙江舟山综合台" to "http://live.wifizs.cn/xwzh/sd/live.m3u8",
        "浙江舟山生活台" to "http://live.wifizs.cn/ggsh/sd/live.m3u8",
        "浙江绍兴综合台" to "http://live.shaoxing.com.cn/video/s10001-sxtv1/index.m3u8",
        "浙江绍兴影视台" to "http://live.shaoxing.com.cn/video/s10001-sxtv3/index.m3u8",
        "浙江绍兴公共台" to "http://live.shaoxing.com.cn/video/s10001-sxtv2/index.m3u8",
        "湖北孝感新闻综合" to "http://xiaogan.live.cjyun.org/video/s10139-xg/index.m3u8",
        "湖北孝感公共频道" to "http://xiaogan.live.cjyun.org/video/s10139-shpd/index.m3u8",
        "湖北大悟综合" to "http://yunshangdawu.live.tempsource.cjyun.org/videotmp/s10129-dwzhpd.m3u8",
        "湖北大冶一套" to "http://dayeyun.live.tempsource.cjyun.org/videotmp/s10102-TC1T.m3u8",
        "湖北大冶二套" to "http://dayeyun.live.tempsource.cjyun.org/videotmp/s10102-TC2T.m3u8",
        "湖北嘉鱼新闻综合" to "http://jiayu.live.tempsource.cjyun.org/videotmp/s10131-jyzh.m3u8",
        "湖北鹤峰综合频道" to "http://hefeng.live.tempsource.cjyun.org/videotmp/s10100-hftv.m3u8",
        "湖北广水新闻频道" to "http://guangshui.live.tempsource.cjyun.org/videotmp/s10146-GSXW.m3u8",
        "湖南娄底综合台" to "http://mms.ldntv.cn:1935/live/zonghe/playlist.m3u8",
        "湖南湘潭综合台" to "http://live.hnxttv.com:9601/live/xwzh/800K/tzwj_video.m3u8",
        "江苏吴江综合台" to "http://30515.hlsplay.aodianyun.com/lms_30515/tv_channel_239.m3u8",
        "江苏张家港综合" to "http://3gvod.zjgonline.com.cn:1935/live/xinwenzonghe2/playlist.m3u8",
        "江苏张家港民生" to "http://3gvod.zjgonline.com.cn:1935/live/shehuishenghuo2/playlist.m3u8",
        "江苏连云港综合" to "http://live.lyg1.com/zhpd/sd/live.m3u8",
        "江苏连云港公共" to "http://live.lyg1.com/ggpd/sd/live.m3u8",
        "江苏淮安综合" to "http://stream.habctv.com/xwzh/sd/live.m3u8",
        "江苏淮安公共" to "http://stream.habctv.com/hagg/sd/live.m3u8",
        "江苏淮安影视" to "http://stream.habctv.com/ysyl/sd/live.m3u8",
        "安徽六安综合台" to "http://live.china-latv.com/channel1/sd/live.m3u8",
        "安徽淮北综合台" to "http://live.0561rtv.cn/xwzh/hd/live.m3u8",
        "安徽淮北公共台" to "http://live.0561rtv.cn/ggpd/hd/live.m3u8",
        "安徽铜陵综合台" to "http://dstpush1.retalltech.com/app/stream1.m3u8",
        "安徽铜陵公共台" to "http://dstpush1.retalltech.com/app/stream2.m3u8",
        "安徽东至综合台" to "http://223.247.33.124:1935/live/zonghe/playlist.m3u8",
        "安徽东至影视台" to "http://223.247.33.124:1935/live/yingshi/playlist.m3u8",
        "重庆万州综合台" to "http://123.146.162.24:8017/iTXwrGs/800/live.m3u8",
        "重庆万州影视台" to "http://wanzhoulive.cbg.cn:8017/d4ceB1a/1000/live.m3u8",
        "重庆万盛综合台" to "http://qxlmlive.cbg.cn:1935/app_2/ls_40.stream/playlist.m3u8",
        "云南楚雄公共台" to "http://hwzbout.yunshicloud.com/gbqei5/12661u.m3u8",
        "云南丽江公共台" to "http://hwzbout.yunshicloud.com/mj1170/06qk26.m3u8",
        "云南西双版纳一" to "http://file.xsbnrtv.cn/vms/videos/nmip-media/channellive/channel1/playlist.m3u8",
        "云南西双版纳二" to "http://file.xsbnrtv.cn/vms/videos/nmip-media/channellive/channel3/playlist.m3u8",
        "四川影视文化" to "http://scgctvshow.sctv.com/hdlive/sctv5/1.m3u8",
        "四川峨眉电影1" to "http://scgctvshow.sctv.com/hdlive/emei/1.m3u8",
        "四川公共乡村" to "http://scgctvshow.sctv.com/hdlive/sctv9/1.m3u8",
        "四川经济" to "http://scgctvshow.sctv.com/hdlive/sctv3/1.m3u8",
        "四川文化旅游HD" to "http://scgctvshow.sctv.com/hdlive/sctv2/1.m3u8",
        "四川新闻频道HD" to "http://scgctvshow.sctv.com/hdlive/sctv4/1.m3u8",
        "福建厦门第一台" to "http://cctvtxyh5ca.liveplay.myqcloud.com/cstv/xiamen1_2/index.m3u8",
        "陕西西安白鸽台" to "http://stream2.xiancity.cn/xatv2/sd/live.m3u8",
        "陕西西安影视台" to "http://stream2.xiancity.cn/xatv4/sd/live.m3u8",
        "陕西西安丝路台" to "http://stream2.xiancity.cn/xatv5/sd/live.m3u8",
        "陕西西安文化影视" to "http://39.134.18.65/dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221226369/index.m3u8",
        "陕西西安丝路频道" to "http://39.134.18.65/dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221226370/index.m3u8",
        "山西朔州新闻台" to "http://stream.sxsztv.com/live4/sd/live.m3u8",
        "山西黄河" to "http://live3.sxrtv.com/flvss?bitrate=512000&channel=HuangHeNews&start=0&provider=www.tvmining.com",
        "山西经济" to "http://live3.sxrtv.com/flvss?bitrate=800000&channel=Shan1XiFinance&start=0&provider=www.tvmining.com",
        "山西影视" to "http://live2.sxrtv.com/flvss?bitrate=800000&channel=Shan1XiFilm&start=0&provider=www.tvmining.com",
        "山西科教" to "http://live3.sxrtv.com/flvss?bitrate=800000&channel=Shan1XiEdu&start=0&provider=www.tvmining.com",
        "山西少儿" to "http://live2.sxrtv.com/flvss?bitrate=800000&channel=Shan1XiChild&start=0&provider=www.tvmining.com",
        "山东德州综合台" to "http://video.dztv.tv:1935/live/xwzh_gq/playlist.m3u8",
        "山东德州公共台" to "http://video.dztv.tv:1935/live/dzgg_gq/playlist.m3u8",
        "深圳蛇口" to "http://218.17.99.211:5080/hls/ttsw6ccn.m3u8",
        "吉林都市" to "http://stream1.jlntv.cn/dspd/sd/live.m3u8?_upt=4f4899d21531575987",
        "吉林生活" to "http://stream1.jlntv.cn/shpd/sd/live.m3u8?_upt=212b47d81531576020",
        "吉林影视" to "http://stream1.jlntv.cn/yspd/sd/live.m3u8",
        "吉林乡村" to "http://stream1.jlntv.cn/xcpd/sd/live.m3u8",
        "吉林公共" to "http://stream1.jlntv.cn/ggpd/sd/live.m3u8",
        "吉林7频道" to "http://stream1.jlntv.cn/fzpd/sd/live.m3u8",
        "广东点掌财经" to "http://cclive2.aniu.tv/live/anzb.m3u8",
        "广东潮州综合" to "http://dslive.grtn.cn/czzh/sd/live.m3u8"
    )

    override fun getSourceId(): String {
        return "150173b652964f71806264c07245587d"
    }

    override fun getUrl(): String {
        return "https://tv.cctv.com/"
    }

    override fun getName(): String {
        return "电视直播"
    }

    override fun getDesc(): String {
        return "推荐指数:2星 ⭐⭐\n直播，请不要开加速播放。"
    }

    override fun search(keywords: String, page: Int): Pair<List<Book>, Int> {
        return Pair(emptyList(), 1)
    }

    override fun isSearchable(): Boolean {
        return false
    }

    override fun isCacheable(): Boolean {
        return false
    }

    override fun getAudioUrlExtractor(): AudioUrlExtractor {
        return AudioUrlDirectExtractor
    }

    override fun getCategoryMenus(): List<CategoryMenu> {
        val menu1 = CategoryMenu("电视", listOf(
            CategoryTab("直播", "liveplay")
        ))
        return listOf(menu1)
    }

    override fun getCategoryList(url: String): Category {
        val currentPage = 1
        val totalPage = 1
        val list = ArrayList<Book>()
        list.add(Book("", "cctv", "央视(不含画面)", "", "").apply {
            this.sourceId = getSourceId()
        })
        list.add(Book("", "cctv2", "电视直播", "", "").apply {
            this.sourceId = getSourceId()
        })
        return Category(list, currentPage, totalPage, url, "")
    }

    override fun getBookDetailInfo(bookUrl: String, loadEpisodes: Boolean, loadFullPages: Boolean): BookDetail {
        val episodes = mutableListOf<Episode>()
        if (loadEpisodes) {
            if (bookUrl == "cctv") {
                liveList.forEach {
                    episodes.add(Episode(it.first, it.second))
                }
            } else if (bookUrl == "cctv2") {
                list2.forEach {
                    episodes.add(Episode(it.first, it.second))
                }
            }
        }
        return BookDetail(episodes)
    }
}