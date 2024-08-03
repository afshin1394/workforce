package irancell.nwg.wfm
import java.io.File
actual fun GetFile(path: String): PlatformFile? {
    val file = File(path)
    return if (file.exists()) {
        PlatformFile(path)
    } else {
        null
    }
}

