package irancell.nwg.wfm

actual fun GetFile(path: String): PlatformFile? {
    val fileManager = NSFileManager.defaultManager
    val filePath = path.toNSString()
    return if (fileManager.fileExistsAtPath(filePath)) {
        PlatformFile(path)
    } else {
        null
    }
}