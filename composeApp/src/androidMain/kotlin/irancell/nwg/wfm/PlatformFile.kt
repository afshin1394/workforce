package irancell.nwg.wfm
import java.io.File

actual class PlatformFile actual constructor(private val path: String) {
    private val file = File(path)

    actual fun readBytes(): ByteArray = file.readBytes()
}
