package irancell.nwg.wfm

expect class FileDestination {
    fun write(data: ByteArray)
    fun writeStreamed(writeBlock: (buffer: (ByteArray) -> Unit) -> Unit)
}