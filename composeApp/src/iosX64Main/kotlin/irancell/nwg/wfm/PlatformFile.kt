package irancell.nwg.wfm

actual class PlatformFile(private val path: String) {
    private val filePath = path.toNSString()

    actual fun readBytes(): ByteArray {
        val data = NSData.dataWithContentsOfFile(filePath)
        return data?.let {
            it.bytes?.readBytes(it.length.toInt())
        } ?: ByteArray(0)
    }
}