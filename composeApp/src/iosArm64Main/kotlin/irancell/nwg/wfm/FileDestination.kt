package irancell.nwg.wfm

import platform.Foundation.*
import kotlin.coroutines.*

actual class FileDestination(private val path: String) {
    actual fun write(data: ByteArray) {
        val nsData = data.toNSData()
        nsData.writeToFile(path, true)
    }
}

fun ByteArray.toNSData(): NSData {
    return NSData.create(bytes = this, length = size.toULong())
}