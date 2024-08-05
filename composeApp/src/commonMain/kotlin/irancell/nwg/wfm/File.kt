package irancell.nwg.wfm

expect class File(path: String) {
    fun sizeInMB(): Int
    fun readBytes(): ByteArray
    fun extension(): String
}