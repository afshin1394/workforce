package irancell.nwg.wfm

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

actual class FileDestination(private val file: File) {

    actual fun write(data: ByteArray) {
        FileOutputStream(file).use { it.write(data) }
    }

    actual fun writeStreamed(writeBlock: (buffer: (ByteArray) -> Unit) -> Unit) {
        FileOutputStream(file).use { outputStream ->
            writeBlock { chunk ->
                outputStream.write(chunk)
            }
        }
    }
}