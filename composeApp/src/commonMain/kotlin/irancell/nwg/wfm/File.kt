package irancell.nwg.wfm

expect class File(path: String) {
    fun sizeInMB(): Double
    fun readBytes(): ByteArray
    fun extension(): String
    fun exists(): Boolean
}