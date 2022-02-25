import org.jsoup.Connection

//仅供测试用，如果放到正式代码里，请改成 config
fun Connection.testConfig(isDesktop: Boolean = false): Connection {
    return if (isDesktop) {
        this.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36")
    } else {
        this.userAgent("Mozilla/5.0 (Android 4.4; Mobile; rv:46.0) Gecko/46.0 Firefox/46.0")
    }
}
