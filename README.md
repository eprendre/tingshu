# 我的听书

本项目停止维护。大部分代码已失效，仅供想要自己写源的同学参考。

## 下载

* [蓝奏云](https://pan.lanzoux.com/b873905)

## 自定义源

请参考 `CustomSources` 一个纯 java/kotlin 项目，用 IDEA 打开即可，不需要 Android Studio 以及安卓环境。零基础的同学请搭建好 java 开发环境，比如安装 **jdk18**, 并且添加 **环境变量**。
app 里面已经集成了网络请求库`Fuel` 以及HTML解析器 `Jsoup`， 此自定义源项目最好直接使用这两个库，不要引入额外的第三方库。

### 第一步：重构目录名

需要保证目录名独一无二, 比如我们取名为 `sources_by_xxx`，后续皆用此名举例。
![alter folder name](art/sources1.jpg)

### 第二步：开始编写自定义源

在第一步的目录下面新建一个类继承`TingShu`，参考注释和代码示例编写相应的代码。附一份粗浅的代码执行逻辑图。

![process](art/sources5.png)

### 第三步：自定义源入口

把第二步编写好的一个或多个源添加至 `SourceEntry` 的 `getSources` 里， app 端会通过这个方法获取源。

### 第四步：打包 jar 文件

1. 在`gradle.properties`里面修改`MY_SOURCES_PACKAGE=sources_by_xxx`
1. 打开命令行，在项目根目录输入: `./gradlew jar`。 windows 平台：`gradlew.bat jar` 或者 `.\gradlew.bat jar`。不喜欢命令行的同学可以直接在IDEA右方找到Gradle->CustomSources->build->jar 双击。
1. 此时在项目目录/build/libs/ 里面出现 CustomSources-1.0-SNAPSHOT.jar。并生成 `sources_by_xxx.jar`, windows 系统还会生成一个 upload.bat 文件。

![jar](art/jar.png)

### 第五步：添加 sources_by_xxx.jar 包至 app


* 自动添加：运行 upload.bat 即可。（需要先配置好adb的环境变量，如果没有adb的同学可以去解压项目里的adb.zip，放到合适的地方并添加环境变量)

* 手动添加：把 jar 包移至手机 app 目录下: `/sdcard/Android/data/com.github.eprendre.tingshu/files/jars/`， app 会在启动时自动加载。

**订阅添加**: 写一个接口，然后在 app 的自定义源管理右上角添加。订阅方式的好处是源作者可以更轻松的维护源，只要在接口里修改版本号，app 每次启动时会自动检测更新。

接口返回内容举例：

```json
{
    "version": 27,
    "entry_package": "sources_by_eprendre",
    "download_url": "https://xxxxx.com/sources_by_eprendre.jar",
    "update_msg": "外置源兼容最新版",
    "support_url": ""
}
```

接口字段说明：

* `version`: 为数字类型，代表版本号。 app 以此来判断这个 jar 包是否有更新。
* `entry_package`: 为第一步提到的目录名，app下载 jar 包后也会自动命名为此名字。这是 app 找到相关类的关键。
* `download_url`: jar 包下载地址。
* `update_msg`: 更新信息。
* `support_url`: 此参数不为空时，在app长按订阅源将出现`支持`选项。源作者可以放自己的赞赏二维码图片链接，或者任意自己想放的链接。

接口或者 jar 包可在 github raw文件免费托管，不过国内用户有一定几率打不开 github 的链接 。

### 调试

app 将在最新版加入调试功能。
在源管理 -> 订阅 -> 选择一个源，长按 -> 调试，进入。

上方搜索框输入`cat`将自动调试分类相关逻辑， 输入其它关键词则调试搜索相关逻辑。

![debug](art/debug.png)
