package irancell.nwg.wfm

expect class PlatformFile(path: String) {
    fun readBytes(): ByteArray
}