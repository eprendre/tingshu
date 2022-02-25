import org.junit.Test
import java.util.*

class GenerateUUID {
    @Test
    fun generateUUID() {
        repeat(10) {
            val uuid = UUID.randomUUID().toString().replace("-","")
            println(uuid)
        }
    }
}