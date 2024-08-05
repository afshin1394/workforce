package irancell.nwg.wfm
import java.io.File as JavaFile




actual class File actual constructor(private val path: String) {
    private val file = JavaFile(path)

    actual fun readBytes(): ByteArray {
        return  file.readBytes()

    }
    actual fun sizeInMB(): Int {
        //return (file.length() / (1024 * 1024)).toInt()
        return file.length().toInt()
    }

    actual fun extension(): String {
        return file.name.substringAfterLast('.', "")
    }
}