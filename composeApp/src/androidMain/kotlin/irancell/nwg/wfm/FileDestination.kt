package irancell.nwg.wfm

import java.io.File
import java.io.FileOutputStream

actual class FileDestination(private val file: File) {
    actual fun write(data: ByteArray) {
        FileOutputStream(file).use { outputStream ->
            outputStream.write(data)
        }
    }
}